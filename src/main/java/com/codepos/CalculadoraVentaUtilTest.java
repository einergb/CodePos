package com.codepos;

import com.codepos.dto.ResultadoDetalle;
import com.codepos.dto.ResultadoVenta;
import com.codepos.model.VentaDetalle;
import com.codepos.util.CalculadoraVentaUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraVentaUtilTest {

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("       TEST CALCULADORA VENTA UTIL");
        System.out.println("==============================================");

        try {

            probarCalculoDetalle();

            probarCalculoDetalleValoresNull();

            probarCalculoVenta();

            probarRedondeo();

            probarValidaciones();

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       TODOS LOS TEST FINALIZADOS");
            System.out.println("       RESULTADO: ✅ CORRECTO");
            System.out.println("==============================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       ❌ ERROR EN LOS TEST");
            System.out.println("==============================================");

            e.printStackTrace();

            System.exit(1);
        }
    }

    /**
     * =========================================================
     * 1. CALCULO COMPLETO DE UN DETALLE
     * =========================================================
     *
     * Cantidad:       2
     * Precio:         350000
     * Descuento:      20000
     * Impuesto:       100000
     *
     * Subtotal:
     *
     * 2 × 350000 = 700000
     *
     * Total:
     *
     * 700000 - 20000 + 100000
     *
     * = 780000
     */
    private static void probarCalculoDetalle() {

        System.out.println();
        System.out.println("1. Probando cálculo completo de detalle...");

        BigDecimal cantidad =
                new BigDecimal("2");

        BigDecimal precio =
                new BigDecimal("350000");

        BigDecimal descuento =
                new BigDecimal("20000");

        BigDecimal impuesto =
                new BigDecimal("100000");

        ResultadoDetalle resultado =
                CalculadoraVentaUtil.calcularDetalle(
                        cantidad,
                        precio,
                        descuento,
                        impuesto
                );

        verificar(
                resultado.getSubtotal(),
                new BigDecimal("700000.00"),
                "Subtotal detalle"
        );

        verificar(
                resultado.getDescuento(),
                new BigDecimal("20000.00"),
                "Descuento detalle"
        );

        verificar(
                resultado.getImpuesto(),
                new BigDecimal("100000.00"),
                "Impuesto detalle"
        );

        verificar(
                resultado.getTotal(),
                new BigDecimal("780000.00"),
                "Total detalle"
        );
    }

    /**
     * =========================================================
     * 2. CALCULO CON DESCUENTO E IMPUESTO NULL
     * =========================================================
     *
     * Cantidad: 2
     * Precio: 100000
     * Descuento: null
     * Impuesto: null
     *
     * Subtotal = 200000
     * Descuento = 0
     * Impuesto = 0
     * Total = 200000
     */
    private static void probarCalculoDetalleValoresNull() {

        System.out.println();
        System.out.println(
                "2. Probando cálculo con descuento e impuesto null..."
        );

        ResultadoDetalle resultado =
                CalculadoraVentaUtil.calcularDetalle(
                        new BigDecimal("2"),
                        new BigDecimal("100000"),
                        null,
                        null
                );

        verificar(
                resultado.getSubtotal(),
                new BigDecimal("200000.00"),
                "Subtotal con valores null"
        );

        verificar(
                resultado.getDescuento(),
                new BigDecimal("0.00"),
                "Descuento null convertido a cero"
        );

        verificar(
                resultado.getImpuesto(),
                new BigDecimal("0.00"),
                "Impuesto null convertido a cero"
        );

        verificar(
                resultado.getTotal(),
                new BigDecimal("200000.00"),
                "Total con valores null"
        );
    }

    /**
     * =========================================================
     * 3. CALCULO COMPLETO DE LA VENTA
     * =========================================================
     *
     * DETALLE 1
     *
     * 2 × 350000 = 700000
     * Descuento = 20000
     * Impuesto = 100000
     *
     * Total = 780000
     *
     * DETALLE 2
     *
     * 1 × 80000 = 80000
     * Descuento = 10000
     * Impuesto = 42500
     *
     * Total = 112500
     *
     * VENTA
     *
     * Subtotal:
     *
     * 700000 + 80000 = 780000
     *
     * Descuento:
     *
     * 20000 + 10000 = 30000
     *
     * Impuesto:
     *
     * 100000 + 42500 = 142500
     *
     * Total:
     *
     * 780000 - 30000 + 142500
     *
     * = 892500
     */
    private static void probarCalculoVenta() {

        System.out.println();
        System.out.println("3. Probando cálculo completo de venta...");

        VentaDetalle detalle1 =
                new VentaDetalle();

        detalle1.setProductoId(1L);

        detalle1.setCantidad(
                new BigDecimal("2")
        );

        detalle1.setPrecioVenta(
                new BigDecimal("350000")
        );

        detalle1.setDescuento(
                new BigDecimal("20000")
        );

        detalle1.setImpuesto(
                new BigDecimal("100000")
        );

        VentaDetalle detalle2 =
                new VentaDetalle();

        detalle2.setProductoId(2L);

        detalle2.setCantidad(
                new BigDecimal("1")
        );

        detalle2.setPrecioVenta(
                new BigDecimal("80000")
        );

        detalle2.setDescuento(
                new BigDecimal("10000")
        );

        detalle2.setImpuesto(
                new BigDecimal("42500")
        );

        List<VentaDetalle> detalles =
                List.of(
                        detalle1,
                        detalle2
                );

        ResultadoVenta resultado =
                CalculadoraVentaUtil.calcularVenta(
                        detalles
                );

        verificar(
                resultado.getSubtotal(),
                new BigDecimal("780000.00"),
                "Subtotal venta"
        );

        verificar(
                resultado.getDescuento(),
                new BigDecimal("30000.00"),
                "Descuento venta"
        );

        verificar(
                resultado.getImpuesto(),
                new BigDecimal("142500.00"),
                "Impuesto venta"
        );

        verificar(
                resultado.getTotal(),
                new BigDecimal("892500.00"),
                "Total venta"
        );

        /*
         * Verificamos que la calculadora
         * haya actualizado cada detalle.
         */

        verificar(
                detalle1.getSubtotal(),
                new BigDecimal("700000.00"),
                "Subtotal almacenado detalle 1"
        );

        verificar(
                detalle1.getDescuento(),
                new BigDecimal("20000.00"),
                "Descuento almacenado detalle 1"
        );

        verificar(
                detalle1.getImpuesto(),
                new BigDecimal("100000.00"),
                "Impuesto almacenado detalle 1"
        );

        verificar(
                detalle2.getSubtotal(),
                new BigDecimal("80000.00"),
                "Subtotal almacenado detalle 2"
        );

        verificar(
                detalle2.getDescuento(),
                new BigDecimal("10000.00"),
                "Descuento almacenado detalle 2"
        );

        verificar(
                detalle2.getImpuesto(),
                new BigDecimal("42500.00"),
                "Impuesto almacenado detalle 2"
        );
    }

    /**
     * =========================================================
     * 4. REDONDEO
     * =========================================================
     */
    private static void probarRedondeo() {

        System.out.println();
        System.out.println("4. Probando redondeo monetario...");

        BigDecimal resultado =
                CalculadoraVentaUtil.redondear(
                        new BigDecimal("100.555")
                );

        verificar(
                resultado,
                new BigDecimal("100.56"),
                "Redondeo HALF_UP"
        );
    }

    /**
     * =========================================================
     * 5. VALIDACIONES
     * =========================================================
     */
    private static void probarValidaciones() {

        System.out.println();
        System.out.println("5. Probando validaciones...");

        /*
         * =====================================================
         * CANTIDAD NEGATIVA
         * =====================================================
         */

        probarError(
                () ->
                        CalculadoraVentaUtil.calcularDetalle(
                                new BigDecimal("-1"),
                                new BigDecimal("100"),
                                BigDecimal.ZERO,
                                BigDecimal.ZERO
                        ),
                "Cantidad negativa"
        );

        /*
         * =====================================================
         * CANTIDAD CERO
         * =====================================================
         */

        probarError(
                () ->
                        CalculadoraVentaUtil.calcularDetalle(
                                BigDecimal.ZERO,
                                new BigDecimal("100"),
                                BigDecimal.ZERO,
                                BigDecimal.ZERO
                        ),
                "Cantidad cero"
        );

        /*
         * =====================================================
         * PRECIO NEGATIVO
         * =====================================================
         */

        probarError(
                () ->
                        CalculadoraVentaUtil.calcularDetalle(
                                BigDecimal.ONE,
                                new BigDecimal("-100"),
                                BigDecimal.ZERO,
                                BigDecimal.ZERO
                        ),
                "Precio negativo"
        );

        /*
         * =====================================================
         * DESCUENTO NEGATIVO
         * =====================================================
         */

        probarError(
                () ->
                        CalculadoraVentaUtil.calcularDetalle(
                                BigDecimal.ONE,
                                new BigDecimal("100"),
                                new BigDecimal("-10"),
                                BigDecimal.ZERO
                        ),
                "Descuento negativo"
        );

        /*
         * =====================================================
         * IMPUESTO NEGATIVO
         * =====================================================
         */

        probarError(
                () ->
                        CalculadoraVentaUtil.calcularDetalle(
                                BigDecimal.ONE,
                                new BigDecimal("100"),
                                BigDecimal.ZERO,
                                new BigDecimal("-10")
                        ),
                "Impuesto negativo"
        );

        /*
         * =====================================================
         * DESCUENTO MAYOR QUE SUBTOTAL
         * =====================================================
         *
         * Subtotal = 100
         * Descuento = 101
         */

        probarError(
                () ->
                        CalculadoraVentaUtil.calcularDetalle(
                                BigDecimal.ONE,
                                new BigDecimal("100"),
                                new BigDecimal("101"),
                                BigDecimal.ZERO
                        ),
                "Descuento mayor que subtotal"
        );

        /*
         * =====================================================
         * LISTA VACÍA
         * =====================================================
         */

        probarError(
                () ->
                        CalculadoraVentaUtil.calcularVenta(
                                List.of()
                        ),
                "Lista de detalles vacía"
        );

        /*
         * =====================================================
         * LISTA NULL
         * =====================================================
         */

        probarError(
                () ->
                        CalculadoraVentaUtil.calcularVenta(
                                null
                        ),
                "Lista de detalles null"
        );

        /*
         * =====================================================
         * DETALLE NULL
         * =====================================================
         *
         * NO usamos:
         *
         * List.of(null)
         *
         * porque List.of() no permite elementos null
         * y lanzaría NullPointerException antes de
         * llegar a CalculadoraVentaUtil.
         */

        probarError(
                () -> {

                    List<VentaDetalle> detalles =
                            new ArrayList<>();

                    detalles.add(null);

                    CalculadoraVentaUtil.calcularVenta(
                            detalles
                    );
                },
                "Detalle null"
        );
    }

    /**
     * Ejecuta una operación que debe producir
     * IllegalArgumentException.
     */
    private static void probarError(
            Runnable operacion,
            String prueba) {

        try {

            operacion.run();

            throw new AssertionError(
                    prueba
                            + " → NO fue rechazada"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ "
                            + prueba
                            + " → rechazada correctamente"
            );
        }
    }

    /**
     * Compara un resultado BigDecimal
     * contra el valor esperado.
     */
    private static void verificar(
            BigDecimal resultado,
            BigDecimal esperado,
            String prueba) {

        if (resultado != null
                && resultado.compareTo(esperado) == 0) {

            System.out.println(
                    "✅ "
                            + prueba
                            + " → "
                            + resultado
            );

        } else {

            throw new AssertionError(
                    prueba
                            + " → esperado: "
                            + esperado
                            + ", obtenido: "
                            + resultado
            );
        }
    }
}
