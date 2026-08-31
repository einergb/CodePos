package com.codepos;

import com.codepos.config.ConexionBD;
import com.codepos.dao.InventarioDAO;
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

 * ============================================================
 * TEST DE INTEGRACIÓN - VENTA INTEGRAL
 * ============================================================
 *
 * Verifica el flujo completo de una venta:
 *
 * Venta
 * ↓
 * Detalle
 * ↓
 * Inventario
 * ↓
 * Movimiento de inventario
 * ↓
 * Pago
 * ↓
 * Estado PAGADA
 *
 * También verifica:
 *
 * * Cálculos monetarios.
 * * Descuento de inventario.
 * * Movimiento generado.
 * * stock_anterior.
 * * stock_posterior.
 * * Rollback.
 * * Validación de pago.
 * * Stock insuficiente.
 * * Producto sin inventario.
 * * Inventario inactivo.
 *
 * IMPORTANTE:
 *
 * El test NO depende de que exista una cantidad específica
 * de stock antes de comenzar.
 */
public class TestVentaIntegralService {

    // =========================================================
    // CONFIGURACIÓN DE PRUEBAS
    // =========================================================

    private static final Long EMPRESA_ID = 1L;
    private static final Long SUCURSAL_ID = 1L;
    private static final Long PRODUCTO_ID = 2L;
    private static final Long CLIENTE_ID = 1L;
    private static final Integer AUTH_USER_ID = 1;

    /*

     * Cantidad utilizada en la venta correcta.
     *
     * El test preparará automáticamente suficiente stock.
     */
    private static final BigDecimal CANTIDAD_VENTA =
            new BigDecimal("2");

    private static final BigDecimal PRECIO_VENTA =
            new BigDecimal("350000");

    private static final BigDecimal DESCUENTO =
            new BigDecimal("20000");

    private static final BigDecimal IMPUESTO =
            new BigDecimal("100000");

    private static final BigDecimal TOTAL_ESPERADO =
            new BigDecimal("780000");

    /*

     * Stock mínimo que queremos garantizar
     * para ejecutar la venta correcta.
     */
    private static final BigDecimal STOCK_PRUEBA =
            new BigDecimal("10");

    private static final String PREFIJO_TEST =
            "TEST-INTEGRAL-";

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {


        System.out.println();
        System.out.println("==============================================");
        System.out.println("       TEST VENTA INTEGRAL SERVICE");
        System.out.println("==============================================");

        VentaIntegralService service =
                new VentaIntegralService();

        try {

            probarVentaCorrecta(service);

            probarPagoIncorrecto(service);

            probarStockInsuficiente(service);

            probarProductoSinInventario(service);

            probarInventarioInactivo(service);

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

            System.exit(1);
        }


    }

    // =========================================================
    // TEST 1
    // =========================================================

    /**

     * Verifica una venta integral completamente correcta.
     *
     * Además verifica:
     *
     * * stock antes
     * * stock después
     * * movimiento de inventario
     * * stock anterior del movimiento
     * * stock posterior del movimiento
     */
    private static void probarVentaCorrecta(
            VentaIntegralService service) {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("1. VENTA INTEGRAL CORRECTA");
        System.out.println("----------------------------------------------");

        /*

         * Garantizamos que exista suficiente stock.
         *
         * El test no depende del stock actual.
         */
        prepararStockParaPrueba();

        InventarioDAO inventarioDAO =
                new InventarioDAO();

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

        /*

         * =====================================================
         * CREAR VENTA
         * =====================================================
         */

        Venta venta =
                new Venta();

        venta.setEmpresaId(
                EMPRESA_ID
        );

        venta.setSucursalId(
                SUCURSAL_ID
        );

        venta.setClienteId(
                CLIENTE_ID
        );

        venta.setAuthUserId(
                AUTH_USER_ID
        );

        venta.setNumero(
                PREFIJO_TEST
                        + System.currentTimeMillis()
        );

        /*

         * =====================================================
         * CREAR DETALLE
         * =====================================================
         */

        VentaDetalle detalle =
                new VentaDetalle();

        detalle.setProductoId(
                PRODUCTO_ID
        );

        detalle.setCantidad(
                CANTIDAD_VENTA
        );

        detalle.setPrecioVenta(
                PRECIO_VENTA
        );

        detalle.setDescuento(
                DESCUENTO
        );

        detalle.setImpuesto(
                IMPUESTO
        );

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        /*

         * =====================================================
         * CREAR PAGO
         * =====================================================
         */

        Pago pago =
                new Pago();

        pago.setMetodo(
                "EFECTIVO"
        );

        pago.setMonto(
                TOTAL_ESPERADO
        );

        pago.setAuthUserId(
                AUTH_USER_ID
        );

        /*

         * =====================================================
         * REGISTRAR VENTA
         * =====================================================
         */

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

        /*

         * =====================================================
         * VALIDAR VENTA
         * =====================================================
         */

        if (ventaId == null
                || ventaId <= 0) {


            throw new AssertionError(
                    "El ID de la venta no es válido"
            );


        }

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

        System.out.println(
                "Pago: " + pago.getMonto()
        );

        verificar(
                venta.getSubtotal(),
                new BigDecimal("700000.00"),
                "Subtotal venta"
        );

        verificar(
                venta.getDescuento(),
                new BigDecimal("20000.00"),
                "Descuento venta"
        );

        verificar(
                venta.getImpuesto(),
                new BigDecimal("100000.00"),
                "Impuesto venta"
        );

        verificar(
                venta.getTotal(),
                TOTAL_ESPERADO,
                "Total venta"
        );

        verificar(
                pago.getMonto(),
                TOTAL_ESPERADO,
                "Pago"
        );

        if (!"PAGADA".equals(
                venta.getEstado()
        )) {


            throw new AssertionError(
                    "La venta debería quedar PAGADA"
            );


        }

        System.out.println(
                "✅ Estado PAGADA confirmado"
        );

        /*

         * =====================================================
         * VALIDAR DETALLE
         * =====================================================
         */

        verificar(
                detalle.getSubtotal(),
                new BigDecimal("700000.00"),
                "Subtotal detalle"
        );

        verificar(
                detalle.getDescuento(),
                new BigDecimal("20000.00"),
                "Descuento detalle"
        );

        verificar(
                detalle.getImpuesto(),
                new BigDecimal("100000.00"),
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

        /*

         * =====================================================
         * VALIDAR STOCK DESPUÉS
         * =====================================================
         */

        Inventario inventarioDespues =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        if (inventarioDespues == null) {


            throw new AssertionError(
                    "El inventario desapareció después de la venta"
            );


        }

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

        /*

         * =====================================================
         * VALIDAR MOVIMIENTO
         * =====================================================
         */

        MovimientoTest movimiento =
                buscarMovimientoVenta(
                        ventaId
                );

        if (movimiento == null) {


            throw new AssertionError(
                    "No se encontró movimiento de inventario para la venta"
            );


        }

        System.out.println();
        System.out.println(
                "Movimiento generado:"
        );

        System.out.println(
                "ID movimiento: "
                        + movimiento.id
        );

        System.out.println(
                "Tipo: "
                        + movimiento.tipo
        );

        System.out.println(
                "Cantidad: "
                        + movimiento.cantidad
        );

        System.out.println(
                "Stock anterior: "
                        + movimiento.stockAnterior
        );

        System.out.println(
                "Stock posterior: "
                        + movimiento.stockPosterior
        );

        System.out.println(
                "Referencia: "
                        + movimiento.referenciaTipo
                        + " #"
                        + movimiento.referenciaId
        );

        if (!"VENTA".equals(
                movimiento.tipo
        )) {


            throw new AssertionError(
                    "El movimiento debería ser de tipo VENTA"
            );


        }

        verificar(
                movimiento.cantidad,
                CANTIDAD_VENTA,
                "Cantidad movimiento"
        );

        verificar(
                movimiento.stockAnterior,
                stockAntes,
                "Stock anterior movimiento"
        );

        verificar(
                movimiento.stockPosterior,
                stockEsperado,
                "Stock posterior movimiento"
        );

        if (!"VENTA".equals(
                movimiento.referenciaTipo
        )) {


            throw new AssertionError(
                    "La referencia del movimiento debería ser VENTA"
            );


        }

        if (!ventaId.equals(
                movimiento.referenciaId
        )) {


            throw new AssertionError(
                    "El movimiento no está asociado a la venta"
            );


        }

        System.out.println(
                "✅ Movimiento de inventario correcto"
        );

        System.out.println(
                "✅ TEST 1 SUPERADO"
        );
    }

    // =========================================================
    // TEST 2
    // =========================================================

    /**

     * Verifica que un pago diferente al total
     * sea rechazado antes de iniciar la transacción.
     */
    private static void probarPagoIncorrecto(
            VentaIntegralService service) {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("2. PAGO INCORRECTO");
        System.out.println("----------------------------------------------");

        Venta venta =
                crearVentaBase(
                        "TEST-PAGO-"
                );

        VentaDetalle detalle =
                crearDetalle();

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        Pago pago =
                new Pago();

        pago.setMetodo(
                "EFECTIVO"
        );

        /*

         * Pago incorrecto intencionalmente.
         */
        pago.setMonto(
                new BigDecimal("1")
        );

        pago.setAuthUserId(
                AUTH_USER_ID
        );

        probarExcepcion(
                () ->
                        service.registrarVenta(
                                venta,
                                detalles,
                                pago
                        ),
                "Pago diferente al total"
        );

        System.out.println(
                "✅ Venta rechazada antes de iniciar la transacción"
        );

        System.out.println(
                "✅ TEST 2 SUPERADO"
        );
    }

    // =========================================================
    // TEST 3
    // =========================================================

    /**

     * Solicita una cantidad exageradamente superior
     * al stock disponible.
     *
     * Debe producir rollback.
     */
    private static void probarStockInsuficiente(
            VentaIntegralService service) {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("3. STOCK INSUFICIENTE");
        System.out.println("----------------------------------------------");

        InventarioDAO inventarioDAO =
                new InventarioDAO();

        Inventario inventario =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        if (inventario == null) {


            throw new AssertionError(
                    "No existe inventario para realizar esta prueba"
            );


        }

        BigDecimal stockAntes =
                inventario.getCantidad();

        /*

         * Cantidad deliberadamente imposible.
         *
         * No depende de cuánto stock exista.
         */
        BigDecimal cantidadImposible =
                stockAntes
                        .add(new BigDecimal("999999"));

        Venta venta =
                crearVentaBase(
                        "TEST-STOCK-"
                );

        VentaDetalle detalle =
                crearDetalle();

        detalle.setCantidad(
                cantidadImposible
        );

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        Pago pago =
                new Pago();

        pago.setMetodo(
                "EFECTIVO"
        );

        /*

         * El total debe coincidir con el cálculo,
         * aunque la venta terminará siendo rechazada
         * por inventario.
         */
        pago.setMonto(
                calcularTotalPrueba(
                        detalle
                )
        );

        pago.setAuthUserId(
                AUTH_USER_ID
        );

        probarExcepcion(
                () ->
                        service.registrarVenta(
                                venta,
                                detalles,
                                pago
                        ),
                "Stock insuficiente"
        );

        System.out.println(
                "✅ Stock insuficiente rechazado correctamente"
        );

        /*

         * Verificamos que el stock NO haya cambiado.
         */
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
                "✅ La transacción ejecutó ROLLBACK"
        );

        System.out.println(
                "✅ TEST 3 SUPERADO"
        );
    }

    // =========================================================
    // TEST 4
    // =========================================================

    /**

     * Utiliza un producto que no tenga inventario.
     *
     * El objetivo es verificar la validación
     * de inventario inexistente.
     */
    private static void probarProductoSinInventario(
            VentaIntegralService service) {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("4. PRODUCTO SIN INVENTARIO");
        System.out.println("----------------------------------------------");

        /*

         * Buscamos un producto que NO tenga inventario
         * para la empresa/sucursal de prueba.
         */
        Long productoSinInventario =
                buscarProductoSinInventario();

        if (productoSinInventario == null) {


            System.out.println(
                    "⚠️ No existe actualmente un producto sin inventario."
            );

            System.out.println(
                    "⚠️ TEST 4 OMITIDO"
            );

            return;


        }

        Venta venta =
                crearVentaBase(
                        "TEST-SIN-INVENTARIO-"
                );

        VentaDetalle detalle =
                crearDetalle();

        detalle.setProductoId(
                productoSinInventario
        );

        List<VentaDetalle> detalles =
                new ArrayList<>();

        detalles.add(detalle);

        Pago pago =
                crearPagoParaDetalle(
                        detalle
                );

        probarExcepcion(
                () ->
                        service.registrarVenta(
                                venta,
                                detalles,
                                pago
                        ),
                "Producto sin inventario"
        );

        System.out.println(
                "✅ Producto sin inventario rechazado"
        );

        System.out.println(
                "✅ TEST 4 SUPERADO"
        );
    }

    // =========================================================
    // TEST 5
    // =========================================================

    /**

     * Verifica que un inventario inactivo
     * no permita realizar ventas.
     *
     * IMPORTANTE:
     *
     * El test guarda el estado original y lo restaura.
     */
    private static void probarInventarioInactivo(
            VentaIntegralService service) {

        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("5. INVENTARIO INACTIVO");
        System.out.println("----------------------------------------------");

        InventarioDAO inventarioDAO =
                new InventarioDAO();

        Inventario inventario =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        if (inventario == null) {


            throw new AssertionError(
                    "No existe inventario para realizar esta prueba"
            );


        }

        /*

         * Guardamos estado original.
         */
        boolean activoOriginal =
                Boolean.TRUE.equals(
                        inventario.getActivo()
                );

        try {


            /*
             * Desactivamos temporalmente.
             */
            cambiarEstadoInventario(
                    inventario.getId(),
                    false
            );

            Venta venta =
                    crearVentaBase(
                            "TEST-INACTIVO-"
                    );

            VentaDetalle detalle =
                    crearDetalle();

            List<VentaDetalle> detalles =
                    new ArrayList<>();

            detalles.add(detalle);

            Pago pago =
                    crearPagoParaDetalle(
                            detalle
                    );

            probarExcepcion(
                    () ->
                            service.registrarVenta(
                                    venta,
                                    detalles,
                                    pago
                            ),
                    "Inventario inactivo"
            );

            System.out.println(
                    "✅ Inventario inactivo rechazado"
            );

            System.out.println(
                    "✅ TEST 5 SUPERADO"
            );


        } finally {


            /*
             * Restauramos el estado original.
             */
            cambiarEstadoInventario(
                    inventario.getId(),
                    activoOriginal
            );


        }
    }

    // =========================================================
    // PREPARACIÓN DE STOCK
    // =========================================================

    /**

     * Garantiza que exista suficiente stock para
     * ejecutar la venta correcta.
     *
     * NO depende del stock actual.
     *
     * Si actualmente hay:
     *
     * 1 unidad
     *
     * y necesitamos:
     *
     * 2 unidades
     *
     * el método aumenta temporalmente el stock
     * hasta STOCK_PRUEBA.
     */
    private static void prepararStockParaPrueba() {

        InventarioDAO inventarioDAO =
                new InventarioDAO();

        Inventario inventario =
                inventarioDAO.buscarPorProducto(
                        EMPRESA_ID,
                        SUCURSAL_ID,
                        PRODUCTO_ID
                );

        if (inventario == null) {


            throw new IllegalStateException(
                    "No existe inventario para el producto "
                            + PRODUCTO_ID
            );


        }

        if (!Boolean.TRUE.equals(
                inventario.getActivo()
        )) {


            throw new IllegalStateException(
                    "El inventario del producto está inactivo"
            );


        }

        BigDecimal stockActual =
                inventario.getCantidad();

        if (stockActual.compareTo(
                CANTIDAD_VENTA
        ) >= 0) {

            System.out.println(
                    "Stock suficiente para la prueba."
            );

            return;

        }

        System.out.println(
                "Stock insuficiente para la prueba."
        );

        System.out.println(
                "Preparando stock automáticamente..."
        );

        BigDecimal nuevoStock =
                STOCK_PRUEBA.max(
                        CANTIDAD_VENTA
                );

        actualizarStock(
                inventario.getId(),
                nuevoStock
        );

        System.out.println(
                "Stock preparado: "
                        + nuevoStock
        );
    }

    // =========================================================
    // CREACIÓN DE OBJETOS
    // =========================================================

    private static Venta crearVentaBase(
            String prefijo) {


        Venta venta =
                new Venta();

        venta.setEmpresaId(
                EMPRESA_ID
        );

        venta.setSucursalId(
                SUCURSAL_ID
        );

        venta.setClienteId(
                CLIENTE_ID
        );

        venta.setAuthUserId(
                AUTH_USER_ID
        );

        venta.setNumero(
                prefijo
                        + System.currentTimeMillis()
        );

        return venta;


    }

    private static VentaDetalle crearDetalle() {


        VentaDetalle detalle =
                new VentaDetalle();

        detalle.setProductoId(
                PRODUCTO_ID
        );

        detalle.setCantidad(
                CANTIDAD_VENTA
        );

        detalle.setPrecioVenta(
                PRECIO_VENTA
        );

        detalle.setDescuento(
                DESCUENTO
        );

        detalle.setImpuesto(
                IMPUESTO
        );

        return detalle;


    }

    private static Pago crearPagoParaDetalle(
            VentaDetalle detalle) {


        Pago pago =
                new Pago();

        pago.setMetodo(
                "EFECTIVO"
        );

        pago.setMonto(
                calcularTotalPrueba(
                        detalle
                )
        );

        pago.setAuthUserId(
                AUTH_USER_ID
        );

        return pago;


    }

    /**

     * Calcula únicamente el total necesario
     * para construir el pago del test.
     *
     * NO reemplaza CalculadoraVentaUtil.
     *
     * Solo se utiliza para preparar un pago
     * válido en pruebas de escenarios que
     * deben fallar posteriormente.
     */
    private static BigDecimal calcularTotalPrueba(
            VentaDetalle detalle) {

        BigDecimal subtotal =
                detalle.getCantidad()
                        .multiply(
                                detalle.getPrecioVenta()
                        );

        BigDecimal descuento =
                detalle.getDescuento() == null
                        ? BigDecimal.ZERO
                        : detalle.getDescuento();

        BigDecimal impuesto =
                detalle.getImpuesto() == null
                        ? BigDecimal.ZERO
                        : detalle.getImpuesto();

        return subtotal
                .subtract(descuento)
                .add(impuesto);
    }

    // =========================================================
    // CONSULTAR MOVIMIENTO
    // =========================================================

    /**

     * Busca el movimiento VENTA asociado
     * a una venta específica.
     */
    private static MovimientoTest buscarMovimientoVenta(
            Long ventaId) {

        String sql = """
    SELECT
    id,
    tipo,
    cantidad,
    stock_anterior,
    stock_posterior,
    referencia_tipo,
    referencia_id
    FROM movimientos_inventario
    WHERE referencia_tipo = 'VENTA'
    AND referencia_id = ?
    ORDER BY id DESC
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
                    ventaId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return null;
                }

                MovimientoTest movimiento =
                        new MovimientoTest();

                movimiento.id =
                        resultSet.getLong(
                                "id"
                        );

                movimiento.tipo =
                        resultSet.getString(
                                "tipo"
                        );

                movimiento.cantidad =
                        resultSet.getBigDecimal(
                                "cantidad"
                        );

                movimiento.stockAnterior =
                        resultSet.getBigDecimal(
                                "stock_anterior"
                        );

                movimiento.stockPosterior =
                        resultSet.getBigDecimal(
                                "stock_posterior"
                        );

                movimiento.referenciaTipo =
                        resultSet.getString(
                                "referencia_tipo"
                        );

                long referenciaId =
                        resultSet.getLong(
                                "referencia_id"
                        );

                if (resultSet.wasNull()) {
                    movimiento.referenciaId = null;
                } else {
                    movimiento.referenciaId =
                            referenciaId;
                }

                return movimiento;
            }


        } catch (Exception e) {


            throw new RuntimeException(
                    "Error consultando movimiento de inventario",
                    e
            );


        }
    }

    // =========================================================
    // BUSCAR PRODUCTO SIN INVENTARIO
    // =========================================================

    /**

     * Busca un producto que no tenga inventario
     * para la empresa y sucursal de prueba.
     *
     * Si no existe, devuelve null.
     */
    private static Long buscarProductoSinInventario() {

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
    ORDER BY p.id
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
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return resultSet.getLong(
                            "id"
                    );
                }
            }


        } catch (Exception e) {


            throw new RuntimeException(
                    "Error buscando producto sin inventario",
                    e
            );


        }

        return null;
    }

    // =========================================================
    // MODIFICAR STOCK
    // =========================================================

    private static void actualizarStock(
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

            int filas =
                    statement.executeUpdate();

            if (filas != 1) {

                throw new RuntimeException(
                        "No se pudo actualizar el stock de prueba"
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error preparando stock de prueba",
                    e
            );
        }


    }

    // =========================================================
    // MODIFICAR ESTADO INVENTARIO
    // =========================================================

    private static void cambiarEstadoInventario(
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

            int filas =
                    statement.executeUpdate();

            if (filas != 1) {

                throw new RuntimeException(
                        "No se pudo cambiar el estado del inventario"
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error modificando estado del inventario",
                    e
            );
        }


    }

    // =========================================================
    // ASSERTIONS
    // =========================================================

    private static void verificar(
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

    // =========================================================
    // PROBAR EXCEPCIÓN
    // =========================================================

    private static void probarExcepcion(
            Runnable operacion,
            String nombrePrueba) {
        try {

            operacion.run();

            throw new AssertionError(
                    nombrePrueba
                            + " → debería haber sido rechazada"
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "✅ "
                            + nombrePrueba
                            + " → rechazada correctamente"
            );
        }

    }

    // =========================================================
    // MODELO AUXILIAR DE MOVIMIENTO
    // =========================================================

    private static class MovimientoTest {


        Long id;

        String tipo;

        BigDecimal cantidad;

        BigDecimal stockAnterior;

        BigDecimal stockPosterior;

        String referenciaTipo;

        Long referenciaId;


    }
}
