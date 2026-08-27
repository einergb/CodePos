package com.codepos;

import com.codepos.model.Proveedor;
import com.codepos.service.ProveedorService;

import java.util.List;

public class TestProveedorService {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("    TEST PROVEEDOR SERVICE");
        System.out.println("=================================");

        ProveedorService proveedorService =
                new ProveedorService();

        Long empresaId = 1L;

        // =================================
        // 1. CONSULTAR PROVEEDOR
        // =================================

        System.out.println();
        System.out.println("1. Consultando proveedor...");

        Proveedor proveedor =
                proveedorService.consultar(
                        empresaId,
                        1L
                );

        System.out.println("✅ Proveedor encontrado");
        System.out.println(
                "ID: " + proveedor.getId()
        );
        System.out.println(
                "Nombre: " + proveedor.getNombre()
        );
        System.out.println(
                "Identificación: " +
                        proveedor.getIdentificacion()
        );
        System.out.println(
                "Activo: " +
                        proveedor.getActivo()
        );

        // =================================
        // 2. LISTAR PROVEEDORES
        // =================================

        System.out.println();
        System.out.println(
                "2. Listando proveedores..."
        );

        List<Proveedor> proveedores =
                proveedorService.listar(
                        empresaId
                );

        System.out.println(
                "Total: " +
                        proveedores.size()
        );

        for (Proveedor p : proveedores) {

            System.out.println(
                    p.getId()
                            + " | "
                            + p.getNombre()
                            + " | "
                            + p.getIdentificacion()
                            + " | Activo: "
                            + p.getActivo()
            );
        }

        // =================================
        // 3. CREAR PROVEEDOR
        // =================================

        System.out.println();
        System.out.println(
                "3. Creando proveedor..."
        );

        Proveedor nuevo =
                new Proveedor();

        nuevo.setEmpresaId(
                empresaId
        );

        nuevo.setNombre(
                "Proveedor Service Test 002"
        );

        nuevo.setIdentificacion(
                "SERVICE-TEST-002"
        );

        nuevo.setTelefono(
                "3011111111"
        );

        nuevo.setCorreo(
                "service@test.codepos"
        );

        nuevo.setDireccion(
                "Cali, Valle del Cauca"
        );

        Long nuevoId =
                proveedorService.crear(
                        nuevo
                );

        System.out.println(
                "✅ Proveedor creado"
        );

        System.out.println(
                "ID generado: " +
                        nuevoId
        );

        // =================================
        // 4. PRUEBA DE VALIDACIÓN
        // =================================

        System.out.println();
        System.out.println(
                "4. Probando validación..."
        );

        try {

            Proveedor invalido =
                    new Proveedor();

            invalido.setEmpresaId(
                    empresaId
            );

            invalido.setNombre(
                    ""
            );

            proveedorService.crear(
                    invalido
            );

            System.out.println(
                    "❌ ERROR: La validación no funcionó"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: " +
                            e.getMessage()
            );
        }

        System.out.println();
        System.out.println("=================================");
        System.out.println(
                "       PRUEBA FINALIZADA"
        );
        System.out.println("=================================");
    }
}