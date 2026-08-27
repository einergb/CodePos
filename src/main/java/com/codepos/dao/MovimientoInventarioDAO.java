package com.codepos.dao;

import com.codepos.config.ConexionBD;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

public class MovimientoInventarioDAO {

    /**
     * Registra un movimiento utilizando
     * la función de PostgreSQL.
     *
     * @return ID del movimiento generado.
     */
    public Long registrarMovimiento(
            Long empresaId,
            Long sucursalId,
            Long productoId,
            String tipo,
            BigDecimal cantidad,
            String motivo,
            String referenciaTipo,
            Long referenciaId,
            Integer authUserId) {

        String sql = """
                SELECT registrar_movimiento_inventario(
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """;

        try (
                Connection connection = ConexionBD.conectar();
                CallableStatement statement =
                        connection.prepareCall(sql)
        ) {

            statement.setLong(1, empresaId);
            statement.setLong(2, sucursalId);
            statement.setLong(3, productoId);
            statement.setString(4, tipo);
            statement.setBigDecimal(5, cantidad);
            statement.setString(6, motivo);

            if (referenciaTipo != null) {
                statement.setString(7, referenciaTipo);
            } else {
                statement.setNull(
                        7,
                        Types.VARCHAR
                );
            }

            if (referenciaId != null) {
                statement.setLong(8, referenciaId);
            } else {
                statement.setNull(
                        8,
                        Types.BIGINT
                );
            }

            if (authUserId != null) {
                statement.setInt(9, authUserId);
            } else {
                statement.setNull(
                        9,
                        Types.INTEGER
                );
            }

            try (var resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al registrar movimiento de inventario",
                    e
            );
        }

        throw new RuntimeException(
                "PostgreSQL no devolvió el ID del movimiento"
        );
    }
}
