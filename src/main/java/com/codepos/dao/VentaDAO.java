package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Venta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    /**
     * Busca una venta por empresa e ID.
     */
    public Venta buscarPorId(
            Long empresaId,
            Long ventaId) {

        String sql = """
        SELECT
            id,
            empresa_id,
            sucursal_id,
            cliente_id,
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
        FROM ventas
        WHERE empresa_id = ?
          AND id = ?
        """;

        try (
                Connection conexion = ConexionBD.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setLong(1, empresaId);
            ps.setLong(2, ventaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearVenta(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar venta",
                    e
            );
        }

        return null;
    }

    /**
     * Lista las ventas de una empresa.
     */
    public List<Venta> listarPorEmpresa(
            Long empresaId) {

        String sql = """
        SELECT
            id,
            empresa_id,
            sucursal_id,
            cliente_id,
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
        FROM ventas
        WHERE empresa_id = ?
        ORDER BY id
        """;

        List<Venta> ventas = new ArrayList<>();

        try (
                Connection conexion = ConexionBD.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setLong(1, empresaId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ventas.add(
                            mapearVenta(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar ventas",
                    e
            );
        }

        return ventas;
    }

    /**
     * Crea una venta utilizando una conexión propia.
     *
     * Este método mantiene compatibilidad con:
     *
     * - VentaService
     * - TestVentaDAO
     * - Otros servicios que creen una venta individualmente
     *
     * La transacción integral utilizará el método
     * crear(Connection, Venta).
     */
    public Long crear(Venta venta) {

        try (Connection conexion = ConexionBD.conectar()) {

            return crear(
                    conexion,
                    venta
            );

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al crear venta",
                    e
            );
        }
    }

    /**
     * Crea una venta utilizando una conexión existente.
     *
     * Este método está diseñado para operaciones
     * transaccionales donde varias operaciones DAO
     * deben utilizar la misma conexión.
     *
     * IMPORTANTE:
     *
     * Este método NO abre ni cierra la conexión.
     * Tampoco realiza commit ni rollback.
     *
     * La conexión y la transacción son responsabilidad
     * del servicio que invoca este método.
     */
    public Long crear(
            Connection conexion,
            Venta venta) {

        String sql = """
        INSERT INTO ventas (
            empresa_id,
            sucursal_id,
            cliente_id,
            auth_user_id,
            numero,
            estado,
            subtotal,
            descuento,
            impuesto,
            total,
            observaciones
        )
        VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        )
        RETURNING id
        """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            // Empresa
            ps.setLong(
                    1,
                    venta.getEmpresaId()
            );

            // Sucursal
            ps.setLong(
                    2,
                    venta.getSucursalId()
            );

            // Cliente opcional
            if (venta.getClienteId() != null) {

                ps.setLong(
                        3,
                        venta.getClienteId()
                );

            } else {

                ps.setNull(
                        3,
                        Types.BIGINT
                );
            }

            // Usuario autenticado opcional
            if (venta.getAuthUserId() != null) {

                ps.setInt(
                        4,
                        venta.getAuthUserId()
                );

            } else {

                ps.setNull(
                        4,
                        Types.INTEGER
                );
            }

            // Número
            ps.setString(
                    5,
                    venta.getNumero()
            );

            // Estado
            ps.setString(
                    6,
                    venta.getEstado()
            );

            // Subtotal
            ps.setBigDecimal(
                    7,
                    venta.getSubtotal()
            );

            // Descuento
            ps.setBigDecimal(
                    8,
                    venta.getDescuento()
            );

            // Impuesto
            ps.setBigDecimal(
                    9,
                    venta.getImpuesto()
            );

            // Total
            ps.setBigDecimal(
                    10,
                    venta.getTotal()
            );

            // Observaciones opcionales
            if (venta.getObservaciones() != null) {

                ps.setString(
                        11,
                        venta.getObservaciones()
                );

            } else {

                ps.setNull(
                        11,
                        Types.VARCHAR
                );
            }

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al crear venta",
                    e
            );
        }

        throw new RuntimeException(
                "No se pudo obtener el ID de la venta creada"
        );
    }

    /**
     * Convierte un ResultSet en un objeto Venta.
     */
    private Venta mapearVenta(
            ResultSet rs)
            throws SQLException {

        Venta venta = new Venta();

        venta.setId(
                rs.getLong("id")
        );

        venta.setEmpresaId(
                rs.getLong("empresa_id")
        );

        venta.setSucursalId(
                rs.getLong("sucursal_id")
        );

        // Cliente opcional
        long clienteId =
                rs.getLong("cliente_id");

        if (!rs.wasNull()) {

            venta.setClienteId(
                    clienteId
            );
        }

        // Usuario autenticado opcional
        int authUserId =
                rs.getInt("auth_user_id");

        if (!rs.wasNull()) {

            venta.setAuthUserId(
                    authUserId
            );
        }

        venta.setNumero(
                rs.getString("numero")
        );

        venta.setFecha(
                rs.getObject(
                        "fecha",
                        java.time.OffsetDateTime.class
                )
        );

        venta.setEstado(
                rs.getString("estado")
        );

        venta.setSubtotal(
                rs.getBigDecimal("subtotal")
        );

        venta.setDescuento(
                rs.getBigDecimal("descuento")
        );

        venta.setImpuesto(
                rs.getBigDecimal("impuesto")
        );

        venta.setTotal(
                rs.getBigDecimal("total")
        );

        venta.setObservaciones(
                rs.getString("observaciones")
        );

        venta.setCreatedAt(
                rs.getObject(
                        "created_at",
                        java.time.OffsetDateTime.class
                )
        );

        venta.setUpdatedAt(
                rs.getObject(
                        "updated_at",
                        java.time.OffsetDateTime.class
                )
        );

        return venta;
    }
    public void marcarComoPagada(
            Connection connection,
            Long empresaId,
            Long ventaId) {

        String sql = """
            UPDATE ventas
            SET estado = 'PAGADA',
                updated_at = CURRENT_TIMESTAMP
            WHERE empresa_id = ?
              AND id = ?
              AND estado = 'REGISTRADA'
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);
            statement.setLong(2, ventaId);

            int filas =
                    statement.executeUpdate();

            if (filas != 1) {

                throw new RuntimeException(
                        "La venta no existe, no pertenece a la empresa o no está en estado REGISTRADA"
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al marcar la venta como PAGADA",
                    e
            );
        }
    }
}