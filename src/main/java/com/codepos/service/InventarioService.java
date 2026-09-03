package com.codepos.service;

import com.codepos.dao.InventarioDAO;
import com.codepos.dao.MovimientoInventarioDAO;
import com.codepos.enums.TipoMovimientoInventario;
import com.codepos.model.Inventario;

import java.math.BigDecimal;

public class InventarioService {

    private final InventarioDAO inventarioDAO;
    private final MovimientoInventarioDAO movimientoDAO;

    public InventarioService() {

        this.inventarioDAO =
                new InventarioDAO();

        this.movimientoDAO =
                new MovimientoInventarioDAO();
    }

    /**
     * Consulta el inventario de un producto
     * perteneciente a una empresa y sucursal.
     */
    public Inventario consultar(
            Long empresaId,
            Long sucursalId,
            Long productoId) {

        validarIds(
                empresaId,
                sucursalId,
                productoId
        );

        Inventario inventario =
                inventarioDAO.buscarPorProducto(
                        empresaId,
                        sucursalId,
                        productoId
                );

        if (inventario == null) {

            throw new IllegalArgumentException(
                    "No existe inventario para el producto indicado"
            );
        }

        if (!Boolean.TRUE.equals(
                inventario.getActivo()
        )) {

            throw new IllegalStateException(
                    "El inventario del producto está inactivo"
            );
        }

        return inventario;
    }

    /**
     * Registra un movimiento de inventario.
     *
     * El tipo de movimiento está controlado mediante
     * TipoMovimientoInventario.
     *
     * Esto evita utilizar Strings libres como:
     *
     * "VENTA"
     * "venta"
     * "VENTAS"
     *
     * La operación es delegada al DAO, que utiliza
     * la función PostgreSQL:
     *
     * registrar_movimiento_inventario()
     */
    public Long registrarMovimiento(
            Long empresaId,
            Long sucursalId,
            Long productoId,
            TipoMovimientoInventario tipo,
            BigDecimal cantidad,
            String motivo,
            String referenciaTipo,
            Long referenciaId,
            Integer authUserId) {

        /*
         * =============================================
         * VALIDAR IDENTIFICADORES
         * =============================================
         */

        validarIds(
                empresaId,
                sucursalId,
                productoId
        );

        /*
         * =============================================
         * VALIDAR TIPO DE MOVIMIENTO
         * =============================================
         */

        if (tipo == null) {

            throw new IllegalArgumentException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        /*
         * =============================================
         * VALIDAR CANTIDAD
         * =============================================
         */

        if (cantidad == null
                || cantidad.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        /*
         * =============================================
         * VALIDAR MOTIVO
         * =============================================
         */

        if (motivo == null
                || motivo.isBlank()) {

            throw new IllegalArgumentException(
                    "El motivo es obligatorio"
            );
        }

        /*
         * =============================================
         * DELEGAR AL DAO
         * =============================================
         *
         * El DAO ejecuta la función PostgreSQL:
         *
         * registrar_movimiento_inventario()
         *
         * PostgreSQL se encarga de:
         *
         * - Bloquear el inventario.
         * - Consultar stock actual.
         * - Calcular stock anterior.
         * - Calcular stock posterior.
         * - Validar stock negativo.
         * - Actualizar inventario.
         * - Registrar movimiento.
         */

        return movimientoDAO.registrarMovimiento(
                empresaId,
                sucursalId,
                productoId,
                tipo,
                cantidad,
                motivo,
                referenciaTipo,
                referenciaId,
                authUserId
        );
    }

    /**
     * Valida los identificadores principales.
     */
    private void validarIds(
            Long empresaId,
            Long sucursalId,
            Long productoId) {

        if (empresaId == null
                || empresaId <= 0) {

            throw new IllegalArgumentException(
                    "Empresa inválida"
            );
        }

        if (sucursalId == null
                || sucursalId <= 0) {

            throw new IllegalArgumentException(
                    "Sucursal inválida"
            );
        }

        if (productoId == null
                || productoId <= 0) {

            throw new IllegalArgumentException(
                    "Producto inválido"
            );
        }
    }
}