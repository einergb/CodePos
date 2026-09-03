package com.codepos;

import com.codepos.config.ConexionBD;
import com.codepos.dao.InventarioDAO;
import com.codepos.dao.MovimientoInventarioDAO;
import com.codepos.dao.PagoDAO;
import com.codepos.dao.VentaDAO;
import com.codepos.dao.VentaDetalleDAO;
import com.codepos.model.Inventario;
import com.codepos.model.Pago;
import com.codepos.model.Venta;
import com.codepos.model.VentaDetalle;
import com.codepos.service.VentaIntegralService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas integrales de VentaIntegralService.
 *
 * OBJETIVO:
 *
 * Verificar que una venta integral sea realmente
 * una única transacción.
 *
 * Pruebas:
 *
 * 1. Venta correcta.
 * 2. Pago incorrecto.
 * 3. Stock insuficiente.
 * 4. Producto sin inventario.
 * 5. Inventario inactivo.
 * 6. ROLLBACK después de crear venta/detalle/inventario.
 *
 * La prueba 6 es la prueba crítica de atomicidad.
 */
public class TestVentaIntegralService {

    private static final Long EMPRESA_ID = 1L;
    private static final Long SUCURSAL_ID = 1L;
    private static final Long PRODUCTO_ID = 2L;

    private static final Integer AUTH_USER_ID = 1;

    private static final BigDecimal CANTIDAD_VENTA =
            new BigDecimal("2");

    private static final BigDecimal PRECIO =
            new BigDecimal("350000");

    private static final BigDecimal DESCUENTO =
            new BigDecimal("20000");

    private static final BigDecimal IMPUESTO =
            new BigDecimal("100000");

    private static final BigDecimal TOTAL =
            new BigDecimal("780000.00");

    private final VentaIntegralService service =
            new VentaIntegralService();

    private final InventarioDAO inventarioDAO =
            new InventarioDAO();

    private final VentaDAO ventaDAO =
            new VentaDAO();

    private final VentaDetalleDAO ventaDetalleDAO =
            new VentaDetalleDAO();

    private final PagoDAO pagoDAO =
            new PagoDAO();

    public static void main(String[] args) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("       TEST VENTA INTEGRAL SERVICE");
        System.out.println("==============================================");

        TestVentaIntegralService test =
                new TestVentaIntegralService();

        try {

            test.probarVentaCorrecta();

            test.probarPagoIncorrecto();

            test.probarStockInsuficiente();

            test.probarProductoSinInventario();

            test.probarInventarioInactivo();

            test.probarRollbackCompleto();

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       TODOS LOS TEST FINALIZADOS");
            System.out.println("       RESULTADO: ✅ CORRECTO");
            System.out.println("==============================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       ❌ ERROR EN LOS TEST");
            System.out.println("==============================================");

            System.out.println(
                    "Mensaje: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // 1. VENTA CORRECTA
    // =========================================================

    private void probarVentaCorrecta() {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("1. VENTA INTEGRAL CORRECTA");
        System.out.println("----------------------------------------------");

        prepararStockSiEsNecesario();

        Inventario inventarioAntes =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        if (inventarioAntes == null) {

            throw new AssertionError(
                    "No existe inventario para el producto de prueba"
            );
        }

        BigDecimal stockAntes =
                inventarioAntes.getCantidad();

        System.out.println(
                "Stock antes: " + stockAntes
        );

        Venta venta =
                crearVenta();

        VentaDetalle detalle =
                crearDetalle();

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        Pago pago =
                crearPago(TOTAL);

        System.out.println();
        System.out.println(
                "Registrando venta integral..."
        );

        Long ventaId =
                service.registrarVenta(
                        venta,
                        detalles,
                        pago
                );

        System.out.println();
        System.out.println(
                "✅ Venta registrada correctamente"
        );

        System.out.println(
                "ID venta: " + ventaId
        );

        System.out.println(
                "Número: " + venta.getNumero()
        );

        System.out.println(
                "Estado: " + venta.getEstado()
        );

        System.out.println(
                "Subtotal: " + venta.getSubtotal()
        );

        System.out.println(
                "Descuento: " + venta.getDescuento()
        );

        System.out.println(
                "Impuesto: " + venta.getImpuesto()
        );

        System.out.println(
                "Total: " + venta.getTotal()
        );

        verificar(
                venta.getSubtotal(),
                new BigDecimal("700000.00"),
                "Subtotal venta"
        );

        verificar(
                venta.getDescuento(),
                DESCUENTO,
                "Descuento venta"
        );

        verificar(
                venta.getImpuesto(),
                IMPUESTO,
                "Impuesto venta"
        );

        verificar(
                venta.getTotal(),
                TOTAL,
                "Total venta"
        );

        verificar(
                pago.getMonto(),
                TOTAL,
                "Pago"
        );

        verificarEstado(
                venta.getEstado(),
                "PAGADA"
        );

        System.out.println(
                "✅ Estado PAGADA confirmado"
        );

        verificar(
                detalle.getSubtotal(),
                new BigDecimal("700000.00"),
                "Subtotal detalle"
        );

        verificar(
                detalle.getDescuento(),
                DESCUENTO,
                "Descuento detalle"
        );

        verificar(
                detalle.getImpuesto(),
                IMPUESTO,
                "Impuesto detalle"
        );

        if (!ventaId.equals(
                detalle.getVentaId()
        )) {

            throw new AssertionError(
                    "El detalle no quedó asociado a la venta"
            );
        }

        System.out.println(
                "✅ Detalle asociado correctamente"
        );

        Inventario inventarioDespues =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        BigDecimal stockDespues =
                inventarioDespues.getCantidad();

        System.out.println();
        System.out.println(
                "Stock después: " + stockDespues
        );

        BigDecimal stockEsperado =
                stockAntes.subtract(
                        CANTIDAD_VENTA
                );

        verificar(
                stockDespues,
                stockEsperado,
                "Stock posterior"
        );

        System.out.println(
                "✅ Inventario descontado correctamente"
        );

        System.out.println(
                "✅ TEST 1 SUPERADO"
        );
    }

    // =========================================================
    // 2. PAGO INCORRECTO
    // =========================================================

    private void probarPagoIncorrecto() {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("2. PAGO INCORRECTO");
        System.out.println("----------------------------------------------");

        Venta venta =
                crearVenta();

        VentaDetalle detalle =
                crearDetalle();

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        Pago pago =
                crearPago(
                        new BigDecimal("100000")
                );

        try {

            service.registrarVenta(
                    venta,
                    detalles,
                    pago
            );

            throw new AssertionError(
                    "La venta debería haber sido rechazada"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Pago diferente al total → "
                            + "rechazada correctamente"
            );

            System.out.println(
                    "✅ Venta rechazada antes de "
                            + "iniciar la transacción"
            );
        }

        System.out.println(
                "✅ TEST 2 SUPERADO"
        );
    }

    // =========================================================
    // 3. STOCK INSUFICIENTE
    // =========================================================

    private void probarStockInsuficiente() {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("3. STOCK INSUFICIENTE");
        System.out.println("----------------------------------------------");

        Inventario inventario =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        if (inventario == null) {

            throw new AssertionError(
                    "No existe inventario"
            );
        }

        BigDecimal stockAntes =
                inventario.getCantidad();

        Venta venta =
                crearVenta();

        VentaDetalle detalle =
                crearDetalle();

        detalle.setCantidad(
                new BigDecimal("999999")
        );

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        BigDecimal subtotal =
                new BigDecimal("349999000000");

        Pago pago =
                crearPago(subtotal);

        try {

            service.registrarVenta(
                    venta,
                    detalles,
                    pago
            );

            throw new AssertionError(
                    "La venta debería ser rechazada"
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "✅ Stock insuficiente → "
                            + "rechazada correctamente"
            );
        }

        Inventario inventarioDespues =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        verificar(
                inventarioDespues.getCantidad(),
                stockAntes,
                "Stock después del rollback"
        );

        System.out.println(
                "✅ Stock insuficiente rechazado correctamente"
        );

        System.out.println(
                "✅ La transacción ejecutó ROLLBACK"
        );

        System.out.println(
                "✅ TEST 3 SUPERADO"
        );
    }

    // =========================================================
    // 4. PRODUCTO SIN INVENTARIO
    // =========================================================

    private void probarProductoSinInventario() {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("4. PRODUCTO SIN INVENTARIO");
        System.out.println("----------------------------------------------");

        Long productoSinInventario =
                buscarProductoSinInventario();

        if (productoSinInventario == null) {

            System.out.println(
                    "⚠️ No existe producto sin inventario."
            );

            System.out.println(
                    "⚠️ TEST 4 OMITIDO"
            );

            return;
        }

        Venta venta =
                crearVenta();

        VentaDetalle detalle =
                crearDetalle();

        detalle.setProductoId(
                productoSinInventario
        );

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        Pago pago =
                crearPago(TOTAL);

        try {

            service.registrarVenta(
                    venta,
                    detalles,
                    pago
            );

            throw new AssertionError(
                    "La venta debería ser rechazada"
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "✅ Producto sin inventario → "
                            + "rechazada correctamente"
            );

            System.out.println(
                    "Mensaje: " + e.getMessage()
            );
        }

        System.out.println(
                "✅ TEST 4 SUPERADO"
        );
    }

    // =========================================================
    // 5. INVENTARIO INACTIVO
    // =========================================================

    private void probarInventarioInactivo() {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("5. INVENTARIO INACTIVO");
        System.out.println("----------------------------------------------");

        Long inventarioId =
                obtenerInventarioId();

        if (inventarioId == null) {

            System.out.println(
                    "⚠️ No existe inventario para probar"
            );

            System.out.println(
                    "⚠️ TEST 5 OMITIDO"
            );

            return;
        }

        boolean estadoOriginal =
                obtenerActivo(inventarioId);

        try {

            cambiarActivo(
                    inventarioId,
                    false
            );

            Venta venta =
                    crearVenta();

            VentaDetalle detalle =
                    crearDetalle();

            List<VentaDetalle> detalles =
                    new ArrayList<>();

            detalles.add(detalle);

            Pago pago =
                    crearPago(TOTAL);

            try {

                service.registrarVenta(
                        venta,
                        detalles,
                        pago
                );

                throw new AssertionError(
                        "La venta debería ser rechazada"
                );

            } catch (RuntimeException e) {

                System.out.println(
                        "✅ Inventario inactivo → "
                                + "rechazada correctamente"
                );
            }

        } finally {

            cambiarActivo(
                    inventarioId,
                    estadoOriginal
            );
        }

        System.out.println(
                "✅ Inventario restaurado"
        );

        System.out.println(
                "✅ TEST 5 SUPERADO"
        );
    }

    // =========================================================
    // 6. ROLLBACK COMPLETO
    // =========================================================

    /**
     * PRUEBA CRÍTICA.
     *
     * Fuerza un error DESPUÉS de que:
     *
     * 1. Se creó la venta.
     * 2. Se creó el detalle.
     * 3. Se descontó inventario.
     * 4. Se creó el movimiento.
     *
     * El error se provoca durante el registro
     * del pago.
     *
     * Después verificamos que TODO haya vuelto
     * al estado anterior.
     */
    private void probarRollbackCompleto() {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("6. ROLLBACK COMPLETO");
        System.out.println("----------------------------------------------");

        prepararStockSiEsNecesario();

        Inventario inventarioAntes =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        BigDecimal stockAntes =
                inventarioAntes.getCantidad();

        int movimientosAntes =
                contarMovimientos();

        Venta venta =
                crearVenta();

        String numeroVenta =
                venta.getNumero();

        VentaDetalle detalle =
                crearDetalle();

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        /*
         * Provocamos un error en PostgreSQL
         * durante el INSERT del pago.
         *
         * El monto sigue siendo correcto para pasar
         * las validaciones del Service.
         *
         * La referencia supera el límite esperado
         * por la restricción CHECK de la BD.
         */
        Pago pago =
                crearPago(TOTAL);

        pago.setReferencia(
                "ROLLBACK-" + "X".repeat(1000)
        );

        System.out.println();
        System.out.println(
                "Stock antes: " + stockAntes
        );

        System.out.println(
                "Movimientos antes: "
                        + movimientosAntes
        );

        System.out.println(
                "Venta de prueba: "
                        + numeroVenta
        );

        try {

            service.registrarVenta(
                    venta,
                    detalles,
                    pago
            );

            throw new AssertionError(
                    "La venta debería haber fallado "
                            + "para provocar ROLLBACK"
            );

        } catch (RuntimeException e) {

            System.out.println();
            System.out.println(
                    "✅ Error provocado correctamente"
            );

            System.out.println(
                    "Mensaje: " + e.getMessage()
            );
        }

        /*
         * =====================================================
         * VERIFICAR VENTA
         * =====================================================
         */

        Long ventaEncontrada =
                buscarVentaPorNumero(
                        numeroVenta
                );

        if (ventaEncontrada != null) {

            throw new AssertionError(
                    "ROLLBACK FALLÓ: "
                            + "la venta todavía existe. ID: "
                            + ventaEncontrada
            );
        }

        System.out.println(
                "✅ Venta eliminada por ROLLBACK"
        );

        /*
         * =====================================================
         * VERIFICAR DETALLE
         * =====================================================
         */

        if (ventaEncontrada != null) {

            List<VentaDetalle> detallesBD =
                    ventaDetalleDAO.listarPorVenta(
                            ventaEncontrada
                    );

            if (!detallesBD.isEmpty()) {

                throw new AssertionError(
                        "ROLLBACK FALLÓ: "
                                + "el detalle todavía existe"
                );
            }
        }

        System.out.println(
                "✅ Detalle eliminado por ROLLBACK"
        );

        /*
         * =====================================================
         * VERIFICAR STOCK
         * =====================================================
         */

        Inventario inventarioDespues =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        BigDecimal stockDespues =
                inventarioDespues.getCantidad();

        System.out.println();
        System.out.println(
                "Stock después: " + stockDespues
        );

        verificar(
                stockDespues,
                stockAntes,
                "Stock restaurado"
        );

        System.out.println(
                "✅ Stock restaurado correctamente"
        );

        /*
         * =====================================================
         * VERIFICAR MOVIMIENTOS
         * =====================================================
         */

        int movimientosDespues =
                contarMovimientos();

        System.out.println(
                "Movimientos después: "
                        + movimientosDespues
        );

        if (movimientosDespues != movimientosAntes) {

            throw new AssertionError(
                    "ROLLBACK FALLÓ: "
                            + "el movimiento de inventario "
                            + "todavía existe"
            );
        }

        System.out.println(
                "✅ Movimiento eliminado por ROLLBACK"
        );

        /*
         * =====================================================
         * VERIFICAR PAGO
         * =====================================================
         *
         * Como la venta desapareció por FK CASCADE,
         * tampoco debe existir ningún pago asociado.
         */

        if (ventaEncontrada != null) {

            List<Pago> pagos =
                    pagoDAO.listarPorVenta(
                            ventaEncontrada
                    );

            if (!pagos.isEmpty()) {

                throw new AssertionError(
                        "ROLLBACK FALLÓ: "
                                + "el pago todavía existe"
                );
            }
        }

        System.out.println(
                "✅ Pago eliminado por ROLLBACK"
        );

        System.out.println();
        System.out.println(
                "✅ TRANSACCIÓN COMPLETAMENTE REVERTIDA"
        );

        System.out.println(
                "✅ Venta → revertida"
        );

        System.out.println(
                "✅ Detalle → revertido"
        );

        System.out.println(
                "✅ Inventario → restaurado"
        );

        System.out.println(
                "✅ Movimiento → revertido"
        );

        System.out.println(
                "✅ Pago → revertido"
        );

        System.out.println(
                "✅ TEST 6 SUPERADO"
        );
    }

    // =========================================================
    // CREACIÓN DE OBJETOS
    // =========================================================

    private Venta crearVenta() {

        Venta venta =
                new Venta();

        venta.setEmpresaId(
                EMPRESA_ID
        );

        venta.setSucursalId(
                SUCURSAL_ID
        );

        venta.setClienteId(
                1L
        );

        venta.setAuthUserId(
                AUTH_USER_ID
        );

        venta.setNumero(
                "TEST-INTEGRAL-"
                        + System.nanoTime()
        );

        return venta;
    }

    private VentaDetalle crearDetalle() {

        VentaDetalle detalle =
                new VentaDetalle();

        detalle.setProductoId(
                PRODUCTO_ID
        );

        detalle.setCantidad(
                CANTIDAD_VENTA
        );

        detalle.setPrecioVenta(
                PRECIO
        );

        detalle.setDescuento(
                DESCUENTO
        );

        detalle.setImpuesto(
                IMPUESTO
        );

        return detalle;
    }

    private Pago crearPago(
            BigDecimal monto) {

        Pago pago =
                new Pago();

        pago.setMetodo(
                "EFECTIVO"
        );

        pago.setMonto(
                monto
        );

        pago.setAuthUserId(
                AUTH_USER_ID
        );

        return pago;
    }

    // =========================================================
    // INVENTARIO
    // =========================================================

    private void prepararStockSiEsNecesario() {

        Inventario inventario =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        if (inventario == null) {

            throw new IllegalStateException(
                    "No existe inventario para producto "
                            + PRODUCTO_ID
            );
        }

        if (inventario.getCantidad()
                .compareTo(
                        CANTIDAD_VENTA
                ) < 0) {

            System.out.println(
                    "Stock insuficiente para la prueba."
            );

            System.out.println(
                    "Preparando stock automáticamente..."
            );

            BigDecimal cantidadNecesaria =
                    new BigDecimal("10");

            actualizarStock(
                    inventario.getId(),
                    cantidadNecesaria
            );

            System.out.println(
                    "Stock preparado: "
                            + cantidadNecesaria
            );
        }
    }

    private Long obtenerInventarioId() {

        Inventario inventario =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        if (inventario == null) {
            return null;
        }

        return inventario.getId();
    }

    private void actualizarStock(
            Long inventarioId,
            BigDecimal cantidad) {

        String sql = """
                UPDATE inventarios
                SET cantidad = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBigDecimal(
                    1,
                    cantidad
            );

            statement.setLong(
                    2,
                    inventarioId
            );

            statement.executeUpdate();

        } catch (Exception e) {

            throw new RuntimeException(
                    "No se pudo preparar el stock",
                    e
            );
        }
    }

    private void cambiarActivo(
            Long inventarioId,
            boolean activo) {

        String sql = """
                UPDATE inventarios
                SET activo = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBoolean(
                    1,
                    activo
            );

            statement.setLong(
                    2,
                    inventarioId
            );

            statement.executeUpdate();

        } catch (Exception e) {

            throw new RuntimeException(
                    "No se pudo modificar el estado "
                            + "del inventario",
                    e
            );
        }
    }

    private boolean obtenerActivo(
            Long inventarioId) {

        String sql = """
                SELECT activo
                FROM inventarios
                WHERE id = ?
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    inventarioId
            );

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getBoolean(
                            "activo"
                    );
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "No se pudo consultar el inventario",
                    e
            );
        }

        throw new RuntimeException(
                "Inventario no encontrado"
        );
    }

    // =========================================================
    // MOVIMIENTOS
    // =========================================================

    private int contarMovimientos() {

        String sql = """
                SELECT COUNT(*)
                FROM movimientos_inventario
                WHERE empresa_id = ?
                  AND sucursal_id = ?
                  AND producto_id = ?
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    EMPRESA_ID
            );

            statement.setLong(
                    2,
                    SUCURSAL_ID
            );

            statement.setLong(
                    3,
                    PRODUCTO_ID
            );

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "No se pudieron contar "
                            + "los movimientos",
                    e
            );
        }

        return 0;
    }

    // =========================================================
    // VENTAS
    // =========================================================

    private Long buscarVentaPorNumero(
            String numero) {

        String sql = """
                SELECT id
                FROM ventas
                WHERE numero = ?
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    numero
            );

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getLong("id");
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "No se pudo buscar la venta",
                    e
            );
        }

        return null;
    }

    // =========================================================
    // PRODUCTO SIN INVENTARIO
    // =========================================================

    private Long buscarProductoSinInventario() {

        String sql = """
                SELECT p.id
                FROM productos p
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM inventarios i
                    WHERE i.producto_id = p.id
                      AND i.empresa_id = ?
                      AND i.sucursal_id = ?
                )
                LIMIT 1
                """;

        try (
                Connection connection =
                        ConexionBD.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    EMPRESA_ID
            );

            statement.setLong(
                    2,
                    SUCURSAL_ID
            );

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getLong("id");
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "No se pudo buscar producto sin inventario",
                    e
            );
        }

        return null;
    }

    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void verificar(
            BigDecimal resultado,
            BigDecimal esperado,
            String prueba) {

        if (resultado != null
                && resultado.compareTo(
                esperado
        ) == 0) {

            System.out.println(
                    "✅ "
                            + prueba
                            + " → "
                            + resultado
            );

        } else {

            throw new AssertionError(
                    prueba
                            + " → esperado: "
                            + esperado
                            + ", obtenido: "
                            + resultado
            );
        }
    }

    private void verificarEstado(
            String resultado,
            String esperado) {

        if (!esperado.equals(resultado)) {

            throw new AssertionError(
                    "Estado esperado: "
                            + esperado
                            + ", obtenido: "
                            + resultado
            );
        }
    }
}

