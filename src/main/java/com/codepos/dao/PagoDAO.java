package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.model.Pago;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO encargado del acceso a datos
 * de la entidad Pago.
 *
 * Soporta:
 *
 * - Consultas individuales.
 * - Listados por venta.
 * - Inserción independiente.
 * - Inserción dentro de transacciones.
 *
 */
public class PagoDAO {


    /**
     * Busca un pago por ID.
     */
    public Pago buscarPorId(
            Long pagoId) {


        String sql = """
            SELECT
                id,
                venta_id,
                auth_user_id,
                metodo,
                monto,
                referencia,
                fecha,
                created_at
            FROM pagos
            WHERE id = ?
            """;


        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {


            ps.setLong(
                    1,
                    pagoId
            );


            try(
                    ResultSet rs =
                            ps.executeQuery()
            ){

                if(rs.next()){

                    return mapearPago(rs);

                }

            }


        } catch(SQLException e){

            throw new RuntimeException(
                    "Error al buscar pago por ID",
                    e
            );

        }


        return null;

    }







    /**
     * Lista pagos asociados a una venta.
     */
    public List<Pago> listarPorVenta(
            Long ventaId) {


        String sql = """
            SELECT
                id,
                venta_id,
                auth_user_id,
                metodo,
                monto,
                referencia,
                fecha,
                created_at
            FROM pagos
            WHERE venta_id = ?
            ORDER BY id
            """;


        List<Pago> pagos =
                new ArrayList<>();



        try(
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        connection.prepareStatement(sql)

        ){


            ps.setLong(
                    1,
                    ventaId
            );



            try(
                    ResultSet rs =
                            ps.executeQuery()
            ){


                while(rs.next()){


                    pagos.add(
                            mapearPago(rs)
                    );


                }

            }



        }catch(SQLException e){


            throw new RuntimeException(
                    "Error al listar pagos de venta",
                    e
            );

        }



        return pagos;

    }








    /**
     * Crea un pago usando conexión propia.
     *
     * Uso:
     *
     * - PagoService
     * - pruebas DAO
     */
    public Long crear(
            Pago pago){


        try(
                Connection connection =
                        ConexionBD.conectar()

        ){


            return crear(
                    connection,
                    pago
            );


        }catch(SQLException e){


            throw new RuntimeException(
                    "Error al crear pago",
                    e
            );

        }


    }








    /**
     * Crea pago dentro de una transacción existente.
     *
     * IMPORTANTE:
     *
     * No abre conexión.
     * No cierra conexión.
     * No hace commit.
     * No hace rollback.
     *
     * La transacción pertenece al Service.
     */
    public Long crear(
            Connection connection,
            Pago pago){



        String sql = """
            INSERT INTO pagos(
                venta_id,
                auth_user_id,
                metodo,
                monto,
                referencia
            )
            VALUES(
                ?,?,?,?,?
            )
            RETURNING id
            """;



        try(
                PreparedStatement ps =
                        connection.prepareStatement(sql)

        ){



            ps.setLong(
                    1,
                    pago.getVentaId()
            );





            if(pago.getAuthUserId()!=null){


                ps.setInt(
                        2,
                        pago.getAuthUserId()
                );


            }else{


                ps.setNull(
                        2,
                        Types.INTEGER
                );

            }







            ps.setString(
                    3,
                    pago.getMetodo()
            );






            ps.setBigDecimal(
                    4,
                    pago.getMonto()
            );







            if(pago.getReferencia()!=null
                    &&
                    !pago.getReferencia()
                            .isBlank()){


                ps.setString(
                        5,
                        pago.getReferencia()
                                .trim()
                );


            }else{


                ps.setNull(
                        5,
                        Types.VARCHAR
                );

            }







            try(
                    ResultSet rs =
                            ps.executeQuery()

            ){


                if(rs.next()){


                    return rs.getLong(
                            "id"
                    );


                }


            }





        }catch(SQLException e){


            throw new RuntimeException(
                    "Error al insertar pago",
                    e
            );

        }



        throw new RuntimeException(
                "No se pudo obtener el ID del pago"
        );


    }









    /**
     * Mapea ResultSet a entidad Pago.
     */
    private Pago mapearPago(
            ResultSet rs)
            throws SQLException {



        Pago pago =
                new Pago();



        pago.setId(
                rs.getLong("id")
        );



        pago.setVentaId(
                rs.getLong("venta_id")
        );





        int authUserId =
                rs.getInt(
                        "auth_user_id"
                );



        if(!rs.wasNull()){


            pago.setAuthUserId(
                    authUserId
            );


        }






        pago.setMetodo(
                rs.getString(
                        "metodo"
                )
        );



        pago.setMonto(
                rs.getBigDecimal(
                        "monto"
                )
        );



        pago.setReferencia(
                rs.getString(
                        "referencia"
                )
        );





        Timestamp fecha =
                rs.getTimestamp(
                        "fecha"
                );


        if(fecha!=null){


            pago.setFecha(
                    fecha.toInstant()
                            .atOffset(
                                    ZoneOffset.UTC
                            )
            );

        }






        Timestamp created =
                rs.getTimestamp(
                        "created_at"
                );



        if(created!=null){


            pago.setCreatedAt(
                    created.toInstant()
                            .atOffset(
                                    ZoneOffset.UTC
                            )
            );


        }




        return pago;

    }


}