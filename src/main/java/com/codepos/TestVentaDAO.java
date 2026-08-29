package com.codepos;

import com.codepos.model.Venta;
import com.codepos.dao.VentaDAO;

import java.math.BigDecimal;
import java.util.List;

public class TestVentaDAO {

    public static void main(String[] args) {

        VentaDAO ventaDAO = new VentaDAO();

        System.out.println("=================================");
        System.out.println("        TEST VENTA DAO");
        System.out.println("=================================");

        // =========================================
        // 1. BUSCAR VENTA
        // =========================================

        System.out.println();
        System.out.println("1. Buscando venta...");

        Venta venta =
                ventaDAO.buscarPorId(1L, 1L);

        if (venta != null) {

            System.out.println(
                    "✅ Venta encontrada"
            );

            System.out.println(
                    "ID: " + venta.getId()
            );

            System.out.println(
                    "Número: " + venta.getNumero()
            );

            System.out.println(
                    "Empresa ID: "
                            + venta.getEmpresaId()
            );

            System.out.println(
                    "Sucursal ID: "
                            + venta.getSucursalId()
            );

            System.out.println(
                    "Cliente ID: "
                            + venta.getClienteId()
            );

            System.out.println(
                    "Estado: " + venta.getEstado()
            );

            System.out.println(
                    "Subtotal: "
                            + venta.getSubtotal()
            );

            System.out.println(
                    "Descuento: "
                            + venta.getDescuento()
            );

            System.out.println(
                    "Impuesto: "
                            + venta.getImpuesto()
            );

            System.out.println(
                    "Total: " + venta.getTotal()
            );

        } else {

            System.out.println(
                    "⚠️ Venta no encontrada"
            );
        }

        // =========================================
        // 2. LISTAR VENTAS
        // =========================================

        System.out.println();
        System.out.println(
                "2. Listando ventas..."
        );

        List<Venta> ventas =
                ventaDAO.listarPorEmpresa(1L);

        System.out.println(
                "Total encontradas: "
                        + ventas.size()
        );

        for (Venta v : ventas) {

            System.out.println(
                    v.getId()
                            + " | "
                            + v.getNumero()
                            + " | Cliente: "
                            + v.getClienteId()
                            + " | Estado: "
                            + v.getEstado()
                            + " | Total: "
                            + v.getTotal()
            );
        }

        // =========================================
        // 3. CREAR VENTA CON CLIENTE
        // =========================================

        System.out.println();
        System.out.println(
                "3. Creando venta con cliente..."
        );

        Venta nuevaVenta =
                new Venta();

        nuevaVenta.setEmpresaId(1L);
        nuevaVenta.setSucursalId(1L);
        nuevaVenta.setClienteId(1L);
        nuevaVenta.setAuthUserId(null);

        nuevaVenta.setNumero(
                "DAO-VENTA-"
                        + System.currentTimeMillis()
        );

        nuevaVenta.setEstado(
                "REGISTRADA"
        );

        nuevaVenta.setSubtotal(
                new BigDecimal("100000.00")
        );

        nuevaVenta.setDescuento(
                BigDecimal.ZERO
        );

        nuevaVenta.setImpuesto(
                BigDecimal.ZERO
        );

        nuevaVenta.setTotal(
                new BigDecimal("100000.00")
        );

        nuevaVenta.setObservaciones(
                "Venta de prueba DAO"
        );

        Long idVenta =
                ventaDAO.crear(nuevaVenta);

        System.out.println(
                "✅ Venta creada"
        );

        System.out.println(
                "ID generado: "
                        + idVenta
        );

        // =========================================
        // 4. CREAR VENTA MOSTRADOR
        // =========================================

        System.out.println();
        System.out.println(
                "4. Creando venta mostrador..."
        );

        Venta ventaMostrador =
                new Venta();

        ventaMostrador.setEmpresaId(1L);
        ventaMostrador.setSucursalId(1L);

        /*
         * cliente_id queda NULL.
         */

        ventaMostrador.setClienteId(null);

        ventaMostrador.setAuthUserId(null);

        ventaMostrador.setNumero(
                "DAO-MOSTRADOR-"
                        + System.currentTimeMillis()
        );

        ventaMostrador.setEstado(
                "REGISTRADA"
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
                "Venta mostrador DAO"
        );

        Long idMostrador =
                ventaDAO.crear(
                        ventaMostrador
                );

        System.out.println(
                "✅ Venta mostrador creada"
        );

        System.out.println(
                "ID generado: "
                        + idMostrador
        );

        System.out.println();
        System.out.println("=================================");
        System.out.println("       PRUEBA FINALIZADA");
        System.out.println("=================================");
    }

}
