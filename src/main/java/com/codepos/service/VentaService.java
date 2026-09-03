package com.codepos.service;

import com.codepos.dao.VentaDAO;
import com.codepos.model.Venta;

import java.math.BigDecimal;
import java.util.List;

public class VentaService {


    private final VentaDAO ventaDAO;


    public VentaService() {

        this.ventaDAO =
                new VentaDAO();

    }



    /**
     * Busca venta por empresa e ID.
     */
    public Venta buscarPorId(
            Long empresaId,
            Long ventaId) {


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        validarId(
                ventaId,
                "La venta es obligatoria"
        );


        Venta venta =
                ventaDAO.buscarPorId(
                        empresaId,
                        ventaId
                );


        if(venta == null){

            throw new IllegalArgumentException(
                    "No existe la venta indicada"
            );

        }


        return venta;

    }




    /**
     * Lista ventas por empresa.
     */
    public List<Venta> listarPorEmpresa(
            Long empresaId){


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        return ventaDAO.listarPorEmpresa(
                empresaId
        );

    }





    /**
     * Crear venta.
     */
    public Long crear(
            Venta venta){


        validarVenta(
                venta
        );


        /*
         * Nueva venta siempre inicia registrada.
         */
        venta.setEstado(
                "REGISTRADA"
        );


        return ventaDAO.crear(
                venta
        );

    }







    private void validarVenta(
            Venta venta){


        if(venta == null){

            throw new IllegalArgumentException(
                    "La venta es obligatoria"
            );

        }



        validarId(
                venta.getEmpresaId(),
                "La empresa es obligatoria"
        );


        validarId(
                venta.getSucursalId(),
                "La sucursal es obligatoria"
        );





        if(venta.getNumero()==null
                || venta.getNumero().isBlank()){


            throw new IllegalArgumentException(
                    "El número de venta es obligatorio"
            );

        }


        venta.setNumero(
                venta.getNumero().trim()
        );






        /*
         * Cliente opcional
         */
        if(venta.getClienteId()!=null){

            validarId(
                    venta.getClienteId(),
                    "Cliente inválido"
            );

        }





        /*
         * Usuario auditoría
         */
        if(venta.getAuthUserId()!=null
                && venta.getAuthUserId()<=0){


            throw new IllegalArgumentException(
                    "Usuario inválido"
            );

        }






        validarMonto(
                venta.getSubtotal(),
                "Subtotal no puede ser negativo"
        );


        validarMonto(
                venta.getDescuento(),
                "Descuento no puede ser negativo"
        );


        validarMonto(
                venta.getImpuesto(),
                "Impuesto no puede ser negativo"
        );


        validarMonto(
                venta.getTotal(),
                "Total no puede ser negativo"
        );





        if(venta.getObservaciones()!=null){

            String observacion =
                    venta.getObservaciones()
                            .trim();


            if(observacion.isEmpty()){

                venta.setObservaciones(null);

            }else{

                venta.setObservaciones(
                        observacion
                );

            }

        }


    }





    private void validarMonto(
            BigDecimal monto,
            String mensaje){


        if(monto!=null &&
                monto.compareTo(
                        BigDecimal.ZERO
                )<0){


            throw new IllegalArgumentException(
                    mensaje
            );

        }

    }





    private void validarId(
            Long id,
            String mensaje){


        if(id==null || id<=0){


            throw new IllegalArgumentException(
                    mensaje
            );

        }

    }

}