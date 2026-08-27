package com.codepos;

import com.codepos.dao.InventarioDAO;
import com.codepos.model.Inventario;

public class TestInventario {

    public static void main(String[] args) {

        InventarioDAO inventarioDAO =
                new InventarioDAO();

        Inventario inventario =
                inventarioDAO.buscarPorProducto(
                        1L,
                        1L,
                        2L
                );

        if (inventario != null) {

            System.out.println(
                    "✅ Inventario encontrado"
            );

            System.out.println(
                    "ID: "
                    + inventario.getId()
            );

            System.out.println(
                    "Producto: "
                    + inventario.getProductoId()
            );

            System.out.println(
                    "Stock actual: "
                    + inventario.getCantidad()
            );

        } else {

            System.out.println(
                    "❌ Inventario no encontrado"
            );
        }
    }
}
