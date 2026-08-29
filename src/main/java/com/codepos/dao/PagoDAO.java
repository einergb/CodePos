package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    /**
     * Busca un pago por su ID.
     */
    public Pago buscarPorId(Long pagoId) {

        String sql = """
                SELECT
                    id,
                    venta_id,
                    auth_user_id,
                    metodo,
                    monto,
                    referencia,
                    fecha,
                    created_at
                FROM pagos
                WHERE id = ?
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, pagoId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return mapearPago(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar pago",
                    e
            );
        }

        return null;
    }

    /**
     * Lista los pagos asociados a una venta.
     */
    public List<Pago> listarPorVenta(Long ventaId) {

        String sql = """
                SELECT
                    id,
                    venta_id,
                    auth_user_id,
                    metodo,
                    monto,
                    referencia,
                    fecha,
                    created_at
                FROM pagos
                WHERE venta_id = ?
                ORDER BY id
                """;

        List<Pago> pagos = new ArrayList<>();

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, ventaId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                while (rs.next()) {

                    pagos.add(
                            mapearPago(rs)
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al listar pagos de la venta",
                    e
            );
        }

        return pagos;
    }

    /**
     * Registra un nuevo pago.
     */
    public Long crear(Pago pago) {

        String sql = """
                INSERT INTO pagos (
                    venta_id,
                    auth_user_id,
                    metodo,
                    monto,
                    referencia
                )
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    pago.getVentaId()
            );

            if (pago.getAuthUserId() != null) {
                statement.setInt(
                        2,
                        pago.getAuthUserId()
                );
            } else {
                statement.setNull(
                        2,
                        Types.INTEGER
                );
            }

            statement.setString(
                    3,
                    pago.getMetodo()
            );

            statement.setBigDecimal(
                    4,
                    pago.getMonto()
            );

            if (pago.getReferencia() != null) {
                statement.setString(
                        5,
                        pago.getReferencia()
                );
            } else {
                statement.setNull(
                        5,
                        Types.VARCHAR
                );
            }

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al crear pago",
                    e
            );
        }

        throw new RuntimeException(
                "No fue posible obtener el ID del pago"
        );
    }

    /**
     * Convierte un ResultSet en un objeto Pago.
     */
    private Pago mapearPago(
            ResultSet rs) throws SQLException {

        Pago pago = new Pago();

        pago.setId(
                rs.getLong("id")
        );

        pago.setVentaId(
                rs.getLong("venta_id")
        );

        int authUserId =
                rs.getInt("auth_user_id");

        if (!rs.wasNull()) {
            pago.setAuthUserId(authUserId);
        }

        pago.setMetodo(
                rs.getString("metodo")
        );

        pago.setMonto(
                rs.getBigDecimal("monto")
        );

        pago.setReferencia(
                rs.getString("referencia")
        );

        Timestamp fecha =
                rs.getTimestamp("fecha");

        if (fecha != null) {
            pago.setFecha(
                    fecha.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {
            pago.setCreatedAt(
                    createdAt.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        return pago;
    }
}
