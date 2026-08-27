package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    /**
     * Busca un proveedor por su ID y empresa.
     */
    public Proveedor buscarPorId(
            Long empresaId,
            Long proveedorId) {

        String sql = """
                SELECT
                    id,
                    empresa_id,
                    nombre,
                    identificacion,
                    telefono,
                    correo,
                    direccion,
                    activo,
                    created_at,
                    updated_at
                FROM proveedores
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
            statement.setLong(2, proveedorId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return mapearProveedor(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar proveedor",
                    e
            );
        }

        return null;
    }

    /**
     * Lista los proveedores de una empresa.
     */
    public List<Proveedor> listarPorEmpresa(
            Long empresaId) {

        String sql = """
                SELECT
                    id,
                    empresa_id,
                    nombre,
                    identificacion,
                    telefono,
                    correo,
                    direccion,
                    activo,
                    created_at,
                    updated_at
                FROM proveedores
                WHERE empresa_id = ?
                ORDER BY id
                """;

        List<Proveedor> proveedores =
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

                    proveedores.add(
                            mapearProveedor(rs)
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al listar proveedores",
                    e
            );
        }

        return proveedores;
    }

    /**
     * Crea un nuevo proveedor.
     */
    public Long crear(Proveedor proveedor) {

        String sql = """
                INSERT INTO proveedores (
                    empresa_id,
                    nombre,
                    identificacion,
                    telefono,
                    correo,
                    direccion
                )
                VALUES (?, ?, ?, ?, ?, ?)
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
                    proveedor.getEmpresaId()
            );

            statement.setString(
                    2,
                    proveedor.getNombre()
            );

            statement.setString(
                    3,
                    proveedor.getIdentificacion()
            );

            statement.setString(
                    4,
                    proveedor.getTelefono()
            );

            statement.setString(
                    5,
                    proveedor.getCorreo()
            );

            statement.setString(
                    6,
                    proveedor.getDireccion()
            );

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al crear proveedor",
                    e
            );
        }

        throw new RuntimeException(
                "No fue posible obtener el ID del proveedor"
        );
    }

    /**
     * Convierte un ResultSet en un objeto Proveedor.
     */
    private Proveedor mapearProveedor(
            ResultSet rs) throws SQLException {

        Proveedor proveedor =
                new Proveedor();

        proveedor.setId(
                rs.getLong("id")
        );

        proveedor.setEmpresaId(
                rs.getLong("empresa_id")
        );

        proveedor.setNombre(
                rs.getString("nombre")
        );

        proveedor.setIdentificacion(
                rs.getString("identificacion")
        );

        proveedor.setTelefono(
                rs.getString("telefono")
        );

        proveedor.setCorreo(
                rs.getString("correo")
        );

        proveedor.setDireccion(
                rs.getString("direccion")
        );

        proveedor.setActivo(
                rs.getBoolean("activo")
        );

        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {
            proveedor.setCreatedAt(
                    createdAt.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        Timestamp updatedAt =
                rs.getTimestamp("updated_at");

        if (updatedAt != null) {
            proveedor.setUpdatedAt(
                    updatedAt.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        return proveedor;
    }
}
