package com.codepos.service;

import com.codepos.dao.VentaDAO;
import com.codepos.dao.VentaDetalleDAO;
import com.codepos.model.Venta;
import com.codepos.model.VentaDetalle;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio encargado de la lógica de negocio
 * de los detalles de venta.
 *
 * Responsabilidades:
 *
 * - Validar información recibida.
 * - Aplicar reglas de negocio.
 * - Preparar datos antes del DAO.
 *
 */
public class VentaDetalleService {


    private final VentaDetalleDAO ventaDetalleDAO;

    private final VentaDAO ventaDAO;


    public VentaDetalleService() {

        this.ventaDetalleDAO =
                new VentaDetalleDAO();

        this.ventaDAO =
                new VentaDAO();

    }



    /**
     * Busca detalle por ID, verificando que la venta
     * a la que pertenece sea de la empresa indicada.
     */
    public VentaDetalle buscarPorId(
            Long empresaId,
            Long detalleId) {


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );

        validarId(
                detalleId,
                "El detalle de venta es obligatorio"
        );


        VentaDetalle detalle =
                ventaDetalleDAO.buscarPorId(
                        empresaId,
                        detalleId
                );


        if(detalle == null){

            throw new IllegalArgumentException(
                    "No existe el detalle indicado para la empresa"
            );

        }


        return detalle;

    }





    /**
     * Lista detalles asociados a una venta, verificando
     * que la venta pertenezca a la empresa indicada.
     */
    public List<VentaDetalle> listarPorVenta(
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


        return ventaDetalleDAO.listarPorVenta(
                empresaId,
                ventaId
        );

    }





    /**
     * Crea detalle de venta, verificando primero que la
     * venta exista y pertenezca a la empresa indicada.
     */
    public Long crear(
            Long empresaId,
            VentaDetalle detalle) {


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        validarDetalle(
                detalle
        );


        validarVenta(
                empresaId,
                detalle.getVentaId()
        );


        return ventaDetalleDAO.crear(
                detalle
        );

    }







    /**
     * Verifica que la venta exista y pertenezca a la
     * empresa indicada.
     */
    private void validarVenta(
            Long empresaId,
            Long ventaId) {


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







    /**
     * Validaciones principales.
     */
    private void validarDetalle(
            VentaDetalle detalle) {



        if(detalle == null){

            throw new IllegalArgumentException(
                    "El detalle de venta es obligatorio"
            );

        }




        validarId(
                detalle.getVentaId(),
                "La venta es obligatoria"
        );




        validarId(
                detalle.getProductoId(),
                "El producto es obligatorio"
        );





        /*
         * Cantidad.
         */
        if(detalle.getCantidad()==null){

            throw new IllegalArgumentException(
                    "La cantidad es obligatoria"
            );

        }



        if(detalle.getCantidad()
                .compareTo(BigDecimal.ZERO)<=0){


            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );

        }





        /*
         * Precio venta.
         */
        validarMontoObligatorio(
                detalle.getPrecioVenta(),
                "El precio de venta es obligatorio"
        );





        /*
         * Descuento.
         *
         * Puede ser null.
         * Si existe no puede ser negativo.
         */
        validarMontoOpcional(
                detalle.getDescuento(),
                "El descuento no puede ser negativo"
        );






        /*
         * Impuesto.
         */
        validarMontoOpcional(
                detalle.getImpuesto(),
                "El impuesto no puede ser negativo"
        );







        /*
         * Subtotal.
         */
        validarMontoObligatorio(
                detalle.getSubtotal(),
                "El subtotal es obligatorio"
        );





        /*
         * Normalización.
         */
        normalizarValores(
                detalle
        );


    }









    /**
     * Limpia valores antes de guardar.
     */
    private void normalizarValores(
            VentaDetalle detalle){


        if(detalle.getDescuento()==null){

            detalle.setDescuento(
                    BigDecimal.ZERO
            );

        }


        if(detalle.getImpuesto()==null){

            detalle.setImpuesto(
                    BigDecimal.ZERO
            );

        }


    }









    /**
     * Valida monto obligatorio.
     */
    private void validarMontoObligatorio(
            BigDecimal monto,
            String mensaje){


        if(monto==null){

            throw new IllegalArgumentException(
                    mensaje
            );

        }



        if(monto.compareTo(
                BigDecimal.ZERO)<0){


            throw new IllegalArgumentException(
                    mensaje
            );

        }


    }









    /**
     * Valida monto opcional.
     */
    private void validarMontoOpcional(
            BigDecimal monto,
            String mensaje){


        if(monto!=null &&
                monto.compareTo(
                        BigDecimal.ZERO)<0){


            throw new IllegalArgumentException(
                    mensaje
            );

        }


    }









    /**
     * Valida identificadores.
     */
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