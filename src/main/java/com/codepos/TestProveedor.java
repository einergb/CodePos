package com.codepos;

import com.codepos.dao.ProveedorDAO;
import com.codepos.model.Proveedor;

import java.util.List;

public class TestProveedor {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       TEST PROVEEDOR DAO");
        System.out.println("=================================");

        ProveedorDAO proveedorDAO =
                new ProveedorDAO();

        Long empresaId = 1L;

        // =================================
        // 1. BUSCAR PROVEEDOR
        // =================================

        System.out.println();
        System.out.println("1. Buscando proveedor...");

        Proveedor proveedor =
                proveedorDAO.buscarPorId(
                        empresaId,
                        1L
                );

        if (proveedor != null) {

            System.out.println(
                    "✅ Proveedor encontrado"
            );

            System.out.println(
                    "ID: " + proveedor.getId()
            );

            System.out.println(
                    "Empresa: " +
                            proveedor.getEmpresaId()
            );

            System.out.println(
                    "Nombre: " +
                            proveedor.getNombre()
            );

            System.out.println(
                    "Identificación: " +
                            proveedor.getIdentificacion()
            );

            System.out.println(
                    "Teléfono: " +
                            proveedor.getTelefono()
            );

            System.out.println(
                    "Correo: " +
                            proveedor.getCorreo()
            );

            System.out.println(
                    "Dirección: " +
                            proveedor.getDireccion()
            );

            System.out.println(
                    "Activo: " +
                            proveedor.getActivo()
            );

        } else {

            System.out.println(
                    "⚠️ Proveedor no encontrado"
            );
        }

        // =================================
        // 2. LISTAR PROVEEDORES
        // =================================

        System.out.println();
        System.out.println(
                "2. Listando proveedores..."
        );

        List<Proveedor> proveedores =
                proveedorDAO.listarPorEmpresa(
                        empresaId
                );

        System.out.println(
                "Total encontrados: " +
                        proveedores.size()
        );

        for (Proveedor p : proveedores) {

            System.out.println(
                    p.getId() +
                            " | " +
                            p.getNombre() +
                            " | " +
                            p.getIdentificacion() +
                            " | Activo: " +
                            p.getActivo()
            );
        }

        // =================================
        // 3. CREAR PROVEEDOR
        // =================================

        System.out.println();
        System.out.println(
                "3. Creando proveedor de prueba..."
        );

        Proveedor nuevoProveedor =
                new Proveedor();

        nuevoProveedor.setEmpresaId(
                empresaId
        );

        nuevoProveedor.setNombre(
                "Proveedor Test CodePOS 003"
        );

        nuevoProveedor.setIdentificacion(
                "TEST-CODEPOS-003"
        );

        nuevoProveedor.setTelefono(
                "3000000000"
        );

        nuevoProveedor.setCorreo(
                "test3@codepos.local"
        );

        nuevoProveedor.setDireccion(
                "Dirección de prueba"
        );

        Long nuevoId =
                proveedorDAO.crear(
                        nuevoProveedor
                );

        System.out.println(
                "✅ Proveedor creado"
        );

        System.out.println(
                "ID generado: " + nuevoId
        );

        System.out.println("=================================");
        System.out.println(
                "       PRUEBA FINALIZADA"
        );
        System.out.println("=================================");
    }
}