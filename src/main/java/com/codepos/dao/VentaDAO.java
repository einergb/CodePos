package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Venta;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO encargado de la persistencia
 * de ventas.
 *
 * Soporta:
 *
 * - CRUD básico.
 * - Multiempresa.
 * - Transacciones externas.
 * - Flujo POS integral.
 */
public class VentaDAO {


    /**
     * Buscar venta por empresa e ID.
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


            try(ResultSet rs = ps.executeQuery()){


                if(rs.next()){

                    return mapearVenta(rs);

                }

            }


        }catch(SQLException e){

            throw new RuntimeException(
                    "Error buscando venta",
                    e
            );

        }


        return null;

    }





    /**
     * Lista ventas por empresa.
     */
    public List<Venta> listarPorEmpresa(
            Long empresaId){


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
            ORDER BY id DESC
            """;


        List<Venta> ventas =
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


            try(ResultSet rs =
                        ps.executeQuery()){


                while(rs.next()){

                    ventas.add(
                            mapearVenta(rs)
                    );

                }

            }


        }catch(SQLException e){

            throw new RuntimeException(
                    "Error listando ventas",
                    e
            );

        }


        return ventas;

    }





    /**
     * Crear venta usando conexión propia.
     */
    public Long crear(
            Venta venta){


        try(
                Connection connection =
                        ConexionBD.conectar()

        ){


            return crear(
                    connection,
                    venta
            );


        }catch(SQLException e){


            throw new RuntimeException(
                    "Error creando venta",
                    e
            );

        }

    }





    /**
     * Crear venta dentro de una transacción existente.
     *
     * No abre conexión.
     * No hace commit.
     * No hace rollback.
     */
    public Long crear(
            Connection connection,
            Venta venta){


        String sql = """
            INSERT INTO ventas
            (
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
            VALUES
            (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?
            )
            RETURNING id
            """;



        try(
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ){



            ps.setLong(
                    1,
                    venta.getEmpresaId()
            );



            ps.setLong(
                    2,
                    venta.getSucursalId()
            );




            if(venta.getClienteId()!=null){


                ps.setLong(
                        3,
                        venta.getClienteId()
                );


            }else{


                ps.setNull(
                        3,
                        Types.BIGINT
                );

            }





            if(venta.getAuthUserId()!=null){


                ps.setInt(
                        4,
                        venta.getAuthUserId()
                );


            }else{


                ps.setNull(
                        4,
                        Types.INTEGER
                );

            }





            ps.setString(
                    5,
                    venta.getNumero()
                            .trim()
            );





            ps.setString(
                    6,
                    venta.getEstado()
            );





            ps.setBigDecimal(
                    7,
                    valorSeguro(
                            venta.getSubtotal()
                    )
            );


            ps.setBigDecimal(
                    8,
                    valorSeguro(
                            venta.getDescuento()
                    )
            );


            ps.setBigDecimal(
                    9,
                    valorSeguro(
                            venta.getImpuesto()
                    )
            );


            ps.setBigDecimal(
                    10,
                    valorSeguro(
                            venta.getTotal()
                    )
            );





            if(venta.getObservaciones()!=null
                    &&
                    !venta.getObservaciones()
                            .isBlank()){


                ps.setString(
                        11,
                        venta.getObservaciones()
                                .trim()
                );


            }else{


                ps.setNull(
                        11,
                        Types.VARCHAR
                );

            }





            try(ResultSet rs =
                        ps.executeQuery()){


                if(rs.next()){

                    return rs.getLong("id");

                }

            }



        }catch(SQLException e){


            throw new RuntimeException(
                    "Error insertando venta",
                    e
            );

        }



        throw new RuntimeException(
                "No se generó ID de venta"
        );

    }







    /**
     * Marca venta como pagada.
     */
    public void marcarComoPagada(
            Connection connection,
            Long empresaId,
            Long ventaId){



        String sql = """
            UPDATE ventas
            SET estado='PAGADA',
                updated_at=CURRENT_TIMESTAMP
            WHERE empresa_id=?
              AND id=?
            """;



        try(
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



            int filas =
                    ps.executeUpdate();



            if(filas!=1){


                throw new IllegalStateException(
                        "No se pudo actualizar la venta a PAGADA"
                );

            }



        }catch(SQLException e){


            throw new RuntimeException(
                    "Error actualizando estado de venta",
                    e
            );

        }

    }






    /**
     * Mapea venta.
     */
    private Venta mapearVenta(
            ResultSet rs)
            throws SQLException {



        Venta venta =
                new Venta();



        venta.setId(
                rs.getLong("id")
        );


        venta.setEmpresaId(
                rs.getLong("empresa_id")
        );


        venta.setSucursalId(
                rs.getLong("sucursal_id")
        );




        long cliente =
                rs.getLong("cliente_id");


        if(!rs.wasNull()){


            venta.setClienteId(
                    cliente
            );

        }




        int usuario =
                rs.getInt("auth_user_id");


        if(!rs.wasNull()){


            venta.setAuthUserId(
                    usuario
            );

        }





        venta.setNumero(
                rs.getString("numero")
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



        venta.setFecha(
                obtenerFecha(
                        rs,
                        "fecha"
                )
        );


        venta.setCreatedAt(
                obtenerFecha(
                        rs,
                        "created_at"
                )
        );


        venta.setUpdatedAt(
                obtenerFecha(
                        rs,
                        "updated_at"
                )
        );



        return venta;

    }






    private OffsetDateTime obtenerFecha(
            ResultSet rs,
            String campo)
            throws SQLException {


        Timestamp fecha =
                rs.getTimestamp(campo);


        if(fecha==null){

            return null;

        }


        return fecha.toInstant()
                .atOffset(
                        java.time.ZoneOffset.UTC
                );

    }





    private java.math.BigDecimal valorSeguro(
            java.math.BigDecimal valor){


        return valor == null
                ?
                java.math.BigDecimal.ZERO
                :
                valor;

    }

}