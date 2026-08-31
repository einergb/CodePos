package com.codepos.dto;

import java.math.BigDecimal;

/**

 * DTO que representa el resultado del cálculo
 * de un detalle de venta.
 *
 * No contiene lógica de negocio.
 * Su responsabilidad es transportar los valores
 * calculados por CalculadoraVentaUtil.
 */
public final class ResultadoDetalle {

    private final BigDecimal subtotal;
    private final BigDecimal descuento;
    private final BigDecimal impuesto;
    private final BigDecimal total;

    public ResultadoDetalle(
            BigDecimal subtotal,
            BigDecimal descuento,
            BigDecimal impuesto,
            BigDecimal total) {


        this.subtotal = subtotal;
        this.descuento = descuento;
        this.impuesto = impuesto;
        this.total = total;


    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
