package com.codepos.service;

import com.codepos.dao.VentaDAO;
import com.codepos.model.Venta;

import java.math.BigDecimal;
import java.util.List;

public class VentaService {

    private final VentaDAO ventaDAO;

    public VentaService() {
        this.ventaDAO = new VentaDAO();
    }

    /**
     * Busca una venta por empresa e ID.
     */
    public Venta buscarPorId(
            Long empresaId,
            Long ventaId) {

        validarId(
                empresaId,
                "El ID de la empresa es obligatorio"
        );

        validarId(
                ventaId,
                "El ID de la venta es obligatorio"
        );

        return ventaDAO.buscarPorId(
                empresaId,
                ventaId
        );
    }

    /**
     * Lista las ventas de una empresa.
     */
    public List<Venta> listarPorEmpresa(
            Long empresaId) {

        validarId(
                empresaId,
                "El ID de la empresa es obligatorio"
        );

        return ventaDAO.listarPorEmpresa(
                empresaId
        );
    }

    /**
     * Crea una nueva venta.
     */
    public Long crear(Venta venta) {

        validarVenta(venta);

        /*
         * El estado inicial de una venta nueva
         * siempre será REGISTRADA.
         *
         * La venta podrá pasar posteriormente
         * a PAGADA o ANULADA mediante la lógica
         * correspondiente.
         */
        venta.setEstado("REGISTRADA");

        return ventaDAO.crear(venta);
    }

    /**
     * Valida los datos principales de una venta.
     */
    private void validarVenta(Venta venta) {

        if (venta == null) {

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

        if (venta.getNumero() == null
                || venta.getNumero().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El número de venta es obligatorio"
            );
        }

        /*
         * Cliente opcional.
         *
         * Una venta puede ser:
         *
         * 1. Venta con cliente identificado.
         * 2. Venta de mostrador.
         */
        if (venta.getClienteId() != null) {

            validarId(
                    venta.getClienteId(),
                    "El ID del cliente no es válido"
            );
        }

        /*
         * Usuario autenticado opcional en esta etapa.
         *
         * Cuando integremos Auth, el flujo real
         * proporcionará este ID.
         */
        if (venta.getAuthUserId() != null
                && venta.getAuthUserId() <= 0) {

            throw new IllegalArgumentException(
                    "El ID del usuario autenticado no es válido"
            );
        }

        validarMontoNoNegativo(
                venta.getSubtotal(),
                "El subtotal no puede ser negativo"
        );

        validarMontoNoNegativo(
                venta.getDescuento(),
                "El descuento no puede ser negativo"
        );

        validarMontoNoNegativo(
                venta.getImpuesto(),
                "El impuesto no puede ser negativo"
        );

        validarMontoNoNegativo(
                venta.getTotal(),
                "El total no puede ser negativo"
        );

        if (venta.getObservaciones() != null
                && venta.getObservaciones().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Las observaciones no pueden estar vacías"
            );
        }
    }

    /**
     * Valida que un monto exista y no sea negativo.
     */
    private void validarMontoNoNegativo(
            BigDecimal monto,
            String mensaje) {

        if (monto == null) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }

        if (monto.compareTo(BigDecimal.ZERO) < 0) {

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
