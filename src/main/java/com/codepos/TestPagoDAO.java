package com.codepos;

import com.codepos.dao.PagoDAO;
import com.codepos.model.Pago;

import java.math.BigDecimal;
import java.util.List;

public class TestPagoDAO {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        TEST PAGO DAO");
        System.out.println("=================================");

        PagoDAO pagoDAO = new PagoDAO();

        try {

            /*
             * =========================================
             * 1. BUSCAR PAGO
             * =========================================
             */

            System.out.println();
            System.out.println("1. Buscando pago...");

            Pago pago = pagoDAO.buscarPorId(1L);

            if (pago != null) {

                System.out.println("✅ Pago encontrado");
                System.out.println(
                        "ID: " + pago.getId()
                );

                System.out.println(
                        "Venta ID: " + pago.getVentaId()
                );

                System.out.println(
                        "Usuario ID: " + pago.getAuthUserId()
                );

                System.out.println(
                        "Método: " + pago.getMetodo()
                );

                System.out.println(
                        "Monto: $" + pago.getMonto()
                );

                System.out.println(
                        "Referencia: " + pago.getReferencia()
                );

            } else {

                System.out.println(
                        "⚠️ No existe un pago con ID 1"
                );
            }


            /*
             * =========================================
             * 2. LISTAR PAGOS DE UNA VENTA
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "2. Listando pagos de la venta..."
            );

            List<Pago> pagos =
                    pagoDAO.listarPorVenta(1L);

            System.out.println(
                    "Total encontrados: " + pagos.size()
            );

            for (Pago p : pagos) {

                System.out.println(
                        p.getId()
                                + " | Venta: "
                                + p.getVentaId()
                                + " | Usuario: "
                                + p.getAuthUserId()
                                + " | Método: "
                                + p.getMetodo()
                                + " | Monto: $"
                                + p.getMonto()
                );
            }


            /*
             * =========================================
             * 3. CREAR PAGO
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "3. Creando pago de prueba..."
            );

            Pago nuevoPago = new Pago();

            /*
             * Utilizamos una venta existente.
             *
             * La venta 1 ya existe en nuestra
             * base de datos.
             */
            nuevoPago.setVentaId(1L);

            /*
             * Usuario autenticado.
             *
             * Utilizamos el usuario 1 que ya
             * utilizamos en la prueba integral.
             */
            nuevoPago.setAuthUserId(1);

            nuevoPago.setMetodo("EFECTIVO");

            nuevoPago.setMonto(
                    new BigDecimal("50000.00")
            );

            nuevoPago.setReferencia(
                    "TEST-PAGO-DAO"
            );

            Long pagoId =
                    pagoDAO.crear(nuevoPago);

            System.out.println(
                    "✅ Pago creado"
            );

            System.out.println(
                    "ID generado: " + pagoId
            );


            /*
             * =========================================
             * 4. RECUPERAR PAGO CREADO
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "4. Consultando pago creado..."
            );

            Pago pagoCreado =
                    pagoDAO.buscarPorId(pagoId);

            if (pagoCreado == null) {

                throw new RuntimeException(
                        "No se pudo recuperar el pago creado"
                );
            }

            System.out.println(
                    "✅ Pago recuperado correctamente"
            );

            System.out.println(
                    "ID: " + pagoCreado.getId()
            );

            System.out.println(
                    "Venta ID: "
                            + pagoCreado.getVentaId()
            );

            System.out.println(
                    "Usuario ID: "
                            + pagoCreado.getAuthUserId()
            );

            System.out.println(
                    "Método: "
                            + pagoCreado.getMetodo()
            );

            System.out.println(
                    "Monto: $"
                            + pagoCreado.getMonto()
            );

            System.out.println(
                    "Referencia: "
                            + pagoCreado.getReferencia()
            );


            /*
             * =========================================
             * 5. VALIDACIONES
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "5. Validando datos..."
            );

            if (!"EFECTIVO".equals(
                    pagoCreado.getMetodo()
            )) {

                throw new RuntimeException(
                        "El método de pago no coincide"
                );
            }

            if (new BigDecimal("50000.00")
                    .compareTo(pagoCreado.getMonto()) != 0) {

                throw new RuntimeException(
                        "El monto del pago no coincide"
                );
            }

            if (!"TEST-PAGO-DAO".equals(
                    pagoCreado.getReferencia()
            )) {

                throw new RuntimeException(
                        "La referencia no coincide"
                );
            }

            System.out.println(
                    "✅ Método correcto"
            );

            System.out.println(
                    "✅ Monto correcto"
            );

            System.out.println(
                    "✅ Referencia correcta"
            );

            /*
             * =========================================
             * FINAL
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "================================="
            );

            System.out.println(
                    "       PRUEBA FINALIZADA"
            );

            System.out.println(
                    "================================="
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "❌ ERROR EN LA PRUEBA"
            );

            e.printStackTrace();
        }
    }
}