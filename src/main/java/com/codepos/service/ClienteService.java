package com.codepos.service;

import com.codepos.dao.ClienteDAO;
import com.codepos.model.Cliente;

import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Busca un cliente por su ID y empresa.
     */
    public Cliente buscarPorId(
            Long empresaId,
            Long clienteId) {

        validarId(
                empresaId,
                "La empresa es obligatoria"
        );

        validarId(
                clienteId,
                "El ID del cliente es obligatorio"
        );

        return clienteDAO.buscarPorId(
                empresaId,
                clienteId
        );
    }

    /**
     * Lista los clientes de una empresa.
     */
    public List<Cliente> listarPorEmpresa(
            Long empresaId) {

        validarId(
                empresaId,
                "La empresa es obligatoria"
        );

        return clienteDAO.listarPorEmpresa(
                empresaId
        );
    }

    /**
     * Crea un nuevo cliente.
     */
    public Long crear(Cliente cliente) {

        validarCliente(cliente);

        return clienteDAO.crear(cliente);
    }

    /**
     * Valida los datos principales del cliente.
     */
    private void validarCliente(Cliente cliente) {

        if (cliente == null) {

            throw new IllegalArgumentException(
                    "El cliente es obligatorio"
            );
        }

        validarId(
                cliente.getEmpresaId(),
                "La empresa es obligatoria"
        );

        if (cliente.getNombre() == null
                || cliente.getNombre().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El nombre del cliente es obligatorio"
            );
        }

        /*
         * Los siguientes campos son opcionales:
         *
         * - identificación
         * - teléfono
         * - correo
         * - dirección
         *
         * Si son enviados, no pueden estar vacíos.
         */

        if (cliente.getIdentificacion() != null
                && cliente.getIdentificacion()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "La identificación no puede estar vacía"
            );
        }

        if (cliente.getTelefono() != null
                && cliente.getTelefono()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "El teléfono no puede estar vacío"
            );
        }

        if (cliente.getCorreo() != null
                && cliente.getCorreo()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "El correo no puede estar vacío"
            );
        }

        if (cliente.getDireccion() != null
                && cliente.getDireccion()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "La dirección no puede estar vacía"
            );
        }
    }

    /**
     * Valida un identificador.
     */
    private void validarId(
            Long id,
            String mensaje) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }

}
