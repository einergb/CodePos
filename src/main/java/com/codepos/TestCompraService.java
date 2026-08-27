package com.codepos;

import com.codepos.model.Compra;
import com.codepos.service.CompraService;

import java.math.BigDecimal;
import java.util.List;

public class TestCompraService {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("     TEST COMPRA SERVICE");
        System.out.println("=================================");

        CompraService compraService =
                new CompraService();

        // =================================
        // 1. CONSULTAR COMPRA
        // =================================

        System.out.println();
        System.out.println("1. Consultando compra...");

        Compra compra =
                compraService.consultar(
                        1L,
                        1L
                );

        System.out.println(
                "✅ Compra encontrada"
        );

        System.out.println(
                "ID: " + compra.getId()
        );

        System.out.println(
                "Número: " + compra.getNumero()
        );

        System.out.println(
                "Proveedor ID: " +
                        compra.getProveedorId()
        );

        System.out.println(
                "Sucursal ID: " +
                        compra.getSucursalId()
        );

        System.out.println(
                "Estado: " + compra.getEstado()
        );

        System.out.println(
                "Subtotal: " +
                        compra.getSubtotal()
        );

        System.out.println(
                "Descuento: " +
                        compra.getDescuento()
        );

        System.out.println(
                "Impuesto: " +
                        compra.getImpuesto()
        );

        System.out.println(
                "Total: " + compra.getTotal()
        );

        // =================================
        // 2. LISTAR COMPRAS
        // =================================

        System.out.println();
        System.out.println(
                "2. Listando compras..."
        );

        List<Compra> compras =
                compraService.listarPorEmpresa(
                        1L
                );

        System.out.println(
                "Total encontradas: " +
                        compras.size()
        );

        for (Compra c : compras) {

            System.out.println(
                    c.getId()
                            + " | "
                            + c.getNumero()
                            + " | Estado: "
                            + c.getEstado()
                            + " | Total: "
                            + c.getTotal()
            );
        }

        // =================================
        // 3. CREAR COMPRA
        // =================================

        System.out.println();
        System.out.println(
                "3. Creando compra de prueba..."
        );

        Compra nuevaCompra =
                new Compra();

        nuevaCompra.setEmpresaId(1L);
        nuevaCompra.setSucursalId(1L);
        nuevaCompra.setProveedorId(1L);

        nuevaCompra.setNumero(
                "COMP-SERVICE-001"
        );

        nuevaCompra.setEstado(
                "REGISTRADA"
        );

        nuevaCompra.setSubtotal(
                new BigDecimal("500000.00")
        );

        nuevaCompra.setDescuento(
                BigDecimal.ZERO
        );

        nuevaCompra.setImpuesto(
                BigDecimal.ZERO
        );

        nuevaCompra.setTotal(
                new BigDecimal("500000.00")
        );

        nuevaCompra.setObservaciones(
                "Compra creada desde TestCompraService"
        );

        Long nuevoId =
                compraService.crear(
                        nuevaCompra
                );

        System.out.println(
                "✅ Compra creada"
        );

        System.out.println(
                "ID generado: " +
                        nuevoId
        );

        // =================================
        // 4. VALIDACIÓN
        // =================================

        System.out.println();
        System.out.println(
                "4. Probando validación..."
        );

        try {

            Compra compraInvalida =
                    new Compra();

            compraInvalida.setEmpresaId(1L);
            compraInvalida.setSucursalId(1L);
            compraInvalida.setProveedorId(1L);

            compraInvalida.setNumero("");

            compraInvalida.setEstado(
                    "REGISTRADA"
            );

            compraInvalida.setSubtotal(
                    BigDecimal.ZERO
            );

            compraInvalida.setDescuento(
                    BigDecimal.ZERO
            );

            compraInvalida.setImpuesto(
                    BigDecimal.ZERO
            );

            compraInvalida.setTotal(
                    BigDecimal.ZERO
            );

            compraService.crear(
                    compraInvalida
            );

            System.out.println(
                    "❌ ERROR: La validación no funcionó"
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

