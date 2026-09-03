package com.codepos.service;

import com.codepos.dao.PagoDAO;
import com.codepos.dao.VentaDAO;
import com.codepos.model.Pago;
import com.codepos.model.Venta;

import java.math.BigDecimal;
import java.util.List;

public class PagoService {


    private final PagoDAO pagoDAO;
    private final VentaDAO ventaDAO;


    public PagoService() {

        this.pagoDAO =
                new PagoDAO();

        this.ventaDAO =
                new VentaDAO();

    }



    /**
     * Busca un pago por ID.
     */
    public Pago buscarPorId(
            Long pagoId) {


        validarId(
                pagoId,
                "El ID del pago es obligatorio"
        );


        return pagoDAO.buscarPorId(
                pagoId
        );

    }




    /**
     * Lista pagos de una venta.
     */
    public List<Pago> listarPorVenta(
            Long ventaId) {


        validarId(
                ventaId,
                "El ID de la venta es obligatorio"
        );


        return pagoDAO.listarPorVenta(
                ventaId
        );

    }




    /**
     * Crear pago.
     */
    public Long crear(
            Pago pago) {


        validarPago(
                pago
        );


        validarVenta(
                pago.getVentaId()
        );


        return pagoDAO.crear(
                pago
        );

    }




    private void validarPago(
            Pago pago) {


        if(pago == null){

            throw new IllegalArgumentException(
                    "El pago es obligatorio"
            );

        }


        validarId(
                pago.getVentaId(),
                "La venta es obligatoria"
        );



        validarMetodo(
                pago
        );



        validarMonto(
                pago.getMonto()
        );




        if(pago.getAuthUserId()!=null
                && pago.getAuthUserId()<=0){


            throw new IllegalArgumentException(
                    "Usuario autenticado inválido"
            );

        }




        if(pago.getReferencia()!=null){


            String referencia =
                    pago.getReferencia()
                            .trim();


            pago.setReferencia(
                    referencia.isEmpty()
                            ? null
                            : referencia
            );

        }


    }





    private void validarVenta(
            Long ventaId){


        /*
         * En esta versión el DAO
         * requiere empresa.
         *
         * La validación completa
         * quedará integrada en VentaIntegralService.
         */

        if(ventaId<=0){

            throw new IllegalArgumentException(
                    "Venta inválida"
            );

        }

    }





    private void validarMetodo(
            Pago pago){


        if(pago.getMetodo()==null
                || pago.getMetodo().isBlank()){


            throw new IllegalArgumentException(
                    "El método de pago es obligatorio"
            );

        }


        String metodo =
                pago.getMetodo()
                        .trim()
                        .toUpperCase();



        switch(metodo){

            case "EFECTIVO",
                 "TARJETA",
                 "TRANSFERENCIA",
                 "NEQUI",
                 "DAVIPLATA" -> {}

            default ->
                    throw new IllegalArgumentException(
                            "Método de pago no permitido: "
                                    + metodo
                    );

        }


        pago.setMetodo(
                metodo
        );


    }





    private void validarMonto(
            BigDecimal monto){


        if(monto==null){

            throw new IllegalArgumentException(
                    "El monto del pago es obligatorio"
            );

        }


        if(monto.compareTo(
                BigDecimal.ZERO)<=0){


            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
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