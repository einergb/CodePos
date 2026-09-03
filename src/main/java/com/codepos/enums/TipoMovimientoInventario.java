package com.codepos.enums;

/**
 * Representa los tipos válidos de movimientos
 * dentro del sistema de inventario.
 *
 * Estos valores deben coincidir exactamente con
 * los valores utilizados por PostgreSQL.
 */
public enum TipoMovimientoInventario {

    ENTRADA_INICIAL(
            "ENTRADA_INICIAL",
            true
    ),

    COMPRA(
            "COMPRA",
            true
    ),

    DEVOLUCION_VENTA(
            "DEVOLUCION_VENTA",
            true
    ),

    AJUSTE_ENTRADA(
            "AJUSTE_ENTRADA",
            true
    ),

    TRASLADO_ENTRADA(
            "TRASLADO_ENTRADA",
            true
    ),

    VENTA(
            "VENTA",
            false
    ),

    DEVOLUCION_COMPRA(
            "DEVOLUCION_COMPRA",
            false
    ),

    AJUSTE_SALIDA(
            "AJUSTE_SALIDA",
            false
    ),

    TRASLADO_SALIDA(
            "TRASLADO_SALIDA",
            false
    );

    /**
     * Valor almacenado en PostgreSQL.
     */
    private final String valor;

    /**
     * Indica si el movimiento aumenta el inventario.
     */
    private final boolean entrada;

    TipoMovimientoInventario(
            String valor,
            boolean entrada) {

        this.valor = valor;
        this.entrada = entrada;
    }

    public String getValor() {

        return valor;
    }

    public boolean esEntrada() {

        return entrada;
    }

    public boolean esSalida() {

        return !entrada;
    }
}