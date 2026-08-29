package com.codepos;

import com.codepos.model.Pago;
import com.codepos.service.PagoService;

import java.math.BigDecimal;
import java.util.List;

public class TestPagoService {

    public static void main(String[] args) {

        PagoService pagoService = new PagoService();

        System.out.println("=================================");
        System.out.println("     TEST PAGO SERVICE");
        System.out.println("=================================");

        // =========================================
        // 1. CONSULTAR PAGO
        // =========================================

        System.out.println();
        System.out.println("1. Consultando pago...");

        Pago pago = pagoService.buscarPorId(1L);

        if (pago != null) {

            System.out.println("✅ Pago encontrado");
            System.out.println("ID: " + pago.getId());
            System.out.println("Venta ID: " + pago.getVentaId());
            System.out.println("Método: " + pago.getMetodo());
            System.out.println("Monto: " + pago.getMonto());
            System.out.println("Referencia: " + pago.getReferencia());

        } else {

            System.out.println("❌ Pago no encontrado");
        }

        // =========================================
        // 2. LISTAR PAGOS DE UNA VENTA
        // =========================================

        System.out.println();
        System.out.println("2. Listando pagos de la venta...");

        List<Pago> pagos =
                pagoService.listarPorVenta(1L);

        System.out.println(
                "Total encontrados: " + pagos.size()
        );

        for (Pago p : pagos) {

            System.out.println(
                    p.getId()
                            + " | Venta: "
                            + p.getVentaId()
                            + " | Método: "
                            + p.getMetodo()
                            + " | Monto: "
                            + p.getMonto()
                            + " | Referencia: "
                            + p.getReferencia()
            );
        }

        // =========================================
        // 3. CREAR PAGO
        // =========================================

        System.out.println();
        System.out.println("3. Creando pago de prueba...");

        Pago nuevoPago = new Pago();

        nuevoPago.setVentaId(1L);
        nuevoPago.setAuthUserId(null);
        nuevoPago.setMetodo("TRANSFERENCIA");
        nuevoPago.setMonto(
                new BigDecimal("1.00")
        );
        nuevoPago.setReferencia(
                "SERVICE-TEST-PAGO-001"
        );

        Long idGenerado =
                pagoService.crear(nuevoPago);

        System.out.println("✅ Pago creado");
        System.out.println(
                "ID generado: " + idGenerado
        );

        // =========================================
        // 4. PROBAR VALIDACIÓN
        // =========================================

        System.out.println();
        System.out.println("4. Probando validación...");

        try {

            Pago pagoInvalido = new Pago();

            pagoInvalido.setVentaId(1L);
            pagoInvalido.setMetodo("EFECTIVO");
            pagoInvalido.setMonto(
                    BigDecimal.ZERO
            );

            pagoService.crear(pagoInvalido);

            System.out.println(
                    "❌ La validación NO funcionó"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: " + e.getMessage()
            );
        }

        System.out.println();
        System.out.println("=================================");
        System.out.println("       PRUEBA FINALIZADA");
        System.out.println("=================================");
    }

}
