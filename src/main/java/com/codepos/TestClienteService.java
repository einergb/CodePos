package com.codepos;

import com.codepos.model.Cliente;
import com.codepos.service.ClienteService;

import java.util.List;

public class TestClienteService {

    public static void main(String[] args) {

        ClienteService clienteService =
                new ClienteService();

        System.out.println("=================================");
        System.out.println("    TEST CLIENTE SERVICE");
        System.out.println("=================================");

        // =========================================
        // 1. CONSULTAR CLIENTE
        // =========================================

        System.out.println();
        System.out.println("1. Consultando cliente...");

        Cliente cliente =
                clienteService.buscarPorId(1L, 1L);

        if (cliente != null) {

            System.out.println(
                    "✅ Cliente encontrado"
            );

            System.out.println(
                    "ID: " + cliente.getId()
            );

            System.out.println(
                    "Nombre: " + cliente.getNombre()
            );

            System.out.println(
                    "Identificación: "
                            + cliente.getIdentificacion()
            );

            System.out.println(
                    "Activo: " + cliente.getActivo()
            );

        } else {

            System.out.println(
                    "⚠️ Cliente no encontrado"
            );
        }

        // =========================================
        // 2. LISTAR CLIENTES
        // =========================================

        System.out.println();
        System.out.println(
                "2. Listando clientes..."
        );

        List<Cliente> clientes =
                clienteService.listarPorEmpresa(1L);

        System.out.println(
                "Total: " + clientes.size()
        );

        for (Cliente c : clientes) {

            System.out.println(
                    c.getId()
                            + " | "
                            + c.getNombre()
                            + " | "
                            + c.getIdentificacion()
                            + " | Activo: "
                            + c.getActivo()
            );
        }

        // =========================================
        // 3. CREAR CLIENTE COMPLETO
        // =========================================

        System.out.println();
        System.out.println(
                "3. Creando cliente completo..."
        );

        Cliente nuevoCliente =
                new Cliente();

        nuevoCliente.setEmpresaId(1L);

        nuevoCliente.setNombre(
                "Cliente Service Test "
                        + System.currentTimeMillis()
        );

        nuevoCliente.setIdentificacion(
                "SERVICE-CLIENTE-"
                        + System.currentTimeMillis()
        );

        nuevoCliente.setTelefono(
                "3011234567"
        );

        nuevoCliente.setCorreo(
                "service.cliente@codepos.com"
        );

        nuevoCliente.setDireccion(
                "Cali, Valle del Cauca"
        );

        Long idGenerado =
                clienteService.crear(
                        nuevoCliente
                );

        System.out.println(
                "✅ Cliente creado"
        );

        System.out.println(
                "ID generado: "
                        + idGenerado
        );

        // =========================================
        // 4. CREAR CLIENTE MOSTRADOR
        // =========================================

        System.out.println();
        System.out.println(
                "4. Creando cliente mostrador..."
        );

        Cliente clienteMostrador =
                new Cliente();

        clienteMostrador.setEmpresaId(1L);

        clienteMostrador.setNombre(
                "Cliente Mostrador Test "
                        + System.currentTimeMillis()
        );

        /*
         * Los demás datos quedan NULL
         * porque son opcionales.
         */

        Long idMostrador =
                clienteService.crear(
                        clienteMostrador
                );

        System.out.println(
                "✅ Cliente mostrador creado"
        );

        System.out.println(
                "ID generado: "
                        + idMostrador
        );

        // =========================================
        // 5. VALIDACIÓN
        // =========================================

        System.out.println();
        System.out.println(
                "5. Probando validación..."
        );

        try {

            Cliente clienteInvalido =
                    new Cliente();

            clienteInvalido.setEmpresaId(1L);

            clienteInvalido.setNombre("");

            clienteService.crear(
                    clienteInvalido
            );

            System.out.println(
                    "❌ ERROR: la validación no funcionó"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }

        System.out.println();
        System.out.println("=================================");
        System.out.println("       PRUEBA FINALIZADA");
        System.out.println("=================================");
    }

}
