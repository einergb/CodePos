package com.codepos.service;

import com.codepos.dao.ProveedorDAO;
import com.codepos.model.Proveedor;

import java.util.List;

public class ProveedorService {

    private final ProveedorDAO proveedorDAO;


    public ProveedorService() {

        this.proveedorDAO =
                new ProveedorDAO();
    }


    /**
     * Busca un proveedor por empresa.
     */
    public Proveedor consultar(
            Long empresaId,
            Long proveedorId) {


        validarEmpresaId(
                empresaId
        );

        validarProveedorId(
                proveedorId
        );


        Proveedor proveedor =
                proveedorDAO.buscarPorId(
                        empresaId,
                        proveedorId
                );


        if (proveedor == null) {

            throw new IllegalArgumentException(
                    "El proveedor no existe"
            );
        }


        return proveedor;
    }



    /**
     * Lista proveedores activos
     * pertenecientes a una empresa.
     */
    public List<Proveedor> listar(
            Long empresaId) {


        validarEmpresaId(
                empresaId
        );


        return proveedorDAO.listarPorEmpresa(
                empresaId
        );
    }




    /**
     * Crea un proveedor.
     */
    public Long crear(
            Proveedor proveedor) {


        validarProveedor(
                proveedor
        );


        return proveedorDAO.crear(
                proveedor
        );
    }





    /**
     * Validaciones del proveedor.
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



        if (proveedor.getNombre() == null
                || proveedor.getNombre().isBlank()) {


            throw new IllegalArgumentException(
                    "El nombre del proveedor es obligatorio"
            );
        }



        String nombre =
                proveedor.getNombre()
                        .trim();



        if (nombre.length() < 2) {

            throw new IllegalArgumentException(
                    "El nombre del proveedor debe tener mínimo 2 caracteres"
            );
        }



        proveedor.setNombre(
                nombre
        );



        limpiarTextoOpcional(
                proveedor,
                "identificacion"
        );

        limpiarTextoOpcional(
                proveedor,
                "telefono"
        );

        limpiarTextoOpcional(
                proveedor,
                "correo"
        );

        limpiarTextoOpcional(
                proveedor,
                "direccion"
        );


        validarCorreo(
                proveedor.getCorreo()
        );
    }





    /**
     * Limpia campos opcionales.
     */
    private void limpiarTextoOpcional(
            Proveedor proveedor,
            String campo) {


        switch (campo) {


            case "identificacion":

                if (proveedor.getIdentificacion() != null) {

                    proveedor.setIdentificacion(
                            proveedor.getIdentificacion()
                                    .trim()
                    );
                }

                break;



            case "telefono":

                if (proveedor.getTelefono() != null) {

                    proveedor.setTelefono(
                            proveedor.getTelefono()
                                    .trim()
                    );
                }

                break;



            case "correo":

                if (proveedor.getCorreo() != null) {

                    proveedor.setCorreo(
                            proveedor.getCorreo()
                                    .trim()
                    );
                }

                break;



            case "direccion":

                if (proveedor.getDireccion() != null) {

                    proveedor.setDireccion(
                            proveedor.getDireccion()
                                    .trim()
                    );
                }

                break;
        }
    }





    /**
     * Validación básica correo.
     */
    private void validarCorreo(
            String correo) {


        if (correo == null
                || correo.isBlank()) {

            return;
        }



        if (!correo.matches(
                "^[A-Za-z0-9+_.-]+@(.+)$"
        )) {


            throw new IllegalArgumentException(
                    "El correo del proveedor no tiene formato válido"
            );
        }
    }





    private void validarEmpresaId(
            Long empresaId) {


        if (empresaId == null
                || empresaId <= 0) {


            throw new IllegalArgumentException(
                    "Empresa inválida"
            );
        }
    }




    private void validarProveedorId(
            Long proveedorId) {


        if (proveedorId == null
                || proveedorId <= 0) {


            throw new IllegalArgumentException(
                    "Proveedor inválido"
            );
        }
    }

}