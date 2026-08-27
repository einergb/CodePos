package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Compra;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    /**
     * Busca una compra por ID y empresa.
     */
    public Compra buscarPorId(
            Long empresaId,
            Long compraId) {

        String sql = """
                SELECT
                    id,
                    empresa_id,
                    sucursal_id,
                    proveedor_id,
                    auth_user_id,
                    numero,
                    fecha,
                    estado,
                    subtotal,
                    descuento,
                    impuesto,
                    total,
                    observaciones,
                    created_at,
                    updated_at
                FROM compras
                WHERE empresa_id = ?
                  AND id = ?
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);
            statement.setLong(2, compraId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return mapearCompra(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar compra",
                    e
            );
        }

        return null;
    }

    /**
     * Lista las compras pertenecientes a una empresa.
     */
    public List<Compra> listarPorEmpresa(
            Long empresaId) {

        String sql = """
                SELECT
                    id,
                    empresa_id,
                    sucursal_id,
                    proveedor_id,
                    auth_user_id,
                    numero,
                    fecha,
                    estado,
                    subtotal,
                    descuento,
                    impuesto,
                    total,
                    observaciones,
                    created_at,
                    updated_at
                FROM compras
                WHERE empresa_id = ?
                ORDER BY id
                """;

        List<Compra> compras =
                new ArrayList<>();

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                while (rs.next()) {

                    compras.add(
                            mapearCompra(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar compras",
                    e
            );
        }

        return compras;
    }

    /**
     * Crea una compra.
     *
     * La compra se registra inicialmente
     * con el estado indicado por el objeto.
     */
    public Long crear(Compra compra) {

        String sql = """
                INSERT INTO compras (
                    empresa_id,
                    sucursal_id,
                    proveedor_id,
                    auth_user_id,
                    numero,
                    estado,
                    subtotal,
                    descuento,
                    impuesto,
                    total,
                    observaciones
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
                    compra.getEmpresaId()
            );

            statement.setLong(
                    2,
                    compra.getSucursalId()
            );

            statement.setLong(
                    3,
                    compra.getProveedorId()
            );

            if (compra.getAuthUserId() != null) {

                statement.setInt(
                        4,
                        compra.getAuthUserId()
                );

            } else {

                statement.setNull(
                        4,
                        Types.INTEGER
                );
            }

            statement.setString(
                    5,
                    compra.getNumero()
            );

            statement.setString(
                    6,
                    compra.getEstado()
            );

            statement.setBigDecimal(
                    7,
                    compra.getSubtotal()
            );

            statement.setBigDecimal(
                    8,
                    compra.getDescuento()
            );

            statement.setBigDecimal(
                    9,
                    compra.getImpuesto()
            );

            statement.setBigDecimal(
                    10,
                    compra.getTotal()
            );

            statement.setString(
                    11,
                    compra.getObservaciones()
            );

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {

                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al crear compra",
                    e
            );
        }

        throw new RuntimeException(
                "No fue posible obtener el ID de la compra"
        );
    }

    /**
     * Convierte un ResultSet en un objeto Compra.
     */
    private Compra mapearCompra(
            ResultSet rs) throws SQLException {

        Compra compra =
                new Compra();

        compra.setId(
                rs.getLong("id")
        );

        compra.setEmpresaId(
                rs.getLong("empresa_id")
        );

        compra.setSucursalId(
                rs.getLong("sucursal_id")
        );

        compra.setProveedorId(
                rs.getLong("proveedor_id")
        );

        int authUserId =
                rs.getInt("auth_user_id");

        if (!rs.wasNull()) {

            compra.setAuthUserId(
                    authUserId
            );
        }

        compra.setNumero(
                rs.getString("numero")
        );

        Timestamp fecha =
                rs.getTimestamp("fecha");

        if (fecha != null) {

            compra.setFecha(
                    fecha.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        compra.setEstado(
                rs.getString("estado")
        );

        compra.setSubtotal(
                rs.getBigDecimal("subtotal")
        );

        compra.setDescuento(
                rs.getBigDecimal("descuento")
        );

        compra.setImpuesto(
                rs.getBigDecimal("impuesto")
        );

        compra.setTotal(
                rs.getBigDecimal("total")
        );

        compra.setObservaciones(
                rs.getString("observaciones")
        );

        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {

            compra.setCreatedAt(
                    createdAt.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        Timestamp updatedAt =
                rs.getTimestamp("updated_at");

        if (updatedAt != null) {

            compra.setUpdatedAt(
                    updatedAt.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        return compra;
    }
}