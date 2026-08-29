package com.codepos;

import com.codepos.dao.VentaDetalleDAO;
import com.codepos.model.VentaDetalle;

import java.math.BigDecimal;
import java.util.List;

public class TestVentaDetalleDAO {

    public static void main(String[] args) {

        VentaDetalleDAO detalleDAO =
                new VentaDetalleDAO();

        System.out.println("=================================");
        System.out.println("   TEST VENTA DETALLE DAO");
        System.out.println("=================================");

        /*
         * =========================================
         * 1. BUSCAR DETALLE
         * =========================================
         */

        System.out.println();
        System.out.println("1. Buscando detalle...");

        VentaDetalle detalle =
                detalleDAO.buscarPorId(1L);

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
                    "Precio venta: "
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

            System.out.println(
                    "Creado: "
                            + detalle.getCreatedAt()
            );

        } else {

            System.out.println(
                    "⚠️ No se encontró el detalle"
            );
        }

        /*
         * =========================================
         * 2. LISTAR DETALLES DE UNA VENTA
         * =========================================
         */

        System.out.println();
        System.out.println(
                "2. Listando detalles de la venta..."
        );

        List<VentaDetalle> detalles =
                detalleDAO.listarPorVenta(1L);

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
         * 3. CREAR DETALLE
         * =========================================
         */

        System.out.println();
        System.out.println(
                "3. Creando detalle de prueba..."
        );

        VentaDetalle nuevoDetalle =
                new VentaDetalle();

        /*
         * Venta existente.
         */
        nuevoDetalle.setVentaId(1L);

        /*
         * Producto existente.
         *
         * Estamos usando el producto 2,
         * que corresponde al producto utilizado
         * en nuestros datos de prueba anteriores.
         */
        nuevoDetalle.setProductoId(2L);

        nuevoDetalle.setCantidad(
                new BigDecimal("2.000")
        );

        nuevoDetalle.setPrecioVenta(
                new BigDecimal("125000.00")
        );

        nuevoDetalle.setDescuento(
                BigDecimal.ZERO
        );

        nuevoDetalle.setImpuesto(
                BigDecimal.ZERO
        );

        /*
         * 2 × 125000 = 250000
         */
        nuevoDetalle.setSubtotal(
                new BigDecimal("250000.00")
        );

        Long idGenerado =
                detalleDAO.crear(
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
         * 4. VERIFICAR DETALLE CREADO
         * =========================================
         */

        System.out.println();
        System.out.println(
                "4. Consultando detalle creado..."
        );

        VentaDetalle detalleCreado =
                detalleDAO.buscarPorId(
                        idGenerado
                );

        if (detalleCreado != null) {

            System.out.println(
                    "✅ Detalle recuperado correctamente"
            );

            System.out.println(
                    "ID: "
                            + detalleCreado.getId()
            );

            System.out.println(
                    "Venta ID: "
                            + detalleCreado.getVentaId()
            );

            System.out.println(
                    "Producto ID: "
                            + detalleCreado.getProductoId()
            );

            System.out.println(
                    "Cantidad: "
                            + detalleCreado.getCantidad()
            );

            System.out.println(
                    "Subtotal: "
                            + detalleCreado.getSubtotal()
            );

        } else {

            System.out.println(
                    "❌ No se pudo recuperar el detalle"
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
