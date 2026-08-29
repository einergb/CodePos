package com.codepos;

import com.codepos.dao.ProductoDAO;
import com.codepos.model.Producto;

import java.math.BigDecimal;
import java.util.List;

public class TestProductoDAO {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       TEST PRODUCTO DAO");
        System.out.println("=================================");

        ProductoDAO productoDAO =
                new ProductoDAO();

        try {

            /*
             * =========================================
             * 1. BUSCAR PRODUCTO
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "1. Buscando producto..."
            );

            Producto producto =
                    productoDAO.buscarPorId(
                            1L,
                            2L
                    );

            if (producto != null) {

                System.out.println(
                        "✅ Producto encontrado"
                );

                System.out.println(
                        "ID: " + producto.getId()
                );

                System.out.println(
                        "Empresa: "
                                + producto.getEmpresaId()
                );

                System.out.println(
                        "SKU: "
                                + producto.getSku()
                );

                System.out.println(
                        "Nombre: "
                                + producto.getNombre()
                );

                System.out.println(
                        "Precio venta: $"
                                + producto.getPrecioVenta()
                );

                System.out.println(
                        "Aplica IVA: "
                                + producto.getAplicaIva()
                );

                System.out.println(
                        "IVA: "
                                + producto.getIvaPorcentaje()
                                + "%"
                );

            } else {

                System.out.println(
                        "⚠️ No existe el producto 2"
                );
            }


            /*
             * =========================================
             * 2. LISTAR PRODUCTOS
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "2. Listando productos..."
            );

            List<Producto> productos =
                    productoDAO.listarPorEmpresa(1L);

            System.out.println(
                    "Total encontrados: "
                            + productos.size()
            );

            for (Producto p : productos) {

                System.out.println(
                        p.getId()
                                + " | SKU: "
                                + p.getSku()
                                + " | Nombre: "
                                + p.getNombre()
                                + " | Precio: $"
                                + p.getPrecioVenta()
                                + " | IVA: "
                                + p.getAplicaIva()
                                + " / "
                                + p.getIvaPorcentaje()
                                + "%"
                );
            }


            /*
             * =========================================
             * 3. CREAR PRODUCTO CON IVA
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "3. Creando producto con IVA..."
            );

            Producto productoIva =
                    new Producto();

            productoIva.setEmpresaId(1L);

            /*
             * Utilizamos una unidad de medida
             * existente.
             *
             * Ajustaremos este ID si PostgreSQL
             * indica que no existe.
             */
            productoIva.setUnidadMedidaId(1L);

            productoIva.setSku(
                    "TEST-IVA-001"
            );

            productoIva.setCodigoBarras(
                    "7700000000011"
            );

            productoIva.setNombre(
                    "Producto prueba IVA"
            );

            productoIva.setDescripcion(
                    "Producto creado para probar IVA"
            );

            productoIva.setPrecioCompra(
                    new BigDecimal("10000.00")
            );

            productoIva.setPrecioVenta(
                    new BigDecimal("20000.00")
            );

            productoIva.setAplicaIva(true);

            productoIva.setIvaPorcentaje(
                    new BigDecimal("19.00")
            );

            productoIva.setActivo(true);

            Long productoIvaId =
                    productoDAO.crear(
                            productoIva
                    );

            System.out.println(
                    "✅ Producto con IVA creado"
            );

            System.out.println(
                    "ID generado: "
                            + productoIvaId
            );


            /*
             * =========================================
             * 4. RECUPERAR PRODUCTO CON IVA
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "4. Recuperando producto con IVA..."
            );

            Producto productoIvaCreado =
                    productoDAO.buscarPorId(
                            1L,
                            productoIvaId
                    );

            if (productoIvaCreado == null) {

                throw new RuntimeException(
                        "No se pudo recuperar "
                                + "el producto con IVA"
                );
            }

            System.out.println(
                    "✅ Producto recuperado"
            );

            System.out.println(
                    "SKU: "
                            + productoIvaCreado.getSku()
            );

            System.out.println(
                    "Precio: $"
                            + productoIvaCreado
                            .getPrecioVenta()
            );

            System.out.println(
                    "Aplica IVA: "
                            + productoIvaCreado
                            .getAplicaIva()
            );

            System.out.println(
                    "IVA: "
                            + productoIvaCreado
                            .getIvaPorcentaje()
                            + "%"
            );


            /*
             * =========================================
             * 5. VALIDAR IVA
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "5. Validando configuración IVA..."
            );

            if (!Boolean.TRUE.equals(
                    productoIvaCreado.getAplicaIva()
            )) {

                throw new RuntimeException(
                        "El producto debería aplicar IVA"
                );
            }

            if (new BigDecimal("19.00")
                    .compareTo(
                            productoIvaCreado
                                    .getIvaPorcentaje()
                    ) != 0) {

                throw new RuntimeException(
                        "El IVA debería ser 19%"
                );
            }

            System.out.println(
                    "✅ Aplica IVA correctamente"
            );

            System.out.println(
                    "✅ IVA 19% correctamente almacenado"
            );


            /*
             * =========================================
             * 6. CREAR PRODUCTO SIN IVA
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "6. Creando producto sin IVA..."
            );

            Producto productoSinIva =
                    new Producto();

            productoSinIva.setEmpresaId(1L);
            productoSinIva.setUnidadMedidaId(1L);

            productoSinIva.setSku(
                    "TEST-SIN-IVA-001"
            );

            productoSinIva.setCodigoBarras(
                    "7700000000028"
            );

            productoSinIva.setNombre(
                    "Producto prueba sin IVA"
            );

            productoSinIva.setDescripcion(
                    "Producto creado para probar "
                            + "productos sin IVA"
            );

            productoSinIva.setPrecioCompra(
                    new BigDecimal("10000.00")
            );

            productoSinIva.setPrecioVenta(
                    new BigDecimal("20000.00")
            );

            productoSinIva.setAplicaIva(false);

            productoSinIva.setIvaPorcentaje(
                    BigDecimal.ZERO
            );

            productoSinIva.setActivo(true);

            Long productoSinIvaId =
                    productoDAO.crear(
                            productoSinIva
                    );

            System.out.println(
                    "✅ Producto sin IVA creado"
            );

            System.out.println(
                    "ID generado: "
                            + productoSinIvaId
            );


            /*
             * =========================================
             * 7. VALIDAR PRODUCTO SIN IVA
             * =========================================
             */

            System.out.println();
            System.out.println(
                    "7. Validando producto sin IVA..."
            );

            Producto productoSinIvaCreado =
                    productoDAO.buscarPorId(
                            1L,
                            productoSinIvaId
                    );

            if (productoSinIvaCreado == null) {

                throw new RuntimeException(
                        "No se pudo recuperar "
                                + "el producto sin IVA"
                );
            }

            if (!Boolean.FALSE.equals(
                    productoSinIvaCreado.getAplicaIva()
            )) {

                throw new RuntimeException(
                        "El producto no debería "
                                + "aplicar IVA"
                );
            }

            if (new BigDecimal("0.00")
                    .compareTo(
                            productoSinIvaCreado
                                    .getIvaPorcentaje()
                    ) != 0) {

                throw new RuntimeException(
                        "El IVA del producto sin IVA "
                                + "debería ser 0"
                );
            }

            System.out.println(
                    "✅ Producto sin IVA correcto"
            );

            System.out.println(
                    "✅ Porcentaje almacenado: 0%"
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
                    "    PRUEBA PRODUCTO EXITOSA"
            );

            System.out.println(
                    "================================="
            );

            System.out.println();
            System.out.println(
                    "Producto IVA ID: "
                            + productoIvaId
            );

            System.out.println(
                    "Producto sin IVA ID: "
                            + productoSinIvaId
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