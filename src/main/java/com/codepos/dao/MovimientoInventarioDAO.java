
package com.codepos.dao;

import com.codepos.config.ConexionBD;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

public class MovimientoInventarioDAO {

    /**
     * Registra un movimiento de inventario utilizando
     * una conexión propia.
     *
     * Este método mantiene compatibilidad con las
     * pruebas y operaciones independientes del sistema.
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

        try (
                Connection connection =
                        ConexionBD.conectar()
        ) {

            return registrarMovimiento(
                    connection,
                    empresaId,
                    sucursalId,
                    productoId,
                    tipo,
                    cantidad,
                    motivo,
                    referenciaTipo,
                    referenciaId,
                    authUserId
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al registrar movimiento de inventario",
                    e
            );
        }
    }

    /**
     * Registra un movimiento utilizando una conexión
     * existente.
     *
     * Este método es utilizado cuando la operación
     * forma parte de una transacción mayor.
     *
     * IMPORTANTE:
     *
     * Este método NO abre ni cierra la conexión.
     * La conexión pertenece al Service que controla
     * la transacción.
     *
     * Ejemplo:
     *
     * BEGIN
     *     venta
     *     detalle
     *     inventario
     *     pago
     * COMMIT
     *
     * Si algo falla, el Service ejecutará ROLLBACK.
     *
     * @param connection conexión de la transacción actual
     * @return ID del movimiento generado
     */
    public Long registrarMovimiento(
            Connection connection,
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
                CallableStatement statement =
                        connection.prepareCall(sql)
        ) {

            statement.setLong(
                    1,
                    empresaId
            );

            statement.setLong(
                    2,
                    sucursalId
            );

            statement.setLong(
                    3,
                    productoId
            );

            statement.setString(
                    4,
                    tipo
            );

            statement.setBigDecimal(
                    5,
                    cantidad
            );

            statement.setString(
                    6,
                    motivo
            );

            /*
             * Referencia de la operación.
             *
             * Ejemplo:
             *
             * referenciaTipo = "VENTA"
             * referenciaId   = ID de la venta
             */
            if (referenciaTipo != null) {

                statement.setString(
                        7,
                        referenciaTipo
                );

            } else {

                statement.setNull(
                        7,
                        Types.VARCHAR
                );
            }

            if (referenciaId != null) {

                statement.setLong(
                        8,
                        referenciaId
                );

            } else {

                statement.setNull(
                        8,
                        Types.BIGINT
                );
            }

            /*
             * Usuario autenticado que realizó
             * el movimiento.
             */
            if (authUserId != null) {

                statement.setInt(
                        9,
                        authUserId
                );

            } else {

                statement.setNull(
                        9,
                        Types.INTEGER
                );
            }

            /*
             * La función PostgreSQL retorna
             * el ID del movimiento generado.
             */
            try (
                    var resultSet =
                            statement.executeQuery()
            ) {

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

