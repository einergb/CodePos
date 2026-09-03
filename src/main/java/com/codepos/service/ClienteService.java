package com.codepos.service;

import com.codepos.dao.ClienteDAO;
import com.codepos.model.Cliente;

import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO;


    public ClienteService() {

        this.clienteDAO =
                new ClienteDAO();

    }



    /**
     * Busca un cliente por empresa e ID.
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



        Cliente cliente =
                clienteDAO.buscarPorId(
                        empresaId,
                        clienteId
                );



        if(cliente == null){

            throw new IllegalArgumentException(
                    "No existe el cliente indicado"
            );

        }



        if(!Boolean.TRUE.equals(
                cliente.getActivo()
        )){

            throw new IllegalStateException(
                    "El cliente está inactivo"
            );

        }



        return cliente;

    }




    /**
     * Lista clientes de una empresa.
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
    public Long crear(
            Cliente cliente) {


        validarCliente(
                cliente
        );


        return clienteDAO.crear(
                cliente
        );

    }





    /**
     * Validaciones principales.
     */
    private void validarCliente(
            Cliente cliente) {



        if(cliente == null){

            throw new IllegalArgumentException(
                    "El cliente es obligatorio"
            );

        }



        validarId(
                cliente.getEmpresaId(),
                "La empresa es obligatoria"
        );




        if(cliente.getNombre()==null
                || cliente.getNombre().isBlank()){


            throw new IllegalArgumentException(
                    "El nombre del cliente es obligatorio"
            );

        }



        String nombre =
                cliente.getNombre()
                        .trim();



        if(nombre.length()<2){

            throw new IllegalArgumentException(
                    "El nombre debe tener mínimo 2 caracteres"
            );

        }



        cliente.setNombre(
                nombre
        );




        if(cliente.getIdentificacion()!=null){

            String valor =
                    cliente.getIdentificacion()
                            .trim();


            cliente.setIdentificacion(
                    valor.isBlank()
                            ? null
                            : valor
            );

        }




        if(cliente.getTelefono()!=null){

            String valor =
                    cliente.getTelefono()
                            .trim();


            cliente.setTelefono(
                    valor.isBlank()
                            ? null
                            : valor
            );

        }




        if(cliente.getCorreo()!=null){

            String correo =
                    cliente.getCorreo()
                            .trim();


            if(correo.isBlank()){

                cliente.setCorreo(null);

            }
            else if(!correo.contains("@")){


                throw new IllegalArgumentException(
                        "El correo no tiene un formato válido"
                );

            }
            else{

                cliente.setCorreo(
                        correo
                );

            }

        }




        if(cliente.getDireccion()!=null){

            String valor =
                    cliente.getDireccion()
                            .trim();


            cliente.setDireccion(
                    valor.isBlank()
                            ? null
                            : valor
            );

        }




        /*
         * Cliente nuevo activo por defecto.
         */
        if(cliente.getActivo()==null){

            cliente.setActivo(true);

        }

    }





    /**
     * Valida IDs.
     */
    private void validarId(
            Long id,
            String mensaje) {


        if(id==null || id<=0){

            throw new IllegalArgumentException(
                    mensaje
            );

        }

    }

}