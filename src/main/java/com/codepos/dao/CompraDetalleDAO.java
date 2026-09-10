package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.CompraDetalle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDetalleDAO {

    /**
     * Busca un detalle por su ID, verificando que la compra
     * a la que pertenece sea de la empresa indicada.
     *
     * compra_detalles no tiene empresa_id propio: la
     * trazabilidad de empresa llega vía compra_id, por eso
     * el filtro se resuelve con JOIN a compras.
     */
    public CompraDetalle buscarPorId(
            Long empresaId,
            Long detalleId) {

        String sql = """
                SELECT
                    cd.id,
                    cd.compra_id,
                    cd.producto_id,
                    cd.cantidad,
                    cd.precio_compra,
                    cd.descuento,
                    cd.impuesto,
                    cd.subtotal,
                    cd.created_at
                FROM compra_detalles cd
                JOIN compras c
                  ON c.id = cd.compra_id
                WHERE c.empresa_id = ?
                  AND cd.id = ?
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);
            statement.setLong(2, detalleId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return mapearDetalle(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar detalle de compra",
                    e
            );
        }

        return null;
    }

    /**
     * Lista todos los detalles pertenecientes a una compra,
     * verificando que la compra sea de la empresa indicada.
     */
    public List<CompraDetalle> listarPorCompra(
            Long empresaId,
            Long compraId) {

        String sql = """
                SELECT
                    cd.id,
                    cd.compra_id,
                    cd.producto_id,
                    cd.cantidad,
                    cd.precio_compra,
                    cd.descuento,
                    cd.impuesto,
                    cd.subtotal,
                    cd.created_at
                FROM compra_detalles cd
                JOIN compras c
                  ON c.id = cd.compra_id
                WHERE c.empresa_id = ?
                  AND cd.compra_id = ?
                ORDER BY cd.id
                """;

        List<CompraDetalle> detalles =
                new ArrayList<>();

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

                while (rs.next()) {

                    detalles.add(
                            mapearDetalle(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar detalles de compra",
                    e
            );
        }

        return detalles;
    }

    /**
     * Crea un nuevo detalle de compra.
     *
     * No requiere empresaId aquí: la pertenencia a la empresa
     * ya se valida en CompraDetalleService.crear() antes de
     * llamar a este método (verifica que la compra padre sea
     * de la empresa indicada).
     */
    public Long crear(
            CompraDetalle detalle) {

        String sql = """
                INSERT INTO compra_detalles (
                    compra_id,
                    producto_id,
                    cantidad,
                    precio_compra,
                    descuento,
                    impuesto,
                    subtotal
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
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
                    detalle.getCompraId()
            );

            statement.setLong(
                    2,
                    detalle.getProductoId()
            );

            statement.setBigDecimal(
                    3,
                    detalle.getCantidad()
            );

            statement.setBigDecimal(
                    4,
                    detalle.getPrecioCompra()
            );

            statement.setBigDecimal(
                    5,
                    detalle.getDescuento()
            );

            statement.setBigDecimal(
                    6,
                    detalle.getImpuesto()
            );

            statement.setBigDecimal(
                    7,
                    detalle.getSubtotal()
            );

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {

                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al crear detalle de compra",
                    e
            );
        }

        throw new RuntimeException(
                "No fue posible obtener el ID del detalle"
        );
    }

    /**
     * Convierte un ResultSet en CompraDetalle.
     */
    private CompraDetalle mapearDetalle(
            ResultSet rs) throws SQLException {

        CompraDetalle detalle =
                new CompraDetalle();

        detalle.setId(
                rs.getLong("id")
        );

        detalle.setCompraId(
                rs.getLong("compra_id")
        );

        detalle.setProductoId(
                rs.getLong("producto_id")
        );

        detalle.setCantidad(
                rs.getBigDecimal("cantidad")
        );

        detalle.setPrecioCompra(
                rs.getBigDecimal("precio_compra")
        );

        detalle.setDescuento(
                rs.getBigDecimal("descuento")
        );

        detalle.setImpuesto(
                rs.getBigDecimal("impuesto")
        );

        detalle.setSubtotal(
                rs.getBigDecimal("subtotal")
        );

        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {

            detalle.setCreatedAt(
                    createdAt.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );
        }

        return detalle;
    }
}