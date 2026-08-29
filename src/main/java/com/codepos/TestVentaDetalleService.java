package com.codepos;

import com.codepos.model.VentaDetalle;
import com.codepos.service.VentaDetalleService;

import java.math.BigDecimal;
import java.util.List;

public class TestVentaDetalleService {

    public static void main(String[] args) {

        VentaDetalleService detalleService =
                new VentaDetalleService();

        System.out.println("=================================");
        System.out.println("  TEST VENTA DETALLE SERVICE");
        System.out.println("=================================");

        /*
         * =========================================
         * 1. CONSULTAR DETALLE
         * =========================================
         */

        System.out.println();
        System.out.println("1. Consultando detalle...");

        VentaDetalle detalle =
                detalleService.buscarPorId(1L);

        if (detalle != null) {

            System.out.println(
                    "✅ Detalle encontrado"
            );

            System.out.println(
                    "ID: "
                            + detalle.getId()
            );

            System.out.println(
                    "Venta ID: "
                            + detalle.getVentaId()
            );

            System.out.println(
                    "Producto ID: "
                            + detalle.getProductoId()
            );

            System.out.println(
                    "Cantidad: "
                            + detalle.getCantidad()
            );

            System.out.println(
                    "Precio: "
                            + detalle.getPrecioVenta()
            );

            System.out.println(
                    "Descuento: "
                            + detalle.getDescuento()
            );

            System.out.println(
                    "Impuesto: "
                            + detalle.getImpuesto()
            );

            System.out.println(
                    "Subtotal: "
                            + detalle.getSubtotal()
            );

        } else {

            System.out.println(
                    "⚠️ No se encontró el detalle"
            );
        }

        /*
         * =========================================
         * 2. LISTAR DETALLES
         * =========================================
         */

        System.out.println();
        System.out.println(
                "2. Listando detalles de la venta..."
        );

        List<VentaDetalle> detalles =
                detalleService.listarPorVenta(1L);

        System.out.println(
                "Total encontrados: "
                        + detalles.size()
        );

        for (VentaDetalle item : detalles) {

            System.out.println(
                    item.getId()
                            + " | Venta: "
                            + item.getVentaId()
                            + " | Producto: "
                            + item.getProductoId()
                            + " | Cantidad: "
                            + item.getCantidad()
                            + " | Precio: "
                            + item.getPrecioVenta()
                            + " | Descuento: "
                            + item.getDescuento()
                            + " | Impuesto: "
                            + item.getImpuesto()
                            + " | Subtotal: "
                            + item.getSubtotal()
            );
        }

        /*
         * =========================================
         * 3. CREAR DETALLE VÁLIDO
         * =========================================
         */

        System.out.println();
        System.out.println(
                "3. Creando detalle válido..."
        );

        VentaDetalle nuevoDetalle =
                new VentaDetalle();

        nuevoDetalle.setVentaId(1L);
        nuevoDetalle.setProductoId(2L);

        nuevoDetalle.setCantidad(
                new BigDecimal("1.000")
        );

        nuevoDetalle.setPrecioVenta(
                new BigDecimal("250000.00")
        );

        nuevoDetalle.setDescuento(
                new BigDecimal("0.00")
        );

        nuevoDetalle.setImpuesto(
                new BigDecimal("0.00")
        );

        nuevoDetalle.setSubtotal(
                new BigDecimal("250000.00")
        );

        Long idGenerado =
                detalleService.crear(
                        nuevoDetalle
                );

        System.out.println(
                "✅ Detalle creado"
        );

        System.out.println(
                "ID generado: "
                        + idGenerado
        );

        /*
         * =========================================
         * 4. VALIDAR CANTIDAD
         * =========================================
         */

        System.out.println();
        System.out.println(
                "4. Probando validación de cantidad..."
        );

        VentaDetalle cantidadInvalida =
                new VentaDetalle();

        cantidadInvalida.setVentaId(1L);
        cantidadInvalida.setProductoId(2L);

        cantidadInvalida.setCantidad(
                BigDecimal.ZERO
        );

        cantidadInvalida.setPrecioVenta(
                new BigDecimal("100000.00")
        );

        cantidadInvalida.setDescuento(
                BigDecimal.ZERO
        );

        cantidadInvalida.setImpuesto(
                BigDecimal.ZERO
        );

        cantidadInvalida.setSubtotal(
                new BigDecimal("100000.00")
        );

        try {

            detalleService.crear(
                    cantidadInvalida
            );

            System.out.println(
                    "❌ La validación falló"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }

        /*
         * =========================================
         * 5. VALIDAR PRECIO
         * =========================================
         */

        System.out.println();
        System.out.println(
                "5. Probando validación de precio..."
        );

        VentaDetalle precioInvalido =
                new VentaDetalle();

        precioInvalido.setVentaId(1L);
        precioInvalido.setProductoId(2L);

        precioInvalido.setCantidad(
                BigDecimal.ONE
        );

        precioInvalido.setPrecioVenta(
                new BigDecimal("-100.00")
        );

        precioInvalido.setDescuento(
                BigDecimal.ZERO
        );

        precioInvalido.setImpuesto(
                BigDecimal.ZERO
        );

        precioInvalido.setSubtotal(
                BigDecimal.ZERO
        );

        try {

            detalleService.crear(
                    precioInvalido
            );

            System.out.println(
                    "❌ La validación falló"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }

        /*
         * =========================================
         * 6. VALIDAR DESCUENTO
         * =========================================
         */

        System.out.println();
        System.out.println(
                "6. Probando validación de descuento..."
        );

        VentaDetalle descuentoInvalido =
                new VentaDetalle();

        descuentoInvalido.setVentaId(1L);
        descuentoInvalido.setProductoId(2L);

        descuentoInvalido.setCantidad(
                BigDecimal.ONE
        );

        descuentoInvalido.setPrecioVenta(
                new BigDecimal("100000.00")
        );

        descuentoInvalido.setDescuento(
                new BigDecimal("-1.00")
        );

        descuentoInvalido.setImpuesto(
                BigDecimal.ZERO
        );

        descuentoInvalido.setSubtotal(
                new BigDecimal("100000.00")
        );

        try {

            detalleService.crear(
                    descuentoInvalido
            );

            System.out.println(
                    "❌ La validación falló"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }

        /*
         * =========================================
         * 7. VALIDAR IMPUESTO
         * =========================================
         */

        System.out.println();
        System.out.println(
                "7. Probando validación de impuesto..."
        );

        VentaDetalle impuestoInvalido =
                new VentaDetalle();

        impuestoInvalido.setVentaId(1L);
        impuestoInvalido.setProductoId(2L);

        impuestoInvalido.setCantidad(
                BigDecimal.ONE
        );

        impuestoInvalido.setPrecioVenta(
                new BigDecimal("100000.00")
        );

        impuestoInvalido.setDescuento(
                BigDecimal.ZERO
        );

        impuestoInvalido.setImpuesto(
                new BigDecimal("-1.00")
        );

        impuestoInvalido.setSubtotal(
                new BigDecimal("100000.00")
        );

        try {

            detalleService.crear(
                    impuestoInvalido
            );

            System.out.println(
                    "❌ La validación falló"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }

        /*
         * =========================================
         * 8. VALIDAR SUBTOTAL
         * =========================================
         */

        System.out.println();
        System.out.println(
                "8. Probando validación de subtotal..."
        );

        VentaDetalle subtotalInvalido =
                new VentaDetalle();

        subtotalInvalido.setVentaId(1L);
        subtotalInvalido.setProductoId(2L);

        subtotalInvalido.setCantidad(
                BigDecimal.ONE
        );

        subtotalInvalido.setPrecioVenta(
                new BigDecimal("100000.00")
        );

        subtotalInvalido.setDescuento(
                BigDecimal.ZERO
        );

        subtotalInvalido.setImpuesto(
                BigDecimal.ZERO
        );

        subtotalInvalido.setSubtotal(
                new BigDecimal("-100.00")
        );

        try {

            detalleService.crear(
                    subtotalInvalido
            );

            System.out.println(
                    "❌ La validación falló"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }

        /*
         * =========================================
         * 9. VALIDAR VENTA
         * =========================================
         */

        System.out.println();
        System.out.println(
                "9. Probando validación de venta..."
        );

        VentaDetalle ventaInvalida =
                new VentaDetalle();

        ventaInvalida.setVentaId(null);
        ventaInvalida.setProductoId(2L);

        ventaInvalida.setCantidad(
                BigDecimal.ONE
        );

        ventaInvalida.setPrecioVenta(
                new BigDecimal("100000.00")
        );

        ventaInvalida.setDescuento(
                BigDecimal.ZERO
        );

        ventaInvalida.setImpuesto(
                BigDecimal.ZERO
        );

        ventaInvalida.setSubtotal(
                new BigDecimal("100000.00")
        );

        try {

            detalleService.crear(
                    ventaInvalida
            );

            System.out.println(
                    "❌ La validación falló"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }

        /*
         * =========================================
         * FIN
         * =========================================
         */

        System.out.println();
        System.out.println("=================================");
        System.out.println(
                "       PRUEBA FINALIZADA"
        );
        System.out.println("=================================");
    }

}
