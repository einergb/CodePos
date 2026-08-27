package com.codepos;

import com.codepos.model.Inventario;
import com.codepos.service.InventarioService;

public class TestInventarioService {

    public static void main(String[] args) {

        InventarioService inventarioService =
                new InventarioService();

        try {

            Inventario inventario =
                    inventarioService.consultar(
                            1L,
                            1L,
                            2L
                    );

            System.out.println(
                    "✅ Consulta realizada correctamente"
            );

            System.out.println(
                    "Empresa: "
                    + inventario.getEmpresaId()
            );

            System.out.println(
                    "Sucursal: "
                    + inventario.getSucursalId()
            );

            System.out.println(
                    "Producto: "
                    + inventario.getProductoId()
            );

            System.out.println(
                    "Stock: "
                    + inventario.getCantidad()
            );

            System.out.println(
                    "Stock mínimo: "
                    + inventario.getStockMinimo()
            );

            System.out.println(
                    "Stock máximo: "
                    + inventario.getStockMaximo()
            );

        } catch (Exception e) {

            System.err.println(
                    "❌ Error: "
                    + e.getMessage()
            );
        }
    }
}
