package com.codepos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String HOST =
            obtener("DB_HOST");

    private static final String PORT =
            obtener("DB_PORT");

    private static final String DATABASE =
            obtener("DB_NAME");

    private static final String USER =
            obtener("DB_USER");

    private static final String PASSWORD =
            obtener("DB_PASSWORD");


    private static final String URL =
            "jdbc:postgresql://"
                    + HOST
                    + ":"
                    + PORT
                    + "/"
                    + DATABASE;


    private ConexionBD() {
    }


    public static Connection conectar()
            throws SQLException {


        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }



    private static String obtener(String nombre){

        String valor =
                System.getenv(nombre);


        if(valor == null || valor.isBlank()){

            throw new IllegalStateException(
                    "Variable de entorno faltante: "
                            + nombre
            );
        }


        return valor;
    }
}