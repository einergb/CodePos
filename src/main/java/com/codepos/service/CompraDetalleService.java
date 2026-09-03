package com.codepos.service;

import com.codepos.dao.CompraDAO;
import com.codepos.dao.CompraDetalleDAO;
import com.codepos.model.Compra;
import com.codepos.model.CompraDetalle;

import java.math.BigDecimal;
import java.util.List;

public class CompraDetalleService {

    private final CompraDetalleDAO detalleDAO;
    private final CompraDAO compraDAO;


    public CompraDetalleService() {

        this.detalleDAO =
                new CompraDetalleDAO();

        this.compraDAO =
                new CompraDAO();
    }



    /**
     * Busca detalle por ID.
     */
    public CompraDetalle consultar(
            Long detalleId) {


        validarId(
                detalleId,
                "Detalle"
        );


        CompraDetalle detalle =
                detalleDAO.buscarPorId(
                        detalleId
                );


        if(detalle == null){

            throw new IllegalArgumentException(
                    "No existe el detalle indicado"
            );
        }


        return detalle;
    }




    /**
     * Lista detalles de una compra.
     */
    public List<CompraDetalle> listarPorCompra(
            Long compraId) {


        validarId(
                compraId,
                "Compra"
        );


        return detalleDAO.listarPorCompra(
                compraId
        );

    }





    /**
     * Crea detalle de compra.
     */
    public Long crear(
            Long empresaId,
            CompraDetalle detalle) {


        validarEmpresa(
                empresaId
        );


        validarDetalle(
                detalle
        );


        validarCompra(
                empresaId,
                detalle.getCompraId()
        );


        return detalleDAO.crear(
                detalle
        );
    }





    /**
     * Valida detalle.
     */
    private void validarDetalle(
            CompraDetalle detalle){


        if(detalle == null){

            throw new IllegalArgumentException(
                    "El detalle de compra es obligatorio"
            );
        }



        validarId(
                detalle.getCompraId(),
                "Compra"
        );



        validarId(
                detalle.getProductoId(),
                "Producto"
        );



        validarCantidad(
                detalle.getCantidad()
        );



        validarMonto(
                detalle.getPrecioCompra(),
                "Precio de compra"
        );



        validarMontoOpcional(
                detalle.getDescuento(),
                "El descuento no puede ser negativo"
        );



        validarMontoOpcional(
                detalle.getImpuesto(),
                "El impuesto no puede ser negativo"
        );



        validarMonto(
                detalle.getSubtotal(),
                "Subtotal"
        );

    }






    /**
     * Valida que la compra exista
     * dentro de la empresa.
     */
    private void validarCompra(
            Long empresaId,
            Long compraId){


        Compra compra =
                compraDAO.buscarPorId(
                        empresaId,
                        compraId
                );



        if(compra == null){

            throw new IllegalArgumentException(
                    "La compra no existe para la empresa indicada"
            );
        }

    }





    private void validarCantidad(
            BigDecimal cantidad){


        if(cantidad == null
                || cantidad.compareTo(
                BigDecimal.ZERO)<=0){


            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );

        }

    }





    private void validarMonto(
            BigDecimal monto,
            String campo){


        if(monto == null){

            throw new IllegalArgumentException(
                    campo+" es obligatorio"
            );

        }


        if(monto.compareTo(
                BigDecimal.ZERO)<0){


            throw new IllegalArgumentException(
                    campo+" no puede ser negativo"
            );

        }

    }





    private void validarMontoOpcional(
            BigDecimal monto,
            String mensaje){


        if(monto != null
                && monto.compareTo(
                BigDecimal.ZERO)<0){


            throw new IllegalArgumentException(
                    mensaje
            );
        }

    }





    private void validarEmpresa(
            Long empresaId){


        if(empresaId == null
                || empresaId<=0){


            throw new IllegalArgumentException(
                    "Empresa inválida"
            );

        }

    }





    private void validarId(
            Long id,
            String entidad){


        if(id == null
                || id<=0){


            throw new IllegalArgumentException(
                    entidad+" inválido"
            );

        }

    }

}