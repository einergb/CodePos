package com.codepos;

import com.codepos.dao.CompraDAO;
import com.codepos.model.Compra;

import java.math.BigDecimal;
import java.util.List;

public class TestCompra {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        TEST COMPRA DAO");
        System.out.println("=================================");

        CompraDAO compraDAO = new CompraDAO();

        Long empresaId = 1L;

        // =================================
        // 1. BUSCAR COMPRA
        // =================================

        System.out.println();
        System.out.println("1. Buscando compra...");

        Compra compra =
                compraDAO.buscarPorId(
                        empresaId,
                        1L
                );

        if (compra != null) {

            System.out.println("✅ Compra encontrada");

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
                    "Estado: " +
                            compra.getEstado()
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
                    "Total: " +
                            compra.getTotal()
            );

        } else {

            System.out.println(
                    "❌ No se encontró la compra"
            );
        }

        // =================================
        // 2. LISTAR COMPRAS
        // =================================

        System.out.println();
        System.out.println(
                "2. Listando compras..."
        );

        List<Compra> compras =
                compraDAO.listarPorEmpresa(
                        empresaId
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

        nuevaCompra.setEmpresaId(
                1L
        );

        nuevaCompra.setSucursalId(
                1L
        );

        nuevaCompra.setProveedorId(
                1L
        );

        nuevaCompra.setNumero(
                "COMP-TEST-002"
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
                "Compra creada desde TestCompra"
        );

        Long nuevoId =
                compraDAO.crear(
                        nuevaCompra
                );

        System.out.println(
                "✅ Compra creada"
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