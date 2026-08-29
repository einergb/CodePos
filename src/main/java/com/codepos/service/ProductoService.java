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
     * Busca un producto por empresa e ID.
     */
    public Producto buscarPorId(
            Long empresaId,
            Long productoId) {

        validarId(
                empresaId,
                "El ID de la empresa es obligatorio"
        );

        validarId(
                productoId,
                "El ID del producto es obligatorio"
        );

        return productoDAO.buscarPorId(
                empresaId,
                productoId
        );
    }

    /**
     * Lista todos los productos de una empresa.
     */
    public List<Producto> listarPorEmpresa(
            Long empresaId) {

        validarId(
                empresaId,
                "El ID de la empresa es obligatorio"
        );

        return productoDAO.listarPorEmpresa(
                empresaId
        );
    }

    /**
     * Crea un nuevo producto.
     */
    public Long crear(Producto producto) {

        validarProducto(producto);

        return productoDAO.crear(producto);
    }

    /**
     * Valida los datos principales del producto.
     */
    private void validarProducto(
            Producto producto) {

        if (producto == null) {

            throw new IllegalArgumentException(
                    "El producto es obligatorio"
            );
        }

        /*
         * Empresa obligatoria.
         */
        validarId(
                producto.getEmpresaId(),
                "La empresa es obligatoria"
        );

        /*
         * Unidad de medida obligatoria.
         */
        validarId(
                producto.getUnidadMedidaId(),
                "La unidad de medida es obligatoria"
        );

        /*
         * SKU obligatorio.
         */
        if (producto.getSku() == null
                || producto.getSku().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El SKU es obligatorio"
            );
        }

        /*
         * Nombre obligatorio.
         */
        if (producto.getNombre() == null
                || producto.getNombre().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio"
            );
        }

        /*
         * Precio de compra.
         */
        validarMontoNoNegativo(
                producto.getPrecioCompra(),
                "El precio de compra no puede ser negativo"
        );

        /*
         * Precio de venta.
         */
        validarMontoNoNegativo(
                producto.getPrecioVenta(),
                "El precio de venta no puede ser negativo"
        );

        /*
         * Configuración del IVA.
         */
        validarIVA(producto);
    }

    /**
     * Valida la configuración tributaria del producto.
     *
     * Regla:
     *
     * Producto sin IVA:
     * aplicaIva = false
     * ivaPorcentaje = 0
     *
     * Producto con IVA:
     * aplicaIva = true
     * ivaPorcentaje > 0
     */
    private void validarIVA(
            Producto producto) {

        Boolean aplicaIva =
                producto.getAplicaIva();

        BigDecimal ivaPorcentaje =
                producto.getIvaPorcentaje();

        /*
         * Ambos campos son obligatorios
         * porque representan la configuración
         * tributaria del producto.
         */
        if (aplicaIva == null) {

            throw new IllegalArgumentException(
                    "Debe indicar si el producto aplica IVA"
            );
        }

        if (ivaPorcentaje == null) {

            throw new IllegalArgumentException(
                    "El porcentaje de IVA es obligatorio"
            );
        }

        /*
         * El porcentaje no puede ser negativo.
         */
        if (ivaPorcentaje.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El porcentaje de IVA no puede ser negativo"
            );
        }

        /*
         * El porcentaje no puede superar 100%.
         */
        if (ivaPorcentaje.compareTo(
                new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    "El porcentaje de IVA no puede superar el 100%"
            );
        }

        /*
         * Producto SIN IVA.
         *
         * El porcentaje debe ser exactamente 0.
         */
        if (!aplicaIva
                && ivaPorcentaje.compareTo(
                BigDecimal.ZERO) != 0) {

            throw new IllegalArgumentException(
                    "Un producto sin IVA debe tener porcentaje 0"
            );
        }

        /*
         * Producto CON IVA.
         *
         * El porcentaje debe ser mayor que 0.
         */
        if (aplicaIva
                && ivaPorcentaje.compareTo(
                BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Un producto con IVA debe tener un porcentaje mayor que 0"
            );
        }
    }

    /**
     * Valida un monto que no puede ser negativo.
     */
    private void validarMontoNoNegativo(
            BigDecimal monto,
            String mensaje) {

        if (monto == null) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }

        if (monto.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    mensaje
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
