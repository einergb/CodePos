package com.codepos;

import com.codepos.service.InventarioService;

import java.math.BigDecimal;

public class TestMovimientoInventario {

    public static void main(String[] args) {

        InventarioService service =
                new InventarioService();

        try {

            System.out.println("=================================");
            System.out.println(" TEST MOVIMIENTO DE INVENTARIO");
            System.out.println("=================================");

            System.out.println("Stock antes:");

            var inventarioAntes =
                    service.consultar(
                            1L,
                            1L,
                            2L
                    );

            System.out.println(
                    "Producto: "
                            + inventarioAntes.getProductoId()
            );

            System.out.println(
                    "Stock: "
                            + inventarioAntes.getCantidad()
            );

            System.out.println("---------------------------------");

            Long movimientoId =
                    service.registrarMovimiento(
                            1L,
                            1L,
                            2L,
                            "AJUSTE_ENTRADA",
                            new BigDecimal("1"),
                            "Prueba técnica desde backend Java",
                            null,
                            null,
                            null
                    );

            System.out.println(
                    "✅ Movimiento registrado"
            );

            System.out.println(
                    "ID movimiento: "
                            + movimientoId
            );

            System.out.println("---------------------------------");

            System.out.println("Stock después:");

            var inventarioDespues =
                    service.consultar(
                            1L,
                            1L,
                            2L
                    );

            System.out.println(
                    "Producto: "
                            + inventarioDespues.getProductoId()
            );

            System.out.println(
                    "Stock: "
                            + inventarioDespues.getCantidad()
            );

            System.out.println("=================================");
            System.out.println(" PRUEBA FINALIZADA");
            System.out.println("=================================");

        } catch (Exception e) {

            System.err.println(
                    "❌ Error durante la prueba:"
            );

            System.err.println(
                    e.getMessage()
            );
        }
    }
}