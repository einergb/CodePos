package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.VentaDetalle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO encargado de la persistencia
 * de detalles de venta.
 *
 * Soporta:
 *
 * - Consultas individuales (filtradas por empresa vía JOIN a ventas).
 * - Listados (idem).
 * - Creación independiente.
 * - Creación dentro de transacciones.
 *
 */
public class VentaDetalleDAO {


    /**
     * Busca un detalle por ID, verificando que la venta
     * a la que pertenece sea de la empresa indicada.
     *
     * venta_detalles no tiene empresa_id propio: la
     * trazabilidad de empresa llega vía venta_id, por eso
     * el filtro se resuelve con JOIN a ventas.
     */
    public VentaDetalle buscarPorId(
            Long empresaId,
            Long detalleId) {


        String sql = """
            SELECT
                vd.id,
                vd.venta_id,
                vd.producto_id,
                vd.cantidad,
                vd.precio_venta,
                vd.descuento,
                vd.impuesto,
                vd.subtotal,
                vd.created_at
            FROM venta_detalles vd
            JOIN ventas v
              ON v.id = vd.venta_id
            WHERE v.empresa_id = ?
              AND vd.id = ?
            """;


        try(
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ){


            ps.setLong(
                    1,
                    empresaId
            );

            ps.setLong(
                    2,
                    detalleId
            );


            try(ResultSet rs =
                        ps.executeQuery()){


                if(rs.next()){

                    return mapearVentaDetalle(
                            rs
                    );

                }

            }


        }catch(SQLException e){


            throw new RuntimeException(
                    "Error al buscar detalle de venta",
                    e
            );

        }


        return null;

    }





    /**
     * Lista detalles de una venta, verificando que la
     * venta sea de la empresa indicada.
     */
    public List<VentaDetalle> listarPorVenta(
            Long empresaId,
            Long ventaId){


        String sql = """
            SELECT
                vd.id,
                vd.venta_id,
                vd.producto_id,
                vd.cantidad,
                vd.precio_venta,
                vd.descuento,
                vd.impuesto,
                vd.subtotal,
                vd.created_at
            FROM venta_detalles vd
            JOIN ventas v
              ON v.id = vd.venta_id
            WHERE v.empresa_id = ?
              AND vd.venta_id = ?
            ORDER BY vd.id
            """;


        List<VentaDetalle> detalles =
                new ArrayList<>();



        try(
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        connection.prepareStatement(sql)

        ){


            ps.setLong(
                    1,
                    empresaId
            );

            ps.setLong(
                    2,
                    ventaId
            );



            try(ResultSet rs =
                        ps.executeQuery()){


                while(rs.next()){


                    detalles.add(
                            mapearVentaDetalle(rs)
                    );


                }

            }


        }catch(SQLException e){


            throw new RuntimeException(
                    "Error al listar detalles de venta",
                    e
            );

        }


        return detalles;

    }







    /**
     * Crea detalle con conexión propia.
     *
     * No requiere empresaId aquí: la pertenencia a la
     * empresa ya se valida antes de llamar a este método
     * (VentaIntegralService construye el detalle a partir
     * de una venta ya validada contra su empresa).
     *
     * Compatible con pruebas unitarias
     * y servicios independientes.
     */
    public Long crear(
            VentaDetalle detalle){



        try(
                Connection connection =
                        ConexionBD.conectar()

        ){


            return crear(
                    connection,
                    detalle
            );



        }catch(SQLException e){


            throw new RuntimeException(
                    "Error al crear detalle de venta",
                    e
            );

        }

    }









    /**
     * Crea detalle usando conexión existente.
     *
     * Utilizado por VentaIntegralService.
     *
     * NO:
     *
     * - abre conexión
     * - cierra conexión
     * - hace commit
     * - hace rollback
     *
     */
    public Long crear(
            Connection connection,
            VentaDetalle detalle){



        String sql = """
            INSERT INTO venta_detalles(
                venta_id,
                producto_id,
                cantidad,
                precio_venta,
                descuento,
                impuesto,
                subtotal
            )
            VALUES(
                ?,?,?,?,?,?,?
            )
            RETURNING id
            """;



        try(
                PreparedStatement ps =
                        connection.prepareStatement(sql)

        ){



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
                    valorSeguro(
                            detalle.getDescuento()
                    )
            );



            ps.setBigDecimal(
                    6,
                    valorSeguro(
                            detalle.getImpuesto()
                    )
            );



            ps.setBigDecimal(
                    7,
                    detalle.getSubtotal()
            );





            try(ResultSet rs =
                        ps.executeQuery()){


                if(rs.next()){


                    return rs.getLong("id");


                }


            }



        }catch(SQLException e){


            throw new RuntimeException(
                    "Error al insertar detalle de venta",
                    e
            );


        }



        throw new RuntimeException(
                "No se pudo obtener ID del detalle"
        );

    }









    /**
     * Convierte ResultSet a entidad.
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
                valorSeguro(
                        rs.getBigDecimal("descuento")
                )
        );



        detalle.setImpuesto(
                valorSeguro(
                        rs.getBigDecimal("impuesto")
                )
        );



        detalle.setSubtotal(
                rs.getBigDecimal("subtotal")
        );



        Timestamp fecha =
                rs.getTimestamp(
                        "created_at"
                );


        if(fecha != null){


            detalle.setCreatedAt(
                    fecha.toInstant()
                            .atOffset(
                                    java.time.ZoneOffset.UTC
                            )
            );

        }



        return detalle;

    }









    /**
     * Evita trabajar con NULL
     * en cálculos monetarios.
     */
    private java.math.BigDecimal valorSeguro(
            java.math.BigDecimal valor){


        return valor == null
                ? java.math.BigDecimal.ZERO
                : valor;


    }


}