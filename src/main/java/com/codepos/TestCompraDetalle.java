package com.codepos;

import com.codepos.dao.CompraDetalleDAO;
import com.codepos.model.CompraDetalle;

import java.math.BigDecimal;
import java.util.List;

public class TestCompraDetalle {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("    TEST COMPRA DETALLE DAO");
        System.out.println("=================================");

        CompraDetalleDAO detalleDAO =
                new CompraDetalleDAO();

        // =================================
        // 1. BUSCAR DETALLE
        // =================================

        System.out.println();
        System.out.println("1. Buscando detalle...");

        CompraDetalle detalle =
                detalleDAO.buscarPorId(1L);

        if (detalle != null) {

            System.out.println(
                    "✅ Detalle encontrado"
            );

            System.out.println(
                    "ID: " + detalle.getId()
            );

            System.out.println(
                    "Compra ID: " +
                            detalle.getCompraId()
            );

            System.out.println(
                    "Producto ID: " +
                            detalle.getProductoId()
            );

            System.out.println(
                    "Cantidad: " +
                            detalle.getCantidad()
            );

            System.out.println(
                    "Precio compra: " +
                            detalle.getPrecioCompra()
            );

            System.out.println(
                    "Descuento: " +
                            detalle.getDescuento()
            );

            System.out.println(
                    "Impuesto: " +
                            detalle.getImpuesto()
            );

            System.out.println(
                    "Subtotal: " +
                            detalle.getSubtotal()
            );

        } else {

            System.out.println(
                    "❌ No se encontró el detalle"
            );
        }

        // =================================
        // 2. LISTAR DETALLES DE UNA COMPRA
        // =================================

        System.out.println();
        System.out.println(
                "2. Listando detalles de compra..."
        );

        List<CompraDetalle> detalles =
                detalleDAO.listarPorCompra(1L);

        System.out.println(
                "Total encontrados: " +
                        detalles.size()
        );

        for (CompraDetalle d : detalles) {

            System.out.println(
                    d.getId()
                            + " | Compra: "
                            + d.getCompraId()
                            + " | Producto: "
                            + d.getProductoId()
                            + " | Cantidad: "
                            + d.getCantidad()
                            + " | Subtotal: "
                            + d.getSubtotal()
            );
        }

        // =================================
        // 3. CREAR DETALLE
        // =================================

        System.out.println();
        System.out.println(
                "3. Creando detalle de prueba..."
        );

        CompraDetalle nuevoDetalle =
                new CompraDetalle();

        nuevoDetalle.setCompraId(2L);

        nuevoDetalle.setProductoId(2L);

        nuevoDetalle.setCantidad(
                new BigDecimal("2.000")
        );

        nuevoDetalle.setPrecioCompra(
                new BigDecimal("250000.00")
        );

        nuevoDetalle.setDescuento(
                BigDecimal.ZERO
        );

        nuevoDetalle.setImpuesto(
                BigDecimal.ZERO
        );

        nuevoDetalle.setSubtotal(
                new BigDecimal("500000.00")
        );

        Long nuevoId =
                detalleDAO.crear(
                        nuevoDetalle
                );

        System.out.println(
                "✅ Detalle creado"
        );

        System.out.println(
                "ID generado: " +
                        nuevoId
        );

        System.out.println();
        System.out.println("=================================");
        System.out.println(
                "       PRUEBA FINALIZADA"
        );
        System.out.println("=================================");
    }
}
