package com.codepos.service;

import com.codepos.dao.PagoDAO;
import com.codepos.model.Pago;

import java.math.BigDecimal;
import java.util.List;

public class PagoService {

    private final PagoDAO pagoDAO;

    public PagoService() {
        this.pagoDAO = new PagoDAO();
    }

    /**
     * Busca un pago por su ID.
     */
    public Pago buscarPorId(Long pagoId) {

        validarId(
                pagoId,
                "El ID del pago es obligatorio"
        );

        return pagoDAO.buscarPorId(pagoId);
    }

    /**
     * Lista los pagos asociados a una venta.
     */
    public List<Pago> listarPorVenta(Long ventaId) {

        validarId(
                ventaId,
                "El ID de la venta es obligatorio"
        );

        return pagoDAO.listarPorVenta(ventaId);
    }

    /**
     * Crea un nuevo pago.
     */
    public Long crear(Pago pago) {

        validarPago(pago);

        return pagoDAO.crear(pago);
    }

    /**
     * Valida los datos principales del pago.
     */
    private void validarPago(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago es obligatorio"
            );
        }

        validarId(
                pago.getVentaId(),
                "La venta es obligatoria"
        );

        if (pago.getMetodo() == null
                || pago.getMetodo().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El método de pago es obligatorio"
            );
        }

        if (pago.getMonto() == null) {

            throw new IllegalArgumentException(
                    "El monto del pago es obligatorio"
            );
        }

        if (pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto del pago debe ser mayor que cero"
            );
        }

        if (pago.getReferencia() != null
                && pago.getReferencia().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La referencia del pago no puede estar vacía"
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
