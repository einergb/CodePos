package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    /**
     * Busca un cliente por su ID y empresa.
     */
    public Cliente buscarPorId(
            Long empresaId,
            Long clienteId) {

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
            FROM clientes
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
            statement.setLong(2, clienteId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar cliente",
                    e
            );
        }

        return null;
    }

    /**
     * Lista los clientes de una empresa.
     */
    public List<Cliente> listarPorEmpresa(
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
            FROM clientes
            WHERE empresa_id = ?
            ORDER BY id
            """;

        List<Cliente> clientes =
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

                    clientes.add(
                            mapearCliente(rs)
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al listar clientes",
                    e
            );
        }

        return clientes;
    }

    /**
     * Crea un nuevo cliente.
     */
    public Long crear(Cliente cliente) {

        String sql = """
            INSERT INTO clientes (
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
                    cliente.getEmpresaId()
            );

            statement.setString(
                    2,
                    cliente.getNombre()
            );

            statement.setString(
                    3,
                    cliente.getIdentificacion()
            );

            statement.setString(
                    4,
                    cliente.getTelefono()
            );

            statement.setString(
                    5,
                    cliente.getCorreo()
            );

            statement.setString(
                    6,
                    cliente.getDireccion()
            );

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al crear cliente",
                    e
            );
        }

        throw new RuntimeException(
                "No fue posible obtener el ID del cliente"
        );
    }

    /**
     * Convierte un ResultSet en un objeto Cliente.
     */
    private Cliente mapearCliente(
            ResultSet rs) throws SQLException {

        Cliente cliente =
                new Cliente();

        cliente.setId(
                rs.getLong("id")
        );

        cliente.setEmpresaId(
                rs.getLong("empresa_id")
        );

        cliente.setNombre(
                rs.getString("nombre")
        );

        cliente.setIdentificacion(
                rs.getString("identificacion")
        );

        cliente.setTelefono(
                rs.getString("telefono")
        );

        cliente.setCorreo(
                rs.getString("correo")
        );

        cliente.setDireccion(
                rs.getString("direccion")
        );

        cliente.setActivo(
                rs.getBoolean("activo")
        );

        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {
            cliente.setCreatedAt(
                    createdAt.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        Timestamp updatedAt =
                rs.getTimestamp("updated_at");

        if (updatedAt != null) {
            cliente.setUpdatedAt(
                    updatedAt.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        return cliente;
    }

}
