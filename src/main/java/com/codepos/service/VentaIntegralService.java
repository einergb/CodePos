package com.codepos.service;

import com.codepos.config.ConexionBD;
import com.codepos.dao.InventarioDAO;
import com.codepos.dao.MovimientoInventarioDAO;
import com.codepos.dao.PagoDAO;
import com.codepos.dao.VentaDAO;
import com.codepos.dao.VentaDetalleDAO;
import com.codepos.dto.ResultadoVenta;
import com.codepos.enums.TipoMovimientoInventario;
import com.codepos.model.Inventario;
import com.codepos.model.Pago;
import com.codepos.model.Venta;
import com.codepos.model.VentaDetalle;
import com.codepos.util.CalculadoraVentaUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Servicio integral de ventas POS.
 *
 * Controla la transacción completa:
 *
 * - Venta
 * - Detalles
 * - Inventario
 * - Kardex
 * - Pago
 *
 * Todo dentro de una única transacción PostgreSQL.
 *
 * IMPORTANTE (pendiente fuera de este archivo):
 *
 * VentaDAO.marcarComoPagada() actualiza el estado sin
 * verificar "AND estado = 'REGISTRADA'" en el WHERE.
 * Este servicio no puede cerrar del todo esa ventana de
 * condición de carrera sin una consulta adicional dentro
 * de la misma transacción (VentaDAO no expone todavía un
 * buscarPorId(Connection, ...)). Ver nota en registrarVenta().
 */
public class VentaIntegralService {


    private final VentaDAO ventaDAO;

    private final VentaDetalleDAO ventaDetalleDAO;

    private final InventarioDAO inventarioDAO;

    private final MovimientoInventarioDAO movimientoDAO;

    private final PagoDAO pagoDAO;


    public VentaIntegralService() {

        this.ventaDAO = new VentaDAO();
        this.ventaDetalleDAO = new VentaDetalleDAO();
        this.inventarioDAO = new InventarioDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
        this.pagoDAO = new PagoDAO();
    }


    /**
     * Registra una venta completa (venta integral POS).
     *
     * Flujo:
     *
     * 1. Validaciones
     * 2. Calcular totales
     * 3. Crear venta
     * 4. Crear detalles + inventario + kardex
     * 5. Registrar pago
     * 6. Marcar venta como PAGADA
     * 7. Confirmar transacción (o rollback total ante cualquier error)
     */
    public Long registrarVenta(
            Venta venta,
            List<VentaDetalle> detalles,
            Pago pago) {

        validarVenta(venta);
        validarDetalles(detalles);
        validarPago(pago);

        ResultadoVenta resultado =
                CalculadoraVentaUtil.calcularVenta(detalles);

        venta.setSubtotal(resultado.getSubtotal());
        venta.setDescuento(resultado.getDescuento());
        venta.setImpuesto(resultado.getImpuesto());
        venta.setTotal(resultado.getTotal());

        /*
         * El pago debe CUBRIR el total, no coincidir exactamente.
         * Un pago mayor es válido y representa cambio a entregar
         * (documentado en las pruebas: Total $250.000, Pagado
         * $300.000, Cambio $50.000). Solo se rechaza un pago
         * insuficiente.
         */
        if (pago.getMonto().compareTo(resultado.getTotal()) < 0) {

            throw new IllegalArgumentException(
                    "El pago no cubre el total de la venta"
            );
        }

        try (Connection connection = ConexionBD.conectar()) {

            connection.setAutoCommit(false);

            try {

                venta.setEstado("REGISTRADA");

                Long ventaId = ventaDAO.crear(connection, venta);

                if (ventaId == null || ventaId <= 0) {

                    throw new IllegalStateException(
                            "No se pudo crear la venta"
                    );
                }

                /*
                 * =====================================================
                 * PROCESAMIENTO DE INVENTARIO + DETALLES
                 * =====================================================
                 *
                 * Por cada producto:
                 *
                 * 1. Buscar inventario (bloqueado con FOR UPDATE).
                 * 2. Validar que esté activo y tenga stock suficiente.
                 * 3. Descontar existencia y registrar movimiento Kardex.
                 * 4. Guardar el detalle de venta.
                 *
                 * La verificación de "productos repetidos" ya se hizo
                 * en validarDetalles(); no se repite aquí.
                 */
                for (VentaDetalle detalle : detalles) {

                    prepararDetalle(detalle, ventaId);

                    Inventario inventario = validarInventario(
                            connection,
                            venta.getEmpresaId(),
                            venta.getSucursalId(),
                            detalle
                    );

                    inventarioDAO.descontarStock(
                            connection,
                            inventario.getId(),
                            detalle.getCantidad()
                    );

                    movimientoDAO.registrarMovimiento(
                            connection,
                            venta.getEmpresaId(),
                            venta.getSucursalId(),
                            detalle.getProductoId(),
                            TipoMovimientoInventario.VENTA,
                            detalle.getCantidad(),
                            "Salida por venta POS",
                            "VENTA",
                            ventaId,
                            venta.getAuthUserId()
                    );

                    Long detalleId = ventaDetalleDAO.crear(connection, detalle);

                    if (detalleId == null || detalleId <= 0) {

                        throw new IllegalStateException(
                                "No fue posible registrar detalle de venta"
                        );
                    }
                }

                /*
                 * =====================================================
                 * REGISTRO DEL PAGO
                 * =====================================================
                 */

                pago.setVentaId(ventaId);

                Long pagoId = pagoDAO.crear(connection, pago);

                if (pagoId == null || pagoId <= 0) {

                    throw new IllegalStateException(
                            "No fue posible registrar el pago"
                    );
                }

                /*
                 * =====================================================
                 * ACTUALIZAR ESTADO DE VENTA: REGISTRADA -> PAGADA
                 *
                 * NOTA: VentaDAO.marcarComoPagada() no verifica todavía
                 * "estado = 'REGISTRADA'" en su WHERE. Dentro de esta
                 * misma transacción no hay condición de carrera posible
                 * con OTRA venta (cada fila está aislada), pero sí queda
                 * abierta la posibilidad de marcar como PAGADA una venta
                 * que, en teoría, ya estuviera ANULADA si alguna vez se
                 * permite anular antes de pagar. Recomendado: agregar
                 * "AND estado = 'REGISTRADA'" al UPDATE de VentaDAO y
                 * verificar filas afectadas == 1, igual que ya se hace
                 * en InventarioDAO.descontarStock().
                 */
                ventaDAO.marcarComoPagada(
                        connection,
                        venta.getEmpresaId(),
                        ventaId
                );

                connection.commit();

                return ventaId;

            } catch (Exception e) {

                /*
                 * Rollback total: venta, detalles, inventario,
                 * kardex y pago quedan revertidos juntos.
                 */
                connection.rollback();

                throw new RuntimeException(
                        "Error registrando venta integral POS. "
                                + "Se realizó rollback completo.",
                        e
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error de conexión registrando venta integral POS",
                    e
            );
        }
    }


    /*
     * =====================================================
     * VALIDACIONES
     * =====================================================
     */

    private void validarVenta(Venta venta) {

        if (venta == null) {

            throw new IllegalArgumentException(
                    "La venta no puede ser nula"
            );
        }

        if (venta.getEmpresaId() == null || venta.getEmpresaId() <= 0) {

            throw new IllegalArgumentException(
                    "La empresa es obligatoria"
            );
        }

        if (venta.getSucursalId() == null || venta.getSucursalId() <= 0) {

            throw new IllegalArgumentException(
                    "La sucursal es obligatoria"
            );
        }

        if (venta.getNumero() == null || venta.getNumero().isBlank()) {

            throw new IllegalArgumentException(
                    "El número de venta es obligatorio"
            );
        }

        venta.setNumero(venta.getNumero().trim());

        if (venta.getClienteId() != null && venta.getClienteId() <= 0) {

            throw new IllegalArgumentException(
                    "Cliente inválido"
            );
        }

        if (venta.getAuthUserId() != null && venta.getAuthUserId() <= 0) {

            throw new IllegalArgumentException(
                    "Usuario autenticado inválido"
            );
        }
    }


    private void validarDetalles(List<VentaDetalle> detalles) {

        if (detalles == null || detalles.isEmpty()) {

            throw new IllegalArgumentException(
                    "La venta debe tener mínimo un detalle"
            );
        }

        Set<Long> productos = new HashSet<>();

        for (VentaDetalle detalle : detalles) {

            if (detalle == null) {

                throw new IllegalArgumentException(
                        "Existe un detalle inválido"
                );
            }

            if (detalle.getProductoId() == null || detalle.getProductoId() <= 0) {

                throw new IllegalArgumentException(
                        "El producto es obligatorio"
                );
            }

            if (!productos.add(detalle.getProductoId())) {

                throw new IllegalArgumentException(
                        "No se permiten productos repetidos en una venta"
                );
            }

            if (detalle.getCantidad() == null
                    || detalle.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero"
                );
            }

            if (detalle.getPrecioVenta() == null
                    || detalle.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "El precio de venta debe ser mayor que cero"
                );
            }
        }
    }


    private void validarPago(Pago pago) {

        if (pago == null) {

            throw new IllegalArgumentException(
                    "El pago es obligatorio"
            );
        }

        if (pago.getMetodo() == null || pago.getMetodo().isBlank()) {

            throw new IllegalArgumentException(
                    "El método de pago es obligatorio"
            );
        }

        if (pago.getMonto() == null
                || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto del pago debe ser mayor que cero"
            );
        }
    }


    /*
     * =====================================================
     * VALIDACIÓN INVENTARIO
     * =====================================================
     */

    private Inventario validarInventario(
            Connection connection,
            Long empresaId,
            Long sucursalId,
            VentaDetalle detalle) {

        Inventario inventario = inventarioDAO.buscarPorProducto(
                connection,
                empresaId,
                sucursalId,
                detalle.getProductoId()
        );

        if (inventario == null) {

            throw new IllegalStateException(
                    "No existe inventario para el producto "
                            + detalle.getProductoId()
            );
        }

        if (!Boolean.TRUE.equals(inventario.getActivo())) {

            throw new IllegalStateException(
                    "El inventario del producto está inactivo"
            );
        }

        if (inventario.getCantidad().compareTo(detalle.getCantidad()) < 0) {

            throw new IllegalStateException(
                    "Stock insuficiente para el producto "
                            + detalle.getProductoId()
            );
        }

        return inventario;
    }


    /*
     * =====================================================
     * PREPARAR DETALLE
     * =====================================================
     *
     * En el flujo normal, CalculadoraVentaUtil.calcularVenta()
     * ya dejó cantidad/precio/descuento/impuesto/subtotal
     * calculados y coherentes en cada detalle antes de llegar
     * aquí. Este método solo fija el ventaId y actúa como
     * resguardo defensivo si algún detalle llegara sin subtotal.
     */

    private void prepararDetalle(VentaDetalle detalle, Long ventaId) {

        detalle.setVentaId(ventaId);

        if (detalle.getDescuento() == null) {

            detalle.setDescuento(BigDecimal.ZERO);
        }

        if (detalle.getImpuesto() == null) {

            detalle.setImpuesto(BigDecimal.ZERO);
        }

        if (detalle.getSubtotal() == null) {

            BigDecimal subtotal =
                    detalle.getPrecioVenta().multiply(detalle.getCantidad());

            detalle.setSubtotal(subtotal);
        }
    }
}