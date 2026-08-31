package com.codepos.service;

import com.codepos.config.ConexionBD;
import com.codepos.dao.InventarioDAO;
import com.codepos.dao.MovimientoInventarioDAO;
import com.codepos.dao.PagoDAO;
import com.codepos.dao.VentaDAO;
import com.codepos.dao.VentaDetalleDAO;
import com.codepos.dto.ResultadoVenta;
import com.codepos.model.Inventario;
import com.codepos.model.Pago;
import com.codepos.model.Venta;
import com.codepos.model.VentaDetalle;
import com.codepos.util.CalculadoraVentaUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**

 * ============================================================
 * VENTA INTEGRAL SERVICE
 * ============================================================
 *
 * Servicio principal para registrar una venta completa.
 *
 * Responsabilidades:
 *
 * 1. Validar datos de entrada.
 * 2. Calcular los valores monetarios.
 * 3. Validar el pago.
 * 4. Abrir una transacción.
 * 5. Bloquear y validar inventario.
 * 6. Crear la venta.
 * 7. Crear los detalles.
 * 8. Registrar movimientos de inventario.
 * 9. Registrar el pago.
 * 10. Marcar la venta como PAGADA.
 * 11. Confirmar mediante COMMIT.
 *
 * Si cualquier operación falla:
 *
 * ROLLBACK.
 *
 * La transacción completa es responsabilidad
 * exclusiva de este Service.
 */
public class VentaIntegralService {

    private final VentaDAO ventaDAO;
    private final VentaDetalleDAO ventaDetalleDAO;
    private final InventarioDAO inventarioDAO;
    private final MovimientoInventarioDAO movimientoDAO;
    private final PagoDAO pagoDAO;

    /**

     * Constructor.
     */
    public VentaIntegralService() {

        this.ventaDAO =
                new VentaDAO();

        this.ventaDetalleDAO =
                new VentaDetalleDAO();

        this.inventarioDAO =
                new InventarioDAO();

        this.movimientoDAO =
                new MovimientoInventarioDAO();

        this.pagoDAO =
                new PagoDAO();
    }

    /**

     * =========================================================
     * REGISTRAR VENTA INTEGRAL
     * =========================================================
     *
     * Ejecuta toda la operación dentro de una única
     * transacción PostgreSQL.
     *
     * @param venta venta principal
     * @param detalles detalles de la venta
     * @param pago pago realizado
     *
     * @return ID de la venta creada
     */
    public Long registrarVenta(
            Venta venta,
            List<VentaDetalle> detalles,
            Pago pago) {

        /*

         * =====================================================
         * 1. VALIDACIONES PREVIAS
         * =====================================================
         *
         * Estas validaciones ocurren ANTES de abrir
         * la conexión/transacción.
         */

        validarVenta(venta);

        validarDetalles(detalles);

        validarPago(pago);

        /*

         * Validamos que no existan productos repetidos.
         */
        validarProductosDuplicados(detalles);

        /*

         * =====================================================
         * 2. CÁLCULO CENTRALIZADO
         * =====================================================
         *
         * Toda la matemática permanece en
         * CalculadoraVentaUtil.
         */

        ResultadoVenta resultado =
                CalculadoraVentaUtil.calcularVenta(
                        detalles
                );

        /*

         * =====================================================
         * 3. ASIGNAR RESULTADOS
         * =====================================================
         */

        venta.setSubtotal(
                resultado.getSubtotal()
        );

        venta.setDescuento(
                resultado.getDescuento()
        );

        venta.setImpuesto(
                resultado.getImpuesto()
        );

        venta.setTotal(
                resultado.getTotal()
        );

        /*

         * =====================================================
         * 4. VALIDAR PAGO CONTRA TOTAL
         * =====================================================
         */

        if (pago.getMonto().compareTo(
                resultado.getTotal()
        ) != 0) {


            throw new IllegalArgumentException(
                    "El monto del pago debe ser igual "
                            + "al total de la venta. "
                            + "Total esperado: "
                            + resultado.getTotal()
                            + ", monto recibido: "
                            + pago.getMonto()
            );


        }

        /*

         * =====================================================
         * 5. CONEXIÓN
         * =====================================================
         */

        try (
                Connection connection =
                        ConexionBD.conectar()
        ) {


            /*
             * =================================================
             * 6. INICIAR TRANSACCIÓN
             * =================================================
             */

            connection.setAutoCommit(false);

            try {

                /*
                 * =================================================
                 * 7. VALIDAR Y BLOQUEAR TODO EL INVENTARIO
                 * =================================================
                 *
                 * Esta fase ocurre antes de realizar
                 * cualquier INSERT de venta/detalle.
                 *
                 * El objetivo es detectar primero si
                 * toda la venta puede realizarse.
                 */

                validarInventarios(
                        connection,
                        venta,
                        detalles
                );

                /*
                 * =================================================
                 * 8. CREAR VENTA
                 * =================================================
                 */

                venta.setEstado(
                        "REGISTRADA"
                );

                Long ventaId =
                        ventaDAO.crear(
                                connection,
                                venta
                        );

                /*
                 * =================================================
                 * 9. CREAR DETALLES Y MOVIMIENTOS
                 * =================================================
                 */

                for (VentaDetalle detalle : detalles) {

                    /*
                     * Asociamos el detalle
                     * con la venta.
                     */

                    detalle.setVentaId(
                            ventaId
                    );

                    /*
                     * Crear detalle.
                     *
                     * Los valores monetarios ya fueron
                     * calculados por CalculadoraVentaUtil.
                     */

                    ventaDetalleDAO.crear(
                            connection,
                            detalle
                    );

                    /*
                     * Registrar movimiento de inventario.
                     *
                     * PostgreSQL se encarga de actualizar
                     * el stock.
                     */

                    movimientoDAO.registrarMovimiento(
                            connection,
                            venta.getEmpresaId(),
                            venta.getSucursalId(),
                            detalle.getProductoId(),
                            "VENTA",
                            detalle.getCantidad(),
                            "Venta",
                            "VENTA",
                            ventaId,
                            venta.getAuthUserId()
                    );
                }

                /*
                 * =================================================
                 * 10. REGISTRAR PAGO
                 * =================================================
                 */

                pago.setVentaId(
                        ventaId
                );

                /*
                 * Si el pago no tiene usuario,
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
                 * =================================================
                 * 11. MARCAR VENTA COMO PAGADA
                 * =================================================
                 */

                ventaDAO.marcarComoPagada(
                        connection,
                        venta.getEmpresaId(),
                        ventaId
                );

                venta.setEstado(
                        "PAGADA"
                );

                /*
                 * =================================================
                 * 12. COMMIT
                 * =================================================
                 */

                connection.commit();

                return ventaId;

            } catch (Exception e) {

                /*
                 * =================================================
                 * ROLLBACK
                 * =================================================
                 */

                try {

                    connection.rollback();

                } catch (Exception rollbackError) {

                    e.addSuppressed(
                            rollbackError
                    );
                }

                /*
                 * La venta NO debe quedar PAGADA
                 * si la transacción falló.
                 */

                venta.setEstado(
                        "REGISTRADA"
                );

                throw new RuntimeException(
                        "No se pudo registrar la venta integral",
                        e
                );

            } finally {

                /*
                 * Restauramos AutoCommit.
                 */

                try {

                    connection.setAutoCommit(
                            true
                    );

                } catch (Exception ignored) {
                }
            }


        } catch (Exception e) {


            /*
             * Conservamos las excepciones RuntimeException.
             */

            if (e instanceof RuntimeException) {

                throw (RuntimeException) e;
            }

            throw new RuntimeException(
                    "Error al conectar con la base de datos",
                    e
            );


        }
    }

    // =========================================================
    // VALIDACIÓN DE INVENTARIOS
    // =========================================================

    /**

     * Valida todos los inventarios antes de crear
     * la venta.
     *
     * El DAO debe utilizar SELECT ... FOR UPDATE
     * para bloquear los registros durante la transacción.
     *
     * Esto ayuda a evitar problemas de concurrencia
     * cuando dos ventas intentan consumir el mismo stock.
     */
    private void validarInventarios(
            Connection connection,
            Venta venta,
            List<VentaDetalle> detalles) {

        for (VentaDetalle detalle : detalles) {


            Inventario inventario =
                    inventarioDAO.buscarPorProducto(
                            connection,
                            venta.getEmpresaId(),
                            venta.getSucursalId(),
                            detalle.getProductoId()
                    );

            /*
             * Inventario inexistente.
             */

            if (inventario == null) {

                throw new IllegalArgumentException(
                        "No existe inventario para el producto: "
                                + detalle.getProductoId()
                );
            }

            /*
             * Inventario inactivo.
             */

            if (!Boolean.TRUE.equals(
                    inventario.getActivo()
            )) {

                throw new IllegalStateException(
                        "El inventario del producto está inactivo: "
                                + detalle.getProductoId()
                );
            }

            /*
             * Validar stock disponible.
             */

            if (inventario.getCantidad()
                    .compareTo(
                            detalle.getCantidad()
                    ) < 0) {

                throw new IllegalStateException(
                        "Stock insuficiente para el producto: "
                                + detalle.getProductoId()
                                + ". Stock disponible: "
                                + inventario.getCantidad()
                                + ". Cantidad solicitada: "
                                + detalle.getCantidad()
                );
            }


        }
    }

    // =========================================================
    // VALIDACIONES GENERALES
    // =========================================================

    /**

     * Valida la venta principal.
     */
    private void validarVenta(
            Venta venta) {

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


            validarDetalle(
                    detalle
            );


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

        if (detalle.getDescuento() != null
                && detalle.getDescuento()
                .compareTo(BigDecimal.ZERO) < 0) {


            throw new IllegalArgumentException(
                    "El descuento del detalle no puede ser negativo"
            );


        }

        if (detalle.getImpuesto() != null
                && detalle.getImpuesto()
                .compareTo(BigDecimal.ZERO) < 0) {


            throw new IllegalArgumentException(
                    "El impuesto del detalle no puede ser negativo"
            );

        }
    }

    /**

     * Valida el pago.
     */
    private void validarPago(
            Pago pago) {

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

     * Valida que no existan productos repetidos.
     *
     * Un producto debe aparecer una sola vez
     * dentro de una venta.
     *
     * Ejemplo inválido:
     *
     * Producto 2 → cantidad 2
     * Producto 2 → cantidad 3
     *
     * En su lugar debería enviarse:
     *
     * Producto 2 → cantidad 5
     */
    private void validarProductosDuplicados(
            List<VentaDetalle> detalles) {

        Set<Long> productos =
                new HashSet<>();

        for (VentaDetalle detalle : detalles) {


            if (!productos.add(
                    detalle.getProductoId()
            )) {

                throw new IllegalArgumentException(
                        "El producto "
                                + detalle.getProductoId()
                                + " aparece más de una vez "
                                + "en la misma venta"
                );
            }

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
