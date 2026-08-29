package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    /**
     * Busca un producto por empresa e ID.
     */
    public Producto buscarPorId(
            Long empresaId,
            Long productoId) {

        String sql = """
                SELECT
                    id,
                    empresa_id,
                    categoria_id,
                    marca_id,
                    unidad_medida_id,
                    sku,
                    codigo_barras,
                    nombre,
                    descripcion,
                    precio_compra,
                    precio_venta,
                    aplica_iva,
                    iva_porcentaje,
                    activo,
                    created_at,
                    updated_at
                FROM productos
                WHERE empresa_id = ?
                  AND id = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);
            statement.setLong(2, productoId);

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar producto",
                    e
            );
        }

        return null;
    }

    /**
     * Busca un producto por SKU.
     */
    public Producto buscarPorSku(
            Long empresaId,
            String sku) {

        String sql = """
                SELECT
                    id,
                    empresa_id,
                    categoria_id,
                    marca_id,
                    unidad_medida_id,
                    sku,
                    codigo_barras,
                    nombre,
                    descripcion,
                    precio_compra,
                    precio_venta,
                    aplica_iva,
                    iva_porcentaje,
                    activo,
                    created_at,
                    updated_at
                FROM productos
                WHERE empresa_id = ?
                  AND sku = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);
            statement.setString(2, sku);

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar producto por SKU",
                    e
            );
        }

        return null;
    }

    /**
     * Lista los productos de una empresa.
     */
    public List<Producto> listarPorEmpresa(
            Long empresaId) {

        String sql = """
                SELECT
                    id,
                    empresa_id,
                    categoria_id,
                    marca_id,
                    unidad_medida_id,
                    sku,
                    codigo_barras,
                    nombre,
                    descripcion,
                    precio_compra,
                    precio_venta,
                    aplica_iva,
                    iva_porcentaje,
                    activo,
                    created_at,
                    updated_at
                FROM productos
                WHERE empresa_id = ?
                ORDER BY id
                """;

        List<Producto> productos =
                new ArrayList<>();

        try (
                Connection conexion =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setLong(1, empresaId);

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                while (rs.next()) {

                    productos.add(
                            mapearProducto(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar productos",
                    e
            );
        }

        return productos;
    }

    /**
     * Crea un producto.
     *
     * PostgreSQL genera:
     * - id
     * - created_at
     * - updated_at
     */
    public Long crear(Producto producto) {

        String sql = """
                INSERT INTO productos (
                    empresa_id,
                    categoria_id,
                    marca_id,
                    unidad_medida_id,
                    sku,
                    codigo_barras,
                    nombre,
                    descripcion,
                    precio_compra,
                    precio_venta,
                    aplica_iva,
                    iva_porcentaje,
                    activo
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?
                )
                RETURNING id
                """;

        try (
                Connection conexion =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    producto.getEmpresaId()
            );

            if (producto.getCategoriaId() != null) {
                statement.setLong(
                        2,
                        producto.getCategoriaId()
                );
            } else {
                statement.setNull(
                        2,
                        Types.BIGINT
                );
            }

            if (producto.getMarcaId() != null) {
                statement.setLong(
                        3,
                        producto.getMarcaId()
                );
            } else {
                statement.setNull(
                        3,
                        Types.BIGINT
                );
            }

            statement.setLong(
                    4,
                    producto.getUnidadMedidaId()
            );

            statement.setString(
                    5,
                    producto.getSku()
            );

            if (producto.getCodigoBarras() != null) {
                statement.setString(
                        6,
                        producto.getCodigoBarras()
                );
            } else {
                statement.setNull(
                        6,
                        Types.VARCHAR
                );
            }

            statement.setString(
                    7,
                    producto.getNombre()
            );

            if (producto.getDescripcion() != null) {
                statement.setString(
                        8,
                        producto.getDescripcion()
                );
            } else {
                statement.setNull(
                        8,
                        Types.VARCHAR
                );
            }

            statement.setBigDecimal(
                    9,
                    producto.getPrecioCompra()
            );

            statement.setBigDecimal(
                    10,
                    producto.getPrecioVenta()
            );

            statement.setBoolean(
                    11,
                    producto.getAplicaIva()
            );

            statement.setBigDecimal(
                    12,
                    producto.getIvaPorcentaje()
            );

            statement.setBoolean(
                    13,
                    producto.getActivo()
            );

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                if (rs.next()) {
                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al crear producto",
                    e
            );
        }

        throw new RuntimeException(
                "No se pudo obtener el ID del producto creado"
        );
    }

    /**
     * Convierte un ResultSet en Producto.
     */
    private Producto mapearProducto(
            ResultSet rs)
            throws SQLException {

        Producto producto =
                new Producto();

        producto.setId(
                rs.getLong("id")
        );

        producto.setEmpresaId(
                rs.getLong("empresa_id")
        );

        long categoriaId =
                rs.getLong("categoria_id");

        if (!rs.wasNull()) {
            producto.setCategoriaId(
                    categoriaId
            );
        }

        long marcaId =
                rs.getLong("marca_id");

        if (!rs.wasNull()) {
            producto.setMarcaId(
                    marcaId
            );
        }

        producto.setUnidadMedidaId(
                rs.getLong("unidad_medida_id")
        );

        producto.setSku(
                rs.getString("sku")
        );

        producto.setCodigoBarras(
                rs.getString("codigo_barras")
        );

        producto.setNombre(
                rs.getString("nombre")
        );

        producto.setDescripcion(
                rs.getString("descripcion")
        );

        producto.setPrecioCompra(
                rs.getBigDecimal("precio_compra")
        );

        producto.setPrecioVenta(
                rs.getBigDecimal("precio_venta")
        );

        producto.setAplicaIva(
                rs.getBoolean("aplica_iva")
        );

        producto.setIvaPorcentaje(
                rs.getBigDecimal("iva_porcentaje")
        );

        producto.setActivo(
                rs.getBoolean("activo")
        );

        producto.setCreatedAt(
                rs.getObject(
                        "created_at",
                        java.time.OffsetDateTime.class
                )
        );

        producto.setUpdatedAt(
                rs.getObject(
                        "updated_at",
                        java.time.OffsetDateTime.class
                )
        );

        return producto;
    }
}