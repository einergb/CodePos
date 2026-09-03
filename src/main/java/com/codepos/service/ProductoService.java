package com.codepos.service;

import com.codepos.dao.ProductoDAO;
import com.codepos.model.Producto;

import java.math.BigDecimal;
import java.util.List;

public class ProductoService {

    private final ProductoDAO productoDAO;


    public ProductoService() {

        this.productoDAO = new ProductoDAO();

    }


    /**
     * Busca producto por empresa e ID.
     */
    public Producto buscarPorId(
            Long empresaId,
            Long productoId) {


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        validarId(
                productoId,
                "El producto es obligatorio"
        );


        return productoDAO.buscarPorId(
                empresaId,
                productoId
        );

    }



    /**
     * Lista productos por empresa.
     */
    public List<Producto> listarPorEmpresa(
            Long empresaId) {


        validarId(
                empresaId,
                "La empresa es obligatoria"
        );


        return productoDAO.listarPorEmpresa(
                empresaId
        );

    }




    /**
     * Crear producto.
     */
    public Long crear(
            Producto producto) {


        validarProducto(producto);


        return productoDAO.crear(
                producto
        );

    }




    /**
     * Validación principal.
     */
    private void validarProducto(
            Producto producto) {


        if(producto == null){

            throw new IllegalArgumentException(
                    "El producto es obligatorio"
            );

        }



        validarId(
                producto.getEmpresaId(),
                "La empresa es obligatoria"
        );



        validarId(
                producto.getUnidadMedidaId(),
                "La unidad de medida es obligatoria"
        );



        /*
         * SKU interno.
         *
         * IMPORTANTE:
         *
         * No dependemos del código de barras.
         * Muchas microempresas no utilizan lector.
         */
        if(producto.getSku()==null
                || producto.getSku().isBlank()){


            throw new IllegalArgumentException(
                    "El SKU es obligatorio"
            );

        }



        if(producto.getNombre()==null
                || producto.getNombre().isBlank()){


            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio"
            );

        }



        /*
         * Normalización.
         */
        producto.setSku(
                producto.getSku().trim()
        );


        producto.setNombre(
                producto.getNombre().trim()
        );



        /*
         * Código de barras opcional.
         */
        if(producto.getCodigoBarras()!=null){

            producto.setCodigoBarras(
                    producto.getCodigoBarras().trim()
            );

        }



        /*
         * Descripción opcional.
         */
        if(producto.getDescripcion()!=null){

            producto.setDescripcion(
                    producto.getDescripcion().trim()
            );

        }




        validarMonto(
                producto.getPrecioCompra(),
                "El precio de compra es obligatorio"
        );


        validarMonto(
                producto.getPrecioVenta(),
                "El precio de venta es obligatorio"
        );



        validarIVA(producto);



        /*
         * Si no especifica estado,
         * queda activo.
         */
        if(producto.getActivo()==null){

            producto.setActivo(true);

        }


    }




    /**
     * Validación IVA.
     */
    private void validarIVA(
            Producto producto){


        Boolean aplica =
                producto.getAplicaIva();


        BigDecimal porcentaje =
                producto.getIvaPorcentaje();



        if(aplica==null){

            producto.setAplicaIva(false);

            aplica=false;

        }



        if(porcentaje==null){

            producto.setIvaPorcentaje(
                    BigDecimal.ZERO
            );

            porcentaje =
                    BigDecimal.ZERO;

        }



        if(porcentaje.compareTo(
                BigDecimal.ZERO)<0){


            throw new IllegalArgumentException(
                    "El IVA no puede ser negativo"
            );

        }



        if(porcentaje.compareTo(
                new BigDecimal("100"))>0){


            throw new IllegalArgumentException(
                    "El IVA no puede superar 100%"
            );

        }



        /*
         * Producto sin IVA.
         */
        if(!aplica
                && porcentaje.compareTo(
                BigDecimal.ZERO)!=0){


            throw new IllegalArgumentException(
                    "Producto sin IVA debe tener porcentaje 0"
            );

        }



        /*
         * Producto con IVA.
         */
        if(aplica
                && porcentaje.compareTo(
                BigDecimal.ZERO)<=0){


            throw new IllegalArgumentException(
                    "Producto con IVA requiere porcentaje"
            );

        }


    }




    /**
     * Valida valores monetarios.
     */
    private void validarMonto(
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
     * Valida IDs.
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