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
     * Busca un detalle de compra por su ID.
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

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "No existe el detalle indicado"
            );
        }

        return detalle;
    }

    /**
     * Lista todos los detalles pertenecientes
     * a una compra.
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
     * Crea un nuevo detalle de compra.
     */
    public Long crear(
            CompraDetalle detalle) {

        if (detalle == null) {
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

        validarMonto(
                detalle.getDescuento(),
                "Descuento"
        );

        validarMonto(
                detalle.getImpuesto(),
                "Impuesto"
        );

        validarMonto(
                detalle.getSubtotal(),
                "Subtotal"
        );

        validarCompra(
                detalle.getCompraId()
        );

        return detalleDAO.crear(
                detalle
        );
    }

    /**
     * Verifica que la compra exista.
     */
    private void validarCompra(
            Long compraId) {

        /*
         * En este punto CompraDAO necesita conocer
         * la empresa para buscar la compra.
         *
         * La validación completa de empresa se realizará
         * cuando terminemos de implementar el aislamiento
         * multiempresa del módulo de compras.
         *
         * Por ahora verificamos mediante la consulta
         * disponible en el DAO.
         */
        Compra compra =
                compraDAO.buscarPorId(
                        1L,
                        compraId
                );

        if (compra == null) {
            throw new IllegalArgumentException(
                    "La compra no existe"
            );
        }
    }

    /**
     * Valida una cantidad.
     */
    private void validarCantidad(
            BigDecimal cantidad) {

        if (cantidad == null ||
                cantidad.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }
    }

    /**
     * Valida un valor monetario.
     */
    private void validarMonto(
            BigDecimal monto,
            String campo) {

        if (monto == null) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio"
            );
        }

        if (monto.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    campo + " no puede ser negativo"
            );
        }
    }

    /**
     * Valida identificadores.
     */
    private void validarId(
            Long id,
            String entidad) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    entidad + " inválido"
            );
        }
    }
}
