package com.codepos;

import com.codepos.model.Venta;
import com.codepos.service.VentaService;

import java.math.BigDecimal;
import java.util.List;

public class TestVentaService {

    public static void main(String[] args) {

        VentaService ventaService =
                new VentaService();

        System.out.println("=================================");
        System.out.println("     TEST VENTA SERVICE");
        System.out.println("=================================");

        /*
         * =========================================
         * 1. CONSULTAR VENTA
         * =========================================
         */

        System.out.println();
        System.out.println("1. Consultando venta...");

        Venta ventaEncontrada =
                ventaService.buscarPorId(
                        1L,
                        1L
                );

        if (ventaEncontrada != null) {

            System.out.println(
                    "✅ Venta encontrada"
            );

            System.out.println(
                    "ID: "
                            + ventaEncontrada.getId()
            );

            System.out.println(
                    "Número: "
                            + ventaEncontrada.getNumero()
            );

            System.out.println(
                    "Cliente ID: "
                            + ventaEncontrada.getClienteId()
            );

            System.out.println(
                    "Usuario ID: "
                            + ventaEncontrada.getAuthUserId()
            );

            System.out.println(
                    "Estado: "
                            + ventaEncontrada.getEstado()
            );

            System.out.println(
                    "Total: "
                            + ventaEncontrada.getTotal()
            );

        } else {

            System.out.println(
                    "⚠️ Venta no encontrada"
            );
        }

        /*
         * =========================================
         * 2. LISTAR VENTAS
         * =========================================
         */

        System.out.println();
        System.out.println("2. Listando ventas...");

        List<Venta> ventas =
                ventaService.listarPorEmpresa(
                        1L
                );

        System.out.println(
                "Total encontradas: "
                        + ventas.size()
        );

        for (Venta venta : ventas) {

            System.out.println(
                    venta.getId()
                            + " | "
                            + venta.getNumero()
                            + " | Cliente: "
                            + venta.getClienteId()
                            + " | Usuario: "
                            + venta.getAuthUserId()
                            + " | Estado: "
                            + venta.getEstado()
                            + " | Total: "
                            + venta.getTotal()
            );
        }

        /*
         * =========================================
         * 3. CREAR VENTA CON CLIENTE
         * =========================================
         */

        System.out.println();
        System.out.println(
                "3. Creando venta con cliente..."
        );

        Venta ventaConCliente =
                new Venta();

        ventaConCliente.setEmpresaId(1L);
        ventaConCliente.setSucursalId(1L);

        // Cliente de prueba existente
        ventaConCliente.setClienteId(1L);

        // Usuario autenticado de prueba
        ventaConCliente.setAuthUserId(1);

        ventaConCliente.setNumero(
                "SERVICE-VENTA-"
                        + System.currentTimeMillis()
        );

        /*
         * El Service establecerá
         * automáticamente:
         *
         * REGISTRADA
         */
        ventaConCliente.setEstado(
                "PAGADA"
        );

        ventaConCliente.setSubtotal(
                new BigDecimal("100000.00")
        );

        ventaConCliente.setDescuento(
                BigDecimal.ZERO
        );

        ventaConCliente.setImpuesto(
                BigDecimal.ZERO
        );

        ventaConCliente.setTotal(
                new BigDecimal("100000.00")
        );

        ventaConCliente.setObservaciones(
                "Venta creada mediante TestVentaService"
        );

        Long ventaId =
                ventaService.crear(
                        ventaConCliente
                );

        System.out.println(
                "✅ Venta creada"
        );

        System.out.println(
                "ID generado: "
                        + ventaId
        );

        System.out.println(
                "Estado aplicado por Service: "
                        + ventaConCliente.getEstado()
        );

        /*
         * =========================================
         * 4. CREAR VENTA MOSTRADOR
         * =========================================
         */

        System.out.println();
        System.out.println(
                "4. Creando venta mostrador..."
        );

        Venta ventaMostrador =
                new Venta();

        ventaMostrador.setEmpresaId(1L);
        ventaMostrador.setSucursalId(1L);

        /*
         * Sin cliente:
         * venta de mostrador.
         */
        ventaMostrador.setClienteId(null);

        // Usuario autenticado
        ventaMostrador.setAuthUserId(1);

        ventaMostrador.setNumero(
                "SERVICE-MOSTRADOR-"
                        + System.currentTimeMillis()
        );

        ventaMostrador.setSubtotal(
                new BigDecimal("50000.00")
        );

        ventaMostrador.setDescuento(
                BigDecimal.ZERO
        );

        ventaMostrador.setImpuesto(
                BigDecimal.ZERO
        );

        ventaMostrador.setTotal(
                new BigDecimal("50000.00")
        );

        ventaMostrador.setObservaciones(
                "Venta mostrador de prueba"
        );

        Long ventaMostradorId =
                ventaService.crear(
                        ventaMostrador
                );

        System.out.println(
                "✅ Venta mostrador creada"
        );

        System.out.println(
                "ID generado: "
                        + ventaMostradorId
        );

        /*
         * =========================================
         * 5. VALIDACIÓN DE EMPRESA
         * =========================================
         */

        System.out.println();
        System.out.println(
                "5. Probando validación de empresa..."
        );

        try {

            Venta ventaInvalida =
                    new Venta();

            ventaInvalida.setEmpresaId(null);
            ventaInvalida.setSucursalId(1L);
            ventaInvalida.setNumero(
                    "TEST-INVALIDO"
            );

            ventaInvalida.setSubtotal(
                    BigDecimal.ZERO
            );

            ventaInvalida.setDescuento(
                    BigDecimal.ZERO
            );

            ventaInvalida.setImpuesto(
                    BigDecimal.ZERO
            );

            ventaInvalida.setTotal(
                    BigDecimal.ZERO
            );

            ventaService.crear(
                    ventaInvalida
            );

            System.out.println(
                    "❌ La validación no funcionó"
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
         * 6. VALIDACIÓN DE TOTAL NEGATIVO
         * =========================================
         */

        System.out.println();
        System.out.println(
                "6. Probando validación de total..."
        );

        try {

            Venta ventaInvalida =
                    new Venta();

            ventaInvalida.setEmpresaId(1L);
            ventaInvalida.setSucursalId(1L);

            ventaInvalida.setNumero(
                    "TEST-TOTAL-INVALIDO"
            );

            ventaInvalida.setSubtotal(
                    new BigDecimal("100000.00")
            );

            ventaInvalida.setDescuento(
                    BigDecimal.ZERO
            );

            ventaInvalida.setImpuesto(
                    BigDecimal.ZERO
            );

            ventaInvalida.setTotal(
                    new BigDecimal("-1.00")
            );

            ventaService.crear(
                    ventaInvalida
            );

            System.out.println(
                    "❌ La validación no funcionó"
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
