package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Inventario;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * DAO encargado de gestionar el inventario.
 *
 * Responsabilidades:
 *
 * - Consultar existencia de productos.
 * - Bloquear inventario durante ventas.
 * - Actualizar cantidades.
 *
 * IMPORTANTE:
 *
 * Cuando se utiliza dentro de una venta integral,
 * debe trabajar con la misma conexión de la transacción.
 */
public class InventarioDAO {


    /**
     * Consulta inventario utilizando una conexión propia.
     *
     * Uso:
     *
     * - Consultas independientes.
     * - Pruebas.
     * - Servicios simples.
     */
    public Inventario buscarPorProducto(
            Long empresaId,
            Long sucursalId,
            Long productoId) {


        try (
                Connection connection =
                        ConexionBD.conectar()
        ) {


            return buscarPorProducto(
                    connection,
                    empresaId,
                    sucursalId,
                    productoId
            );


        } catch (SQLException e) {


            throw new RuntimeException(
                    "Error al consultar inventario",
                    e
            );
        }
    }



    /**
     * Consulta inventario dentro de una transacción.
     *
     * FOR UPDATE:
     *
     * Bloquea la fila del inventario evitando
     * ventas simultáneas que puedan generar
     * stock negativo.
     */
    public Inventario buscarPorProducto(
            Connection connection,
            Long empresaId,
            Long sucursalId,
            Long productoId) {


        validarParametros(
                connection,
                empresaId,
                sucursalId,
                productoId
        );


        String sql = """
            SELECT
                id,
                empresa_id,
                sucursal_id,
                producto_id,
                cantidad,
                stock_minimo,
                stock_maximo,
                activo,
                created_at,
                updated_at
            FROM inventarios
            WHERE empresa_id = ?
              AND sucursal_id = ?
              AND producto_id = ?
            FOR UPDATE
            """;


        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
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


            try(
                    ResultSet rs =
                            statement.executeQuery()
            ){

                if(rs.next()){

                    return mapearInventario(rs);
                }
            }


        } catch(SQLException e){


            throw new RuntimeException(
                    "Error al buscar inventario del producto",
                    e
            );
        }


        return null;
    }




    /**
     * Descuenta una cantidad del inventario.
     *
     * Se ejecuta dentro de la misma transacción
     * de la venta.
     */
    public void descontarStock(
            Connection connection,
            Long inventarioId,
            BigDecimal cantidad) {


        if(connection == null){

            throw new IllegalArgumentException(
                    "La conexión es obligatoria"
            );
        }


        if(inventarioId == null || inventarioId <= 0){

            throw new IllegalArgumentException(
                    "El inventario es obligatorio"
            );
        }


        if(cantidad == null
                || cantidad.compareTo(BigDecimal.ZERO) <= 0){

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }



        String sql = """
            UPDATE inventarios
            SET cantidad = cantidad - ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
              AND cantidad >= ?
            """;



        try(
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ){


            statement.setBigDecimal(
                    1,
                    cantidad
            );


            statement.setLong(
                    2,
                    inventarioId
            );


            statement.setBigDecimal(
                    3,
                    cantidad
            );



            int filas =
                    statement.executeUpdate();



            if(filas != 1){

                throw new IllegalStateException(
                        "No existe inventario suficiente para descontar stock"
                );
            }



        }catch(SQLException e){


            throw new RuntimeException(
                    "Error actualizando inventario",
                    e
            );
        }
    }




    /**
     * Convierte registro SQL a objeto Inventario.
     */
    private Inventario mapearInventario(
            ResultSet rs)
            throws SQLException {



        Inventario inventario =
                new Inventario();



        inventario.setId(
                rs.getLong("id")
        );


        inventario.setEmpresaId(
                rs.getLong("empresa_id")
        );


        inventario.setSucursalId(
                rs.getLong("sucursal_id")
        );


        inventario.setProductoId(
                rs.getLong("producto_id")
        );


        inventario.setCantidad(
                rs.getBigDecimal("cantidad")
        );


        inventario.setStockMinimo(
                rs.getBigDecimal("stock_minimo")
        );


        inventario.setStockMaximo(
                rs.getBigDecimal("stock_maximo")
        );


        inventario.setActivo(
                rs.getBoolean("activo")
        );



        inventario.setCreatedAt(
                rs.getObject(
                        "created_at",
                        java.time.OffsetDateTime.class
                )
        );



        inventario.setUpdatedAt(
                rs.getObject(
                        "updated_at",
                        java.time.OffsetDateTime.class
                )
        );


        return inventario;
    }




    /**
     * Validaciones comunes.
     */
    private void validarParametros(
            Connection connection,
            Long empresaId,
            Long sucursalId,
            Long productoId) {


        if(connection == null){

            throw new IllegalArgumentException(
                    "La conexión es obligatoria"
            );
        }


        if(empresaId == null || empresaId <= 0){

            throw new IllegalArgumentException(
                    "La empresa es obligatoria"
            );
        }


        if(sucursalId == null || sucursalId <= 0){

            throw new IllegalArgumentException(
                    "La sucursal es obligatoria"
            );
        }


        if(productoId == null || productoId <= 0){

            throw new IllegalArgumentException(
                    "El producto es obligatorio"
            );
        }
    }
}