package com.codepos.config;

import java.sql.Connection;

public class TestConexion {

    public static void main(String[] args) {

        try (Connection cn = ConexionBD.conectar()) {

            System.out.println(
                    "✅ Conexión a PostgreSQL exitosa"
            );

            System.out.println(
                    "Base de datos: "
                    + cn.getCatalog()
            );

        } catch (Exception e) {

            System.err.println(
                    "❌ Error de conexión"
            );

            e.printStackTrace();
        }
    }
}
