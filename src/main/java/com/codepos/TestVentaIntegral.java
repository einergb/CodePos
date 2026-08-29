package com.codepos;

import com.codepos.model.Pago;
import com.codepos.model.Venta;
import com.codepos.model.VentaDetalle;
import com.codepos.service.PagoService;
import com.codepos.service.VentaDetalleService;
import com.codepos.service.VentaService;

import java.math.BigDecimal;
import java.util.List;

public class TestVentaIntegral {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       TEST VENTA INTEGRAL");
        System.out.println("=================================");

        /*
         * DATOS CONTROLADOS DE PRUEBA
         *
         * Empresa: 1
         * Sucursal: 1
         * Cliente: 1
         * Usuario autenticado: 1
         * Producto: 2
         */

        Long empresaId = 1L;
        Long sucursalId = 1L;
        Long clienteId = 1L;
        Integer authUserId = 1;
        Long productoId = 2L;

        BigDecimal precioVenta =
                new BigDecimal("250000.00");

        BigDecimal cantidad =
                new BigDecimal("1.000");

        BigDecimal descuento =
                new BigDecimal("0.00");

        BigDecimal impuesto =
                new BigDecimal("0.00");

        BigDecimal subtotal =
                precioVenta.multiply(cantidad);

        BigDecimal total =
                subtotal
                        .subtract(descuento)
                        .add(impuesto);

        /*
         * Servicios
         */

        VentaService ventaService =
                new VentaService();

        VentaDetalleService detalleService =
                new VentaDetalleService();

        PagoService pagoService =
                new PagoService();

        try {

            // =================================
            // 1. CREAR CABECERA DE VENTA
            // =================================

            System.out.println();
            System.out.println("1. Creando venta...");

            Venta venta = new Venta();

            venta.setEmpresaId(empresaId);
            venta.setSucursalId(sucursalId);
            venta.setClienteId(clienteId);
            venta.setAuthUserId(authUserId);

            venta.setNumero(
                    "INTEGRAL-"
                            + System.currentTimeMillis()
            );

            venta.setSubtotal(subtotal);
            venta.setDescuento(descuento);
            venta.setImpuesto(impuesto);
            venta.setTotal(total);

            venta.setObservaciones(
                    "Venta de prueba integral CodePOS"
            );

            Long ventaId =
                    ventaService.crear(venta);

            System.out.println(
                    "✅ Venta creada"
            );

            System.out.println(
                    "ID venta: " + ventaId
            );

            System.out.println(
                    "Empresa: " + empresaId
            );

            System.out.println(
                    "Sucursal: " + sucursalId
            );

            System.out.println(
                    "Cliente: " + clienteId
            );

            System.out.println(
                    "Usuario autenticado: "
                            + authUserId
            );

            System.out.println(
                    "Estado: "
                            + venta.getEstado()
            );

            // =================================
            // 2. CREAR DETALLE
            // =================================

            System.out.println();
            System.out.println(
                    "2. Creando detalle..."
            );

            VentaDetalle detalle =
                    new VentaDetalle();

            detalle.setVentaId(ventaId);
            detalle.setProductoId(productoId);
            detalle.setCantidad(cantidad);
            detalle.setPrecioVenta(precioVenta);
            detalle.setDescuento(descuento);
            detalle.setImpuesto(impuesto);
            detalle.setSubtotal(subtotal);

            Long detalleId =
                    detalleService.crear(detalle);

            System.out.println(
                    "✅ Detalle creado"
            );

            System.out.println(
                    "ID detalle: " + detalleId
            );

            System.out.println(
                    "Producto: " + productoId
            );

            System.out.println(
                    "Cantidad: " + cantidad
            );

            System.out.println(
                    "Subtotal: $" + subtotal
            );

            // =================================
            // 3. CONSULTAR DETALLES
            // =================================

            System.out.println();
            System.out.println(
                    "3. Verificando detalles..."
            );

            List<VentaDetalle> detalles =
                    detalleService.listarPorVenta(
                            ventaId
                    );

            System.out.println(
                    "Total detalles: "
                            + detalles.size()
            );

            for (VentaDetalle d : detalles) {

                System.out.println(
                        d.getId()
                                + " | Producto: "
                                + d.getProductoId()
                                + " | Cantidad: "
                                + d.getCantidad()
                                + " | Subtotal: "
                                + d.getSubtotal()
                );
            }

            // =================================
            // 4. CREAR PAGO
            // =================================

            System.out.println();
            System.out.println(
                    "4. Creando pago..."
            );

            BigDecimal montoPagado =
                    new BigDecimal("300000.00");

            Pago pago =
                    new Pago();

            pago.setVentaId(ventaId);
            pago.setMetodo("EFECTIVO");
            pago.setMonto(montoPagado);
            pago.setReferencia(
                    "INTEGRAL-EFECTIVO-"
                            + System.currentTimeMillis()
            );

            Long pagoId =
                    pagoService.crear(pago);

            System.out.println(
                    "✅ Pago creado"
            );

            System.out.println(
                    "ID pago: " + pagoId
            );

            System.out.println(
                    "Método: "
                            + pago.getMetodo()
            );

            System.out.println(
                    "Monto: $"
                            + montoPagado
            );

            // =================================
            // 5. CONSULTAR PAGOS
            // =================================

            System.out.println();
            System.out.println(
                    "5. Verificando pagos..."
            );

            List<Pago> pagos =
                    pagoService.listarPorVenta(
                            ventaId
                    );

            System.out.println(
                    "Total pagos de esta venta: "
                            + pagos.size()
            );

            BigDecimal totalPagado =
                    BigDecimal.ZERO;

            for (Pago p : pagos) {

                System.out.println(
                        p.getId()
                                + " | Método: "
                                + p.getMetodo()
                                + " | Monto: $"
                                + p.getMonto()
                );

                totalPagado =
                        totalPagado.add(
                                p.getMonto()
                        );
            }

            // =================================
            // 6. CALCULAR SALDO / CAMBIO
            // =================================

            System.out.println();
            System.out.println(
                    "6. Calculando saldo y cambio..."
            );

            BigDecimal diferencia =
                    totalPagado.subtract(total);

            BigDecimal saldo =
                    BigDecimal.ZERO;

            BigDecimal cambio =
                    BigDecimal.ZERO;

            if (diferencia.compareTo(
                    BigDecimal.ZERO) < 0) {

                saldo =
                        diferencia.abs();

            } else {

                cambio =
                        diferencia;
            }

            System.out.println(
                    "Total venta: $"
                            + total
            );

            System.out.println(
                    "Total pagado: $"
                            + totalPagado
            );

            System.out.println(
                    "Saldo pendiente: $"
                            + saldo
            );

            System.out.println(
                    "Cambio: $"
                            + cambio
            );

            // =================================
            // 7. VALIDACIONES DEL FLUJO
            // =================================

            System.out.println();
            System.out.println(
                    "7. Validando resultado..."
            );

            if (total.compareTo(
                    new BigDecimal("250000.00")
            ) != 0) {

                throw new RuntimeException(
                        "El total de la venta no coincide"
                );
            }

            if (totalPagado.compareTo(
                    new BigDecimal("300000.00")
            ) != 0) {
                throw new RuntimeException(
                        "El total pagado no coincide"
                );
            }

            if (cambio.compareTo(
                    new BigDecimal("50000.00")
            ) != 0) {
                throw new RuntimeException(
                        "El cambio calculado no coincide"
                );
            }

            if (saldo.compareTo(
                    BigDecimal.ZERO
            ) != 0) {

                throw new RuntimeException(
                        "La venta no debería tener saldo pendiente"
                );
            }

            System.out.println(
                    "✅ Total correcto"
            );

            System.out.println(
                    "✅ Pago correcto"
            );

            System.out.println(
                    "✅ Cambio correcto"
            );

            System.out.println(
                    "✅ Saldo correcto"
            );

            // =================================
            // 8. RECUPERAR VENTA
            // =================================

            System.out.println();
            System.out.println(
                    "8. Recuperando venta..."
            );

            Venta ventaRecuperada =
                    ventaService.buscarPorId(
                            empresaId,
                            ventaId
                    );

            if (ventaRecuperada == null) {

                throw new RuntimeException(
                        "No se pudo recuperar la venta"
                );
            }

            System.out.println(
                    "✅ Venta recuperada"
            );

            System.out.println(
                    "ID: "
                            + ventaRecuperada.getId()
            );

            System.out.println(
                    "Número: "
                            + ventaRecuperada.getNumero()
            );

            System.out.println(
                    "Cliente ID: "
                            + ventaRecuperada.getClienteId()
            );

            System.out.println(
                    "Usuario ID: "
                            + ventaRecuperada.getAuthUserId()
            );

            System.out.println(
                    "Estado: "
                            + ventaRecuperada.getEstado()
            );

            System.out.println(
                    "Total: $"
                            + ventaRecuperada.getTotal()
            );

            // =================================
            // RESULTADO FINAL
            // =================================

            System.out.println();
            System.out.println(
                    "================================="
            );

            System.out.println(
                    "    PRUEBA INTEGRAL EXITOSA"
            );

            System.out.println(
                    "================================="
            );

            System.out.println();
            System.out.println(
                    "Venta ID: " + ventaId
            );

            System.out.println(
                    "Detalle ID: " + detalleId
            );

            System.out.println(
                    "Pago ID: " + pagoId
            );

            System.out.println(
                    "Total: $" + total
            );

            System.out.println(
                    "Pagado: $" + totalPagado
            );

            System.out.println(
                    "Cambio: $" + cambio
            );

            System.out.println(
                    "Saldo: $" + saldo
            );

            System.out.println(
                    "Usuario: " + authUserId
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "❌ ERROR EN LA PRUEBA INTEGRAL"
            );

            e.printStackTrace();
        }
    }

}
