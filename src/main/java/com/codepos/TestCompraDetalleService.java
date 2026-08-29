
package com.codepos;

import com.codepos.model.CompraDetalle;
import com.codepos.service.CompraDetalleService;

import java.math.BigDecimal;
import java.util.List;

public class TestCompraDetalleService {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println(" TEST COMPRA DETALLE SERVICE");
        System.out.println("=================================");

        CompraDetalleService service =
                new CompraDetalleService();

        // =================================
        // 1. CONSULTAR DETALLE
        // =================================

        System.out.println();
        System.out.println("1. Consultando detalle...");

        CompraDetalle detalle =
                service.consultar(1L);

        System.out.println("✅ Detalle encontrado");
        System.out.println(
                "ID: " + detalle.getId()
        );
        System.out.println(
                "Compra ID: " + detalle.getCompraId()
        );
        System.out.println(
                "Producto ID: " + detalle.getProductoId()
        );
        System.out.println(
                "Cantidad: " + detalle.getCantidad()
        );
        System.out.println(
                "Precio compra: " +
                        detalle.getPrecioCompra()
        );
        System.out.println(
                "Subtotal: " +
                        detalle.getSubtotal()
        );

        // =================================
        // 2. LISTAR DETALLES
        // =================================

        System.out.println();
        System.out.println(
                "2. Listando detalles de compra..."
        );

        List<CompraDetalle> detalles =
                service.listarPorCompra(1L);

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

        CompraDetalle nuevo =
                new CompraDetalle();

        nuevo.setCompraId(1L);
        nuevo.setProductoId(2L);
        nuevo.setCantidad(
                new BigDecimal("1")
        );
        nuevo.setPrecioCompra(
                new BigDecimal("250000")
        );
        nuevo.setDescuento(
                BigDecimal.ZERO
        );
        nuevo.setImpuesto(
                BigDecimal.ZERO
        );
        nuevo.setSubtotal(
                new BigDecimal("250000")
        );

        Long idGenerado =
                service.crear(nuevo);

        System.out.println(
                "✅ Detalle creado"
        );

        System.out.println(
                "ID generado: " +
                        idGenerado
        );

        // =================================
        // 4. VALIDACIÓN
        // =================================

        System.out.println();
        System.out.println(
                "4. Probando validación..."
        );

        try {

            CompraDetalle invalido =
                    new CompraDetalle();

            invalido.setCompraId(1L);
            invalido.setProductoId(2L);
            invalido.setCantidad(
                    BigDecimal.ZERO
            );
            invalido.setPrecioCompra(
                    new BigDecimal("100000")
            );
            invalido.setDescuento(
                    BigDecimal.ZERO
            );
            invalido.setImpuesto(
                    BigDecimal.ZERO
            );
            invalido.setSubtotal(
                    BigDecimal.ZERO
            );

            service.crear(invalido);

            System.out.println(
                    "❌ La validación no funcionó"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: " +
                            e.getMessage()
            );
        }

        System.out.println();
        System.out.println("=================================");
        System.out.println(
                "       PRUEBA FINALIZADA"
        );
        System.out.println("=================================");
    }
}

