package com.codepos.dao;

import com.codepos.config.ConexionBD;
import com.codepos.enums.TipoMovimientoInventario;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * DAO encargado del registro de movimientos
 * de inventario.
 *
 * La lógica principal se ejecuta en PostgreSQL
 * mediante:
 *
 * registrar_movimiento_inventario()
 *
 * Responsabilidades PostgreSQL:
 *
 * - Bloquear inventario.
 * - Validar existencia.
 * - Validar stock.
 * - Actualizar cantidad.
 * - Registrar kardex.
 *
 * Este DAO únicamente comunica Java con BD.
 */
public class MovimientoInventarioDAO {


    /**
     * Registro independiente.
     *
     * Crea su propia conexión.
     */
    public Long registrarMovimiento(
            Long empresaId,
            Long sucursalId,
            Long productoId,
            TipoMovimientoInventario tipo,
            BigDecimal cantidad,
            String motivo,
            String referenciaTipo,
            Long referenciaId,
            Integer authUserId) {


        try (
                Connection connection =
                        ConexionBD.conectar()
        ) {


            return registrarMovimiento(
                    connection,
                    empresaId,
                    sucursalId,
                    productoId,
                    tipo,
                    cantidad,
                    motivo,
                    referenciaTipo,
                    referenciaId,
                    authUserId
            );


        } catch (SQLException e) {


            throw new RuntimeException(
                    "Error registrando movimiento de inventario",
                    e
            );
        }
    }




    /**
     * Registro dentro de una transacción existente.
     *
     * IMPORTANTE:
     *
     * Este método:
     *
     * - No abre conexión.
     * - No cierra conexión.
     * - No realiza commit.
     * - No realiza rollback.
     *
     * La transacción pertenece al Service.
     */
    public Long registrarMovimiento(
            Connection connection,
            Long empresaId,
            Long sucursalId,
            Long productoId,
            TipoMovimientoInventario tipo,
            BigDecimal cantidad,
            String motivo,
            String referenciaTipo,
            Long referenciaId,
            Integer authUserId) {


        validarDatos(
                connection,
                empresaId,
                sucursalId,
                productoId,
                tipo,
                cantidad
        );



        String sql = """
                SELECT registrar_movimiento_inventario(
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
                """;



        try (
                CallableStatement statement =
                        connection.prepareCall(sql)
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


            statement.setString(
                    4,
                    tipo.getValor()
            );


            statement.setBigDecimal(
                    5,
                    cantidad
            );



            if(motivo != null && !motivo.isBlank()) {

                statement.setString(
                        6,
                        motivo.trim()
                );

            } else {

                statement.setNull(
                        6,
                        Types.VARCHAR
                );
            }




            if(referenciaTipo != null
                    && !referenciaTipo.isBlank()) {


                statement.setString(
                        7,
                        referenciaTipo.trim()
                );


            } else {


                statement.setNull(
                        7,
                        Types.VARCHAR
                );

            }




            if(referenciaId != null) {


                statement.setLong(
                        8,
                        referenciaId
                );


            } else {


                statement.setNull(
                        8,
                        Types.BIGINT
                );

            }




            if(authUserId != null) {


                statement.setInt(
                        9,
                        authUserId
                );


            } else {


                statement.setNull(
                        9,
                        Types.INTEGER
                );

            }




            try(
                    ResultSet rs =
                            statement.executeQuery()
            ){


                if(rs.next()) {


                    return rs.getLong(1);

                }

            }


        } catch(SQLException e) {


            throw new RuntimeException(
                    "Error ejecutando registrar_movimiento_inventario()",
                    e
            );

        }



        throw new IllegalStateException(
                "PostgreSQL no devolvió el ID del movimiento"
        );

    }





    private void validarDatos(
            Connection connection,
            Long empresaId,
            Long sucursalId,
            Long productoId,
            TipoMovimientoInventario tipo,
            BigDecimal cantidad) {


        if(connection == null) {

            throw new IllegalArgumentException(
                    "La conexión es obligatoria"
            );
        }



        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        validarId(
                sucursalId,
                "La sucursal es obligatoria"
        );


        validarId(
                productoId,
                "El producto es obligatorio"
        );



        if(tipo == null) {

            throw new IllegalArgumentException(
                    "El tipo de movimiento es obligatorio"
            );

        }



        if(tipo.getValor() == null
                || tipo.getValor().isBlank()) {


            throw new IllegalArgumentException(
                    "El tipo de movimiento no tiene valor configurado"
            );

        }



        if(cantidad == null
                || cantidad.compareTo(
                BigDecimal.ZERO
        ) <= 0) {


            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );

        }

    }





    private void validarId(
            Long id,
            String mensaje) {


        if(id == null || id <= 0) {


            throw new IllegalArgumentException(
                    mensaje
            );

        }

    }

}