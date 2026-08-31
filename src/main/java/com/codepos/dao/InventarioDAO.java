package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Inventario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;

public class InventarioDAO {

    /**
     * Busca el inventario utilizando una conexión propia.
     *
     * Mantiene compatibilidad con los servicios y tests
     * que consultan inventario fuera de una transacción integral.
     */
    public Inventario buscarPorProducto(
            Long empresaId,
            Long sucursalId,
            Long productoId) {

        try (
                Connection connection =
                        ConexionBD.conectar()
        ) {

            return buscarPorProducto(
                    connection,
                    empresaId,
                    sucursalId,
                    productoId
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al consultar el inventario",
                    e
            );
        }
    }

    /**
     * Busca el inventario utilizando una conexión existente.
     *
     * Este método está diseñado para operaciones
     * transaccionales.
     *
     * FOR UPDATE bloquea la fila durante la transacción,
     * evitando modificaciones concurrentes mientras
     * se verifica y descuenta el stock.
     */
    public Inventario buscarPorProducto(
            Connection connection,
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
            FOR UPDATE
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);
            statement.setLong(2, sucursalId);
            statement.setLong(3, productoId);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return mapearInventario(resultSet);
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al consultar inventario dentro de la transacción",
                    e
            );
        }

        return null;
    }

    /**
     * Descuenta stock utilizando la conexión
     * de la transacción actual.
     */
    public void descontarStock(
            Connection connection,
            Long inventarioId,
            BigDecimal cantidad) {

        String sql = """
            UPDATE inventarios
            SET cantidad = cantidad - ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
              AND cantidad >= ?
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBigDecimal(1, cantidad);
            statement.setLong(2, inventarioId);
            statement.setBigDecimal(3, cantidad);

            int filas =
                    statement.executeUpdate();

            if (filas != 1) {

                throw new RuntimeException(
                        "No hay inventario suficiente para realizar la venta"
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al descontar inventario",
                    e
            );
        }
    }

    /**
     * Convierte un ResultSet en un objeto Inventario.
     */
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