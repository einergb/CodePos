package com.codepos.util;

import com.codepos.model.VentaDetalle;
import com.codepos.dto.ResultadoDetalle;
import com.codepos.dto.ResultadoVenta;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Utilidad central para realizar todos los cálculos
 * monetarios relacionados con las ventas de CodePOS.
 *
 * RESPONSABILIDADES:
 *
 * 1. Calcular detalles.
 * 2. Calcular subtotal.
 * 3. Calcular descuentos.
 * 4. Calcular impuestos.
 * 5. Calcular total.
 * 6. Redondear valores monetarios.
 * 7. Validar valores utilizados en los cálculos.
 *
 * Esta clase NO realiza operaciones de base de datos.
 *
 * Esta clase representa la única fuente de verdad
 * para los cálculos de ventas.
 */
public final class CalculadoraVentaUtil {

    /**
     * Número de decimales utilizados para valores monetarios.
     */
    private static final int ESCALA_MONETARIA = 2;

    /**
     * Valor cero monetario.
     */
    private static final BigDecimal CERO =
            BigDecimal.ZERO.setScale(
                    ESCALA_MONETARIA,
                    RoundingMode.HALF_UP
            );

    /**
     * Constructor privado.
     *
     * Evita crear instancias de esta clase.
     */
    private CalculadoraVentaUtil() {
    }

    // =========================================================
    // DETALLE DE VENTA
    // =========================================================

    /**
     * Realiza el cálculo COMPLETO de un detalle.
     *
     * Fórmula:
     *
     * subtotal = cantidad × precioVenta
     *
     * total = subtotal - descuento + impuesto
     *
     * Los valores null de descuento e impuesto
     * se consideran cero.
     *
     * @param cantidad cantidad vendida
     * @param precioVenta precio unitario
     * @param descuento descuento aplicado
     * @param impuesto impuesto aplicado
     *
     * @return resultado completo del detalle
     */
    public static ResultadoDetalle calcularDetalle(
            BigDecimal cantidad,
            BigDecimal precioVenta,
            BigDecimal descuento,
            BigDecimal impuesto) {

        validarPositivo(
                cantidad,
                "La cantidad debe ser mayor que cero"
        );

        validarNoNegativo(
                precioVenta,
                "El precio de venta no puede ser negativo"
        );

        BigDecimal descuentoSeguro =
                descuento != null
                        ? descuento
                        : CERO;

        BigDecimal impuestoSeguro =
                impuesto != null
                        ? impuesto
                        : CERO;

        validarNoNegativo(
                descuentoSeguro,
                "El descuento no puede ser negativo"
        );

        validarNoNegativo(
                impuestoSeguro,
                "El impuesto no puede ser negativo"
        );

        /*
         * =============================================
         * 1. SUBTOTAL
         * =============================================
         */

        BigDecimal subtotal =
                redondear(
                        cantidad.multiply(precioVenta)
                );

        /*
         * =============================================
         * 2. VALIDAR DESCUENTO
         * =============================================
         *
         * No permitimos que el descuento
         * supere el subtotal.
         */

        if (descuentoSeguro.compareTo(subtotal) > 0) {

            throw new IllegalArgumentException(
                    "El descuento no puede ser mayor que el subtotal"
            );
        }

        /*
         * =============================================
         * 3. TOTAL
         * =============================================
         */

        BigDecimal total =
                subtotal
                        .subtract(descuentoSeguro)
                        .add(impuestoSeguro);

        total = redondear(total);

        if (total.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El total del detalle no puede ser negativo"
            );
        }

        return new ResultadoDetalle(
                subtotal,
                redondear(descuentoSeguro),
                redondear(impuestoSeguro),
                total
        );
    }

    /**
     * Calcula y actualiza todos los valores monetarios
     * de un objeto VentaDetalle.
     *
     * Después de ejecutar este método el detalle contiene:
     *
     * subtotal
     * descuento
     * impuesto
     *
     * El total del detalle se calcula pero no se almacena,
     * ya que el modelo VentaDetalle actualmente utiliza
     * subtotal como valor persistido.
     *
     * @param detalle detalle de venta
     *
     * @return resultado completo del cálculo
     */
    public static ResultadoDetalle calcularDetalle(
            VentaDetalle detalle) {

        if (detalle == null) {

            throw new IllegalArgumentException(
                    "El detalle de venta es obligatorio"
            );
        }

        ResultadoDetalle resultado =
                calcularDetalle(
                        detalle.getCantidad(),
                        detalle.getPrecioVenta(),
                        detalle.getDescuento(),
                        detalle.getImpuesto()
                );

        /*
         * Actualizamos el modelo con los valores
         * calculados por la utilidad.
         */

        detalle.setSubtotal(
                resultado.getSubtotal()
        );

        detalle.setDescuento(
                resultado.getDescuento()
        );

        detalle.setImpuesto(
                resultado.getImpuesto()
        );

        return resultado;
    }

    /**
     * Calcula todos los detalles de una venta.
     *
     * Cada detalle es recalculado individualmente.
     *
     * @param detalles lista de detalles
     */
    public static void calcularDetalles(
            List<VentaDetalle> detalles) {

        validarListaDetalles(detalles);

        for (VentaDetalle detalle : detalles) {

            calcularDetalle(detalle);
        }
    }

    // =========================================================
    // VENTA COMPLETA
    // =========================================================

    /**
     * Calcula TODA la venta a partir de sus detalles.
     *
     * Este es el método principal que debería utilizar
     * VentaIntegralService.
     *
     * Fórmulas:
     *
     * subtotal =
     *      suma de subtotales de los detalles
     *
     * descuento =
     *      suma de descuentos
     *
     * impuesto =
     *      suma de impuestos
     *
     * total =
     *      subtotal - descuento + impuesto
     *
     * @param detalles detalles de la venta
     *
     * @return resultado consolidado de la venta
     */
    public static ResultadoVenta calcularVenta(
            List<VentaDetalle> detalles) {

        validarListaDetalles(detalles);

        BigDecimal subtotal =
                CERO;

        BigDecimal descuento =
                CERO;

        BigDecimal impuesto =
                CERO;

        /*
         * Cada detalle se calcula nuevamente
         * desde sus valores originales.
         */
        for (VentaDetalle detalle : detalles) {

            ResultadoDetalle resultado =
                    calcularDetalle(detalle);

            /*
             * Actualizamos el detalle.
             */

            detalle.setSubtotal(
                    resultado.getSubtotal()
            );

            detalle.setDescuento(
                    resultado.getDescuento()
            );

            detalle.setImpuesto(
                    resultado.getImpuesto()
            );

            /*
             * Acumulamos los valores.
             */

            subtotal =
                    subtotal.add(
                            resultado.getSubtotal()
                    );

            descuento =
                    descuento.add(
                            resultado.getDescuento()
                    );

            impuesto =
                    impuesto.add(
                            resultado.getImpuesto()
                    );
        }

        /*
         * Redondeamos los acumulados.
         */

        subtotal =
                redondear(subtotal);

        descuento =
                redondear(descuento);

        impuesto =
                redondear(impuesto);

        /*
         * =============================================
         * TOTAL FINAL
         * =============================================
         */

        BigDecimal total =
                calcularTotal(
                        subtotal,
                        descuento,
                        impuesto
                );

        return new ResultadoVenta(
                subtotal,
                descuento,
                impuesto,
                total
        );
    }

    // =========================================================
    // CÁLCULO DEL TOTAL
    // =========================================================

    /**
     * Calcula el total final de una venta.
     *
     * Fórmula:
     *
     * subtotal - descuento + impuesto
     *
     * Este método es utilizado internamente por
     * calcularVenta().
     *
     * @param subtotal subtotal de la venta
     * @param descuento descuento total
     * @param impuesto impuesto total
     *
     * @return total calculado
     */
    public static BigDecimal calcularTotal(
            BigDecimal subtotal,
            BigDecimal descuento,
            BigDecimal impuesto) {

        validarNoNegativo(
                subtotal,
                "El subtotal no puede ser negativo"
        );

        validarNoNegativo(
                descuento,
                "El descuento no puede ser negativo"
        );

        validarNoNegativo(
                impuesto,
                "El impuesto no puede ser negativo"
        );

        if (descuento.compareTo(subtotal) > 0) {

            throw new IllegalArgumentException(
                    "El descuento no puede ser mayor que el subtotal"
            );
        }

        BigDecimal total =
                subtotal
                        .subtract(descuento)
                        .add(impuesto);

        total =
                redondear(total);

        if (total.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El total de la venta no puede ser negativo"
            );
        }

        return total;
    }

    // =========================================================
    // MÉTODOS DE COMPATIBILIDAD
    // =========================================================

    /**
     * Calcula únicamente el subtotal bruto
     * de un detalle.
     *
     * Mantiene compatibilidad con tests
     * y código existente.
     */
    public static BigDecimal calcularSubtotalDetalle(
            BigDecimal cantidad,
            BigDecimal precioVenta) {

        validarPositivo(
                cantidad,
                "La cantidad debe ser mayor que cero"
        );

        validarNoNegativo(
                precioVenta,
                "El precio de venta no puede ser negativo"
        );

        return redondear(
                cantidad.multiply(precioVenta)
        );
    }

    /**
     * Calcula el subtotal bruto de todos los detalles.
     *
     * No incluye descuentos ni impuestos.
     */
    public static BigDecimal calcularSubtotal(
            List<VentaDetalle> detalles) {

        validarListaDetalles(detalles);

        BigDecimal subtotal =
                CERO;

        for (VentaDetalle detalle : detalles) {

            subtotal =
                    subtotal.add(
                            calcularSubtotalDetalle(
                                    detalle.getCantidad(),
                                    detalle.getPrecioVenta()
                            )
                    );
        }

        return redondear(subtotal);
    }

    /**
     * Calcula el total de descuentos
     * de todos los detalles.
     */
    public static BigDecimal calcularDescuentoDetalles(
            List<VentaDetalle> detalles) {

        validarListaDetalles(detalles);

        BigDecimal descuento =
                CERO;

        for (VentaDetalle detalle : detalles) {

            BigDecimal valor =
                    detalle.getDescuento();

            if (valor == null) {
                continue;
            }

            validarNoNegativo(
                    valor,
                    "El descuento del detalle no puede ser negativo"
            );

            descuento =
                    descuento.add(valor);
        }

        return redondear(descuento);
    }

    /**
     * Calcula el total de impuestos
     * de todos los detalles.
     */
    public static BigDecimal calcularImpuestoDetalles(
            List<VentaDetalle> detalles) {

        validarListaDetalles(detalles);

        BigDecimal impuesto =
                CERO;

        for (VentaDetalle detalle : detalles) {

            BigDecimal valor =
                    detalle.getImpuesto();

            if (valor == null) {
                continue;
            }

            validarNoNegativo(
                    valor,
                    "El impuesto del detalle no puede ser negativo"
            );

            impuesto =
                    impuesto.add(valor);
        }

        return redondear(impuesto);
    }

    // =========================================================
    // REDONDEO
    // =========================================================

    /**
     * Redondea un valor monetario a dos decimales.
     *
     * Utiliza HALF_UP.
     */
    public static BigDecimal redondear(
            BigDecimal valor) {

        if (valor == null) {

            throw new IllegalArgumentException(
                    "El valor es obligatorio"
            );
        }

        return valor.setScale(
                ESCALA_MONETARIA,
                RoundingMode.HALF_UP
        );
    }

    // =========================================================
    // VALIDACIONES
    // =========================================================

    /**
     * Valida que la lista contenga al menos
     * un detalle.
     */
    private static void validarListaDetalles(
            List<VentaDetalle> detalles) {

        if (detalles == null
                || detalles.isEmpty()) {

            throw new IllegalArgumentException(
                    "La venta debe contener al menos un detalle"
            );
        }

        for (VentaDetalle detalle : detalles) {

            if (detalle == null) {

                throw new IllegalArgumentException(
                        "El detalle de venta no puede ser null"
                );
            }
        }
    }

    /**
     * Valida que un valor no sea negativo.
     */
    private static void validarNoNegativo(
            BigDecimal valor,
            String mensaje) {

        if (valor == null) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }

        if (valor.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }

    /**
     * Valida que un valor sea mayor que cero.
     */
    private static void validarPositivo(
            BigDecimal valor,
            String mensaje) {

        if (valor == null) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }
}