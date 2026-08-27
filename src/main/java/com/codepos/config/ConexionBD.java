package com.codepos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String HOST =
            System.getenv("DB_HOST");

    private static final String PORT =
            System.getenv("DB_PORT");

    private static final String DATABASE =
            System.getenv("DB_NAME");

    private static final String USER =
            System.getenv("DB_USER");

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

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
}
