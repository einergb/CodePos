package com.codepos.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**

 * Modelo que representa el detalle de una venta.
 *
 * Cada registro corresponde a un producto
 * incluido dentro de una venta.
 */
public class VentaDetalle {

    private Long id;

    private Long ventaId;

    private Long productoId;

    private BigDecimal cantidad;

    private BigDecimal precioVenta;

    private BigDecimal descuento;

    private BigDecimal impuesto;

    private BigDecimal subtotal;

    private OffsetDateTime createdAt;

    public VentaDetalle() {
    }

    public VentaDetalle(
            Long id,
            Long ventaId,
            Long productoId,
            BigDecimal cantidad,
            BigDecimal precioVenta,
            BigDecimal descuento,
            BigDecimal impuesto,
            BigDecimal subtotal,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.descuento = descuento;
        this.impuesto = impuesto;
        this.subtotal = subtotal;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
