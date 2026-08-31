package com.codepos.service;

import com.codepos.config.ConexionBD;
import com.codepos.dao.InventarioDAO;
import com.codepos.dao.MovimientoInventarioDAO;
import com.codepos.dao.PagoDAO;
import com.codepos.dao.VentaDAO;
import com.codepos.dao.VentaDetalleDAO;
import com.codepos.model.Inventario;
import com.codepos.model.Pago;
import com.codepos.model.Venta;
import com.codepos.model.VentaDetalle;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

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
     * Registra una venta completa dentro de una
     * única transacción PostgreSQL.
     *
     * La operación incluye:
     *
     * 1. Venta
     * 2. Detalles
     * 3. Descuento de inventario
     * 4. Movimiento de inventario
     * 5. Pago
     *
     * Si cualquier operación falla, toda la transacción
     * se revierte mediante ROLLBACK.
     *
     * @param venta venta principal
     * @param detalles productos vendidos
     * @param pago pago de la venta
     *
     * @return ID de la venta creada
     */
    public Long registrarVenta(
            Venta venta,
            List<VentaDetalle> detalles,
            Pago pago) {

        validarVenta(venta);
        validarDetalles(detalles);
        validarPago(pago);

        try (
                Connection connection =
                        ConexionBD.conectar()
        ) {

            /*
             * Desde este punto todas las operaciones
             * utilizan la misma conexión.
             */
            connection.setAutoCommit(false);

            try {

                /*
                 * =====================================================
                 * 1. CREAR VENTA
                 * =====================================================
                 */

                venta.setEstado("REGISTRADA");

                Long ventaId =
                        ventaDAO.crear(
                                connection,
                                venta
                        );

                /*
                 * =====================================================
                 * 2. PROCESAR DETALLES E INVENTARIO
                 * =====================================================
                 */

                for (VentaDetalle detalle : detalles) {

                    validarDetalle(detalle);

                    /*
                     * Asociamos el detalle con la venta
                     * recién creada.
                     */
                    detalle.setVentaId(ventaId);

                    /*
                     * =================================================
                     * Buscar y bloquear inventario
                     * =================================================
                     *
                     * FOR UPDATE garantiza que otro proceso
                     * no pueda modificar esta fila mientras
                     * nuestra transacción esté activa.
                     */
                    Inventario inventario =
                            inventarioDAO.buscarPorProducto(
                                    connection,
                                    venta.getEmpresaId(),
                                    venta.getSucursalId(),
                                    detalle.getProductoId()
                            );

                    if (inventario == null) {

                        throw new IllegalArgumentException(
                                "No existe inventario para el producto: "
                                        + detalle.getProductoId()
                        );
                    }

                    if (!Boolean.TRUE.equals(
                            inventario.getActivo()
                    )) {

                        throw new IllegalStateException(
                                "El inventario del producto está inactivo: "
                                        + detalle.getProductoId()
                        );
                    }

                    /*
                     * =================================================
                     * Verificar stock
                     * =================================================
                     */

                    if (inventario.getCantidad()
                            .compareTo(
                                    detalle.getCantidad()
                            ) < 0) {

                        throw new IllegalStateException(
                                "Stock insuficiente para el producto: "
                                        + detalle.getProductoId()
                        );
                    }

                    /*
                     * =================================================
                     * Crear detalle
                     * =================================================
                     */

                    ventaDetalleDAO.crear(
                            connection,
                            detalle
                    );

                    /*
                     * =================================================
                     * Descontar inventario
                     * =================================================
                     */

                    inventarioDAO.descontarStock(
                            connection,
                            inventario.getId(),
                            detalle.getCantidad()
                    );

                    /*
                     * =================================================
                     * Registrar movimiento de salida
                     * =================================================
                     */

                    movimientoDAO.registrarMovimiento(
                            connection,
                            venta.getEmpresaId(),
                            venta.getSucursalId(),
                            detalle.getProductoId(),
                            "SALIDA",
                            detalle.getCantidad(),
                            "Venta",
                            "VENTA",
                            ventaId,
                            venta.getAuthUserId()
                    );
                }

                /*
                 * =====================================================
                 * 3. REGISTRAR PAGO
                 * =====================================================
                 */

                pago.setVentaId(ventaId);

                /*
                 * Si no se especificó usuario en el pago,
                 * heredamos el usuario de la venta.
                 */
                if (pago.getAuthUserId() == null) {

                    pago.setAuthUserId(
                            venta.getAuthUserId()
                    );
                }

                pagoDAO.crear(
                        connection,
                        pago
                );

                /*
                 * =====================================================
                 * 4. CONFIRMAR TRANSACCIÓN
                 * =====================================================
                 */

                connection.commit();

                return ventaId;

            } catch (Exception e) {

                /*
                 * =====================================================
                 * ROLLBACK
                 * =====================================================
                 *
                 * Si cualquier operación falla,
                 * se deshace toda la venta.
                 */
                try {
                    connection.rollback();
                } catch (Exception rollbackError) {

                    e.addSuppressed(
                            rollbackError
                    );
                }

                throw new RuntimeException(
                        "No se pudo registrar la venta integral",
                        e
                );

            } finally {

                /*
                 * Restauramos el comportamiento normal
                 * de la conexión antes de cerrarla.
                 */
                try {
                    connection.setAutoCommit(true);
                } catch (Exception ignored) {
                }
            }

        } catch (Exception e) {

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            throw new RuntimeException(
                    "Error al conectar con la base de datos",
                    e
            );
        }
    }

    /**
     * Valida la venta principal.
     */
    private void validarVenta(Venta venta) {

        if (venta == null) {

            throw new IllegalArgumentException(
                    "La venta es obligatoria"
            );
        }

        validarId(
                venta.getEmpresaId(),
                "La empresa es obligatoria"
        );

        validarId(
                venta.getSucursalId(),
                "La sucursal es obligatoria"
        );

        if (venta.getNumero() == null
                || venta.getNumero().isBlank()) {

            throw new IllegalArgumentException(
                    "El número de venta es obligatorio"
            );
        }

        validarMonto(
                venta.getSubtotal(),
                "El subtotal"
        );

        validarMonto(
                venta.getDescuento(),
                "El descuento"
        );

        validarMonto(
                venta.getImpuesto(),
                "El impuesto"
        );

        validarMonto(
                venta.getTotal(),
                "El total"
        );
    }

    /**
     * Valida la lista de detalles.
     */
    private void validarDetalles(
            List<VentaDetalle> detalles) {

        if (detalles == null
                || detalles.isEmpty()) {

            throw new IllegalArgumentException(
                    "La venta debe contener al menos un detalle"
            );
        }

        for (VentaDetalle detalle : detalles) {

            validarDetalle(detalle);
        }
    }

    /**
     * Valida un detalle individual.
     */
    private void validarDetalle(
            VentaDetalle detalle) {

        if (detalle == null) {

            throw new IllegalArgumentException(
                    "El detalle de venta es obligatorio"
            );
        }

        validarId(
                detalle.getProductoId(),
                "El producto del detalle es obligatorio"
        );

        if (detalle.getCantidad() == null
                || detalle.getCantidad()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        if (detalle.getPrecioVenta() == null
                || detalle.getPrecioVenta()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El precio de venta no puede ser negativo"
            );
        }

        validarMonto(
                detalle.getDescuento(),
                "El descuento del detalle"
        );

        validarMonto(
                detalle.getImpuesto(),
                "El impuesto del detalle"
        );

        validarMonto(
                detalle.getSubtotal(),
                "El subtotal del detalle"
        );
    }

    /**
     * Valida el pago.
     */
    private void validarPago(Pago pago) {

        if (pago == null) {

            throw new IllegalArgumentException(
                    "El pago es obligatorio"
            );
        }

        if (pago.getMetodo() == null
                || pago.getMetodo().isBlank()) {

            throw new IllegalArgumentException(
                    "El método de pago es obligatorio"
            );
        }

        if (pago.getMonto() == null
                || pago.getMonto()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto del pago debe ser mayor que cero"
            );
        }
    }

    /**
     * Valida un monto no negativo.
     */
    private void validarMonto(
            BigDecimal monto,
            String campo) {

        if (monto == null) {

            throw new IllegalArgumentException(
                    campo + " es obligatorio"
            );
        }

        if (monto.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    campo + " no puede ser negativo"
            );
        }
    }

    /**
     * Valida un identificador.
     */
    private void validarId(
            Long id,
            String mensaje) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }
}