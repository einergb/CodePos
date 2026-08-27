package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Inventario;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventarioDAO {

    public Inventario buscarPorProducto(
            Long empresaId,
            Long sucursalId,
            Long productoId) {

        String sql = """
                SELECT
                    id,
                    empresa_id,
                    sucursal_id,
                    producto_id,
                    cantidad,
                    stock_minimo,
                    stock_maximo,
                    activo,
                    created_at,
                    updated_at
                FROM inventarios
                WHERE empresa_id = ?
                  AND sucursal_id = ?
                  AND producto_id = ?
                """;

        try (
                Connection connection = ConexionBD.conectar();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);
            statement.setLong(2, sucursalId);
            statement.setLong(3, productoId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapearInventario(resultSet);
                }

            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al consultar el inventario",
                    e
            );
        }

        return null;
    }

    private Inventario mapearInventario(
            ResultSet resultSet) throws Exception {

        Inventario inventario = new Inventario();

        inventario.setId(
                resultSet.getLong("id")
        );

        inventario.setEmpresaId(
                resultSet.getLong("empresa_id")
        );

        inventario.setSucursalId(
                resultSet.getLong("sucursal_id")
        );

        inventario.setProductoId(
                resultSet.getLong("producto_id")
        );

        inventario.setCantidad(
                resultSet.getBigDecimal("cantidad")
        );

        inventario.setStockMinimo(
                resultSet.getBigDecimal("stock_minimo")
        );

        inventario.setStockMaximo(
                resultSet.getBigDecimal("stock_maximo")
        );

        inventario.setActivo(
                resultSet.getBoolean("activo")
        );

        inventario.setCreatedAt(
                resultSet.getObject(
                        "created_at",
                        java.time.OffsetDateTime.class
                )
        );

        inventario.setUpdatedAt(
                resultSet.getObject(
                        "updated_at",
                        java.time.OffsetDateTime.class
                )
        );

        return inventario;
    }


}
