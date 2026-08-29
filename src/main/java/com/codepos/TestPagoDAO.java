package com.codepos;

import com.codepos.dao.PagoDAO;
import com.codepos.model.Pago;

import java.math.BigDecimal;
import java.util.List;

public class TestPagoDAO {

    public static void main(String[] args) {

        PagoDAO pagoDAO = new PagoDAO();

        System.out.println("=================================");
        System.out.println("        TEST PAGO DAO");
        System.out.println("=================================");

        // =========================================
        // 1. BUSCAR PAGO
        // =========================================

        System.out.println();
        System.out.println("1. Buscando pago...");

        Pago pago = pagoDAO.buscarPorId(1L);

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
                pagoDAO.listarPorVenta(1L);

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
        // 3. CREAR PAGO DE PRUEBA
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
                "TEST-PAGO-CODEPOS-001"
        );

        Long idGenerado =
                pagoDAO.crear(nuevoPago);

        System.out.println("✅ Pago creado");
        System.out.println(
                "ID generado: " + idGenerado
        );

        System.out.println();
        System.out.println("=================================");
        System.out.println("       PRUEBA FINALIZADA");
        System.out.println("=================================");
    }

}
