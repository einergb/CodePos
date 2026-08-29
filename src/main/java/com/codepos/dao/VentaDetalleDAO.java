package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.VentaDetalle;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaDetalleDAO {

    /**
     * Busca un detalle de venta por su ID.
     */
    public VentaDetalle buscarPorId(Long detalleId) {

        String sql = """
            SELECT
                id,
                venta_id,
                producto_id,
                cantidad,
                precio_venta,
                descuento,
                impuesto,
                subtotal,
                created_at
            FROM venta_detalles
            WHERE id = ?
            """;

        try (
                Connection conexion =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setLong(1, detalleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearVentaDetalle(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar detalle de venta",
                    e
            );
        }

        return null;
    }

    /**
     * Lista todos los detalles asociados
     * a una venta.
     */
    public List<VentaDetalle> listarPorVenta(
            Long ventaId) {

        String sql = """
            SELECT
                id,
                venta_id,
                producto_id,
                cantidad,
                precio_venta,
                descuento,
                impuesto,
                subtotal,
                created_at
            FROM venta_detalles
            WHERE venta_id = ?
            ORDER BY id
            """;

        List<VentaDetalle> detalles =
                new ArrayList<>();

        try (
                Connection conexion =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setLong(1, ventaId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    detalles.add(
                            mapearVentaDetalle(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar detalles de venta",
                    e
            );
        }

        return detalles;
    }

    /**
     * Crea un nuevo detalle de venta.
     *
     * created_at no se incluye en el INSERT
     * porque PostgreSQL lo genera mediante
     * DEFAULT CURRENT_TIMESTAMP.
     */
    public Long crear(VentaDetalle detalle) {

        String sql = """
            INSERT INTO venta_detalles (
                venta_id,
                producto_id,
                cantidad,
                precio_venta,
                descuento,
                impuesto,
                subtotal
            )
            VALUES (
                ?, ?, ?, ?, ?, ?, ?
            )
            RETURNING id
            """;

        try (
                Connection conexion =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setLong(
                    1,
                    detalle.getVentaId()
            );

            ps.setLong(
                    2,
                    detalle.getProductoId()
            );

            ps.setBigDecimal(
                    3,
                    detalle.getCantidad()
            );

            ps.setBigDecimal(
                    4,
                    detalle.getPrecioVenta()
            );

            ps.setBigDecimal(
                    5,
                    detalle.getDescuento()
            );

            ps.setBigDecimal(
                    6,
                    detalle.getImpuesto()
            );

            ps.setBigDecimal(
                    7,
                    detalle.getSubtotal()
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al crear detalle de venta",
                    e
            );
        }

        throw new RuntimeException(
                "No se pudo obtener el ID del detalle creado"
        );
    }

    /**
     * Convierte un ResultSet en un objeto VentaDetalle.
     */
    private VentaDetalle mapearVentaDetalle(
            ResultSet rs)
            throws SQLException {

        VentaDetalle detalle =
                new VentaDetalle();

        detalle.setId(
                rs.getLong("id")
        );

        detalle.setVentaId(
                rs.getLong("venta_id")
        );

        detalle.setProductoId(
                rs.getLong("producto_id")
        );

        detalle.setCantidad(
                rs.getBigDecimal("cantidad")
        );

        detalle.setPrecioVenta(
                rs.getBigDecimal("precio_venta")
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

        detalle.setCreatedAt(
                rs.getObject(
                        "created_at",
                        OffsetDateTime.class
                )
        );

        return detalle;
    }


}
