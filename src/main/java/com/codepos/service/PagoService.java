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
     * Busca un pago por ID, verificando que la venta
     * a la que pertenece sea de la empresa indicada.
     */
    public Pago buscarPorId(
            Long empresaId,
            Long pagoId) {


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        validarId(
                pagoId,
                "El ID del pago es obligatorio"
        );


        Pago pago =
                pagoDAO.buscarPorId(
                        empresaId,
                        pagoId
                );


        if(pago == null){

            throw new IllegalArgumentException(
                    "No existe el pago indicado para la empresa"
            );

        }


        return pago;

    }




    /**
     * Lista pagos de una venta, verificando que la venta
     * pertenezca a la empresa indicada.
     */
    public List<Pago> listarPorVenta(
            Long empresaId,
            Long ventaId) {


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        validarId(
                ventaId,
                "El ID de la venta es obligatorio"
        );


        return pagoDAO.listarPorVenta(
                empresaId,
                ventaId
        );

    }




    /**
     * Crea un pago, verificando primero que la venta
     * exista y pertenezca a la empresa indicada.
     *
     * Reemplaza el crear(Pago) anterior (sin empresaId),
     * que dejaba el VentaDAO inyectado sin usar y delegaba
     * la validación completa a VentaIntegralService.
     */
    public Long crear(
            Long empresaId,
            Pago pago) {


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        validarPago(
                pago
        );


        validarVenta(
                empresaId,
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





    /**
     * Verifica que la venta exista y pertenezca a la
     * empresa indicada, usando el VentaDAO ya inyectado.
     */
    private void validarVenta(
            Long empresaId,
            Long ventaId){


        Venta venta =
                ventaDAO.buscarPorId(
                        empresaId,
                        ventaId
                );


        if(venta == null){

            throw new IllegalArgumentException(
                    "La venta no existe para la empresa indicada"
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