package com.codepos.service;

import com.codepos.dao.ProveedorDAO;
import com.codepos.model.Proveedor;

import java.util.List;

public class ProveedorService {

    private final ProveedorDAO proveedorDAO;

    public ProveedorService() {
        this.proveedorDAO = new ProveedorDAO();
    }

    /**
     * Consulta un proveedor perteneciente
     * a una empresa.
     */
    public Proveedor consultar(
            Long empresaId,
            Long proveedorId) {

        validarEmpresaId(empresaId);
        validarProveedorId(proveedorId);

        Proveedor proveedor =
                proveedorDAO.buscarPorId(
                        empresaId,
                        proveedorId
                );

        if (proveedor == null) {
            throw new IllegalArgumentException(
                    "No existe el proveedor indicado"
            );
        }

        return proveedor;
    }

    /**
     * Lista los proveedores de una empresa.
     */
    public List<Proveedor> listar(
            Long empresaId) {

        validarEmpresaId(empresaId);

        return proveedorDAO.listarPorEmpresa(
                empresaId
        );
    }

    /**
     * Crea un nuevo proveedor.
     */
    public Long crear(
            Proveedor proveedor) {

        validarProveedor(proveedor);

        return proveedorDAO.crear(
                proveedor
        );
    }

    /**
     * Valida los datos principales del proveedor.
     */
    private void validarProveedor(
            Proveedor proveedor) {

        if (proveedor == null) {
            throw new IllegalArgumentException(
                    "El proveedor es obligatorio"
            );
        }

        validarEmpresaId(
                proveedor.getEmpresaId()
        );

        if (proveedor.getNombre() == null ||
                proveedor.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del proveedor es obligatorio"
            );
        }

        String nombre =
                proveedor.getNombre().trim();

        if (nombre.length() < 2) {
            throw new IllegalArgumentException(
                    "El nombre del proveedor debe tener al menos 2 caracteres"
            );
        }

        proveedor.setNombre(nombre);

        if (proveedor.getIdentificacion() != null) {

            String identificacion =
                    proveedor.getIdentificacion().trim();

            if (identificacion.isBlank()) {
                proveedor.setIdentificacion(null);
            } else {
                proveedor.setIdentificacion(
                        identificacion
                );
            }
        }

        if (proveedor.getTelefono() != null) {

            String telefono =
                    proveedor.getTelefono().trim();

            if (telefono.isBlank()) {
                proveedor.setTelefono(null);
            } else {
                proveedor.setTelefono(telefono);
            }
        }

        if (proveedor.getCorreo() != null) {

            String correo =
                    proveedor.getCorreo().trim();

            if (correo.isBlank()) {
                proveedor.setCorreo(null);
            } else {
                proveedor.setCorreo(correo);
            }
        }

        if (proveedor.getDireccion() != null) {

            String direccion =
                    proveedor.getDireccion().trim();

            if (direccion.isBlank()) {
                proveedor.setDireccion(null);
            } else {
                proveedor.setDireccion(direccion);
            }
        }
    }

    /**
     * Valida el ID de la empresa.
     */
    private void validarEmpresaId(
            Long empresaId) {

        if (empresaId == null ||
                empresaId <= 0) {

            throw new IllegalArgumentException(
                    "Empresa inválida"
            );
        }
    }

    /**
     * Valida el ID del proveedor.
     */
    private void validarProveedorId(
            Long proveedorId) {

        if (proveedorId == null ||
                proveedorId <= 0) {

            throw new IllegalArgumentException(
                    "Proveedor inválido"
            );
        }
    }
}
