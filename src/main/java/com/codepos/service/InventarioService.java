package com.codepos.service;

import com.codepos.dao.InventarioDAO;
import com.codepos.dao.MovimientoInventarioDAO;
import com.codepos.enums.TipoMovimientoInventario;
import com.codepos.model.Inventario;

import java.math.BigDecimal;
import java.sql.Connection;

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
     * Registra un movimiento de inventario usando una
     * conexión propia (abre y cierra su propia transacción).
     *
     * Usar esta variante solo cuando el movimiento NO forma
     * parte de una operación más amplia (por ejemplo, un
     * ajuste manual de inventario aislado). Para operaciones
     * como una venta completa, usar la sobrecarga con
     * Connection, de forma que el movimiento participe en la
     * misma transacción que la venta, el detalle y el pago.
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

        validarIds(
                empresaId,
                sucursalId,
                productoId
        );

        validarTipoCantidadMotivo(
                tipo,
                cantidad,
                motivo
        );

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
     * Registra un movimiento de inventario dentro de una
     * transacción existente.
     *
     * IMPORTANTE:
     *
     * - No abre conexión.
     * - No cierra conexión.
     * - No hace commit.
     * - No hace rollback.
     *
     * La transacción pertenece a quien orquesta la operación
     * completa (por ejemplo VentaIntegralService), que debe
     * hacer commit/rollback sobre la misma Connection para
     * todos los pasos (venta, detalles, inventario, kardex,
     * pago) de forma atómica.
     *
     * Esta sobrecarga delega en la variante transaccional de
     * MovimientoInventarioDAO.registrarMovimiento(Connection, ...),
     * que ya existía en el DAO pero no estaba expuesta a nivel
     * de Service.
     */
    public Long registrarMovimiento(
            Connection connection,
            Long empresaId,
            Long sucursalId,
            Long productoId,
            TipoMovimientoInventario tipo,
            BigDecimal cantidad,
            String motivo,
            String referenciaTipo,
            Long referenciaId,
            Integer authUserId) {

        if (connection == null) {

            throw new IllegalArgumentException(
                    "La conexión es obligatoria"
            );
        }

        validarIds(
                empresaId,
                sucursalId,
                productoId
        );

        validarTipoCantidadMotivo(
                tipo,
                cantidad,
                motivo
        );

        return movimientoDAO.registrarMovimiento(
                connection,
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
     * Valida tipo, cantidad y motivo. Reutilizada por ambas
     * sobrecargas de registrarMovimiento().
     */
    private void validarTipoCantidadMotivo(
            TipoMovimientoInventario tipo,
            BigDecimal cantidad,
            String motivo) {

        if (tipo == null) {

            throw new IllegalArgumentException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        if (cantidad == null
                || cantidad.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        if (motivo == null
                || motivo.isBlank()) {

            throw new IllegalArgumentException(
                    "El motivo es obligatorio"
            );
        }
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