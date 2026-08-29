package com.codepos;

import com.codepos.model.Producto;
import com.codepos.service.ProductoService;

import java.math.BigDecimal;

public class TestProductoService {


public static void main(String[] args) {

    System.out.println("=================================");
    System.out.println("    TEST PRODUCTO SERVICE");
    System.out.println("=================================");

    ProductoService productoService =
            new ProductoService();

    try {

        /*
         * =========================================
         * 1. BUSCAR PRODUCTO
         * =========================================
         */

        System.out.println();
        System.out.println("1. Buscando producto...");

        Producto producto =
                productoService.buscarPorId(
                        1L,
                        2L
                );

        if (producto == null) {

            throw new RuntimeException(
                    "No se encontró el producto"
            );
        }

        System.out.println(
                "✅ Producto encontrado"
        );

        System.out.println(
                "ID: " + producto.getId()
        );

        System.out.println(
                "SKU: " + producto.getSku()
        );

        System.out.println(
                "Nombre: " + producto.getNombre()
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


        /*
         * =========================================
         * 2. LISTAR PRODUCTOS
         * =========================================
         */

        System.out.println();
        System.out.println(
                "2. Listando productos..."
        );

        var productos =
                productoService.listarPorEmpresa(
                        1L
                );

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

        Producto productoIVA =
                new Producto();

        productoIVA.setEmpresaId(1L);
        productoIVA.setUnidadMedidaId(1L);

        productoIVA.setSku(
                "TEST-SERVICE-IVA"
        );

        productoIVA.setCodigoBarras(
                "7700000000021"
        );

        productoIVA.setNombre(
                "Producto Service con IVA"
        );

        productoIVA.setDescripcion(
                "Producto creado para probar ProductoService"
        );

        productoIVA.setPrecioCompra(
                new BigDecimal("10000.00")
        );

        productoIVA.setPrecioVenta(
                new BigDecimal("20000.00")
        );

        productoIVA.setActivo(true);

        productoIVA.setAplicaIva(true);

        productoIVA.setIvaPorcentaje(
                new BigDecimal("19.00")
        );

        Long productoIVAId =
                productoService.crear(
                        productoIVA
                );

        System.out.println(
                "✅ Producto con IVA creado"
        );

        System.out.println(
                "ID generado: "
                        + productoIVAId
        );


        /*
         * =========================================
         * 4. CREAR PRODUCTO SIN IVA
         * =========================================
         */

        System.out.println();
        System.out.println(
                "4. Creando producto sin IVA..."
        );

        Producto productoSinIVA =
                new Producto();

        productoSinIVA.setEmpresaId(1L);
        productoSinIVA.setUnidadMedidaId(1L);

        productoSinIVA.setSku(
                "TEST-SERVICE-SIN-IVA"
        );

        productoSinIVA.setCodigoBarras(
                "7700000000022"
        );

        productoSinIVA.setNombre(
                "Producto Service sin IVA"
        );

        productoSinIVA.setDescripcion(
                "Producto creado para probar producto sin IVA"
        );

        productoSinIVA.setPrecioCompra(
                new BigDecimal("10000.00")
        );

        productoSinIVA.setPrecioVenta(
                new BigDecimal("20000.00")
        );

        productoSinIVA.setActivo(true);

        productoSinIVA.setAplicaIva(false);

        productoSinIVA.setIvaPorcentaje(
                BigDecimal.ZERO
        );

        Long productoSinIVAId =
                productoService.crear(
                        productoSinIVA
                );

        System.out.println(
                "✅ Producto sin IVA creado"
        );

        System.out.println(
                "ID generado: "
                        + productoSinIVAId
        );


        /*
         * =========================================
         * 5. VALIDAR IVA INVÁLIDO
         * =========================================
         */

        System.out.println();
        System.out.println(
                "5. Probando IVA inválido..."
        );

        Producto ivaInvalido =
                new Producto();

        ivaInvalido.setEmpresaId(1L);
        ivaInvalido.setUnidadMedidaId(1L);

        ivaInvalido.setSku(
                "TEST-SERVICE-INVALIDO"
        );

        ivaInvalido.setNombre(
                "Producto IVA inválido"
        );

        ivaInvalido.setPrecioCompra(
                new BigDecimal("10000.00")
        );

        ivaInvalido.setPrecioVenta(
                new BigDecimal("20000.00")
        );

        ivaInvalido.setAplicaIva(false);

        ivaInvalido.setIvaPorcentaje(
                new BigDecimal("19.00")
        );

        try {

            productoService.crear(
                    ivaInvalido
            );

            throw new RuntimeException(
                    "El Service permitió IVA inválido"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }


        /*
         * =========================================
         * 6. VALIDAR IVA CERO CON APLICA IVA
         * =========================================
         */

        System.out.println();
        System.out.println(
                "6. Probando IVA 0% con aplicaIva=true..."
        );

        Producto ivaCero =
                new Producto();

        ivaCero.setEmpresaId(1L);
        ivaCero.setUnidadMedidaId(1L);

        ivaCero.setSku(
                "TEST-SERVICE-IVA-CERO"
        );

        ivaCero.setNombre(
                "Producto IVA cero"
        );

        ivaCero.setPrecioCompra(
                new BigDecimal("10000.00")
        );

        ivaCero.setPrecioVenta(
                new BigDecimal("20000.00")
        );

        ivaCero.setAplicaIva(true);

        ivaCero.setIvaPorcentaje(
                BigDecimal.ZERO
        );

        try {

            productoService.crear(
                    ivaCero
            );

            throw new RuntimeException(
                    "El Service permitió IVA 0% con aplicaIva=true"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }


        /*
         * =========================================
         * 7. VALIDAR PORCENTAJE NEGATIVO
         * =========================================
         */

        System.out.println();
        System.out.println(
                "7. Probando IVA negativo..."
        );

        Producto ivaNegativo =
                new Producto();

        ivaNegativo.setEmpresaId(1L);
        ivaNegativo.setUnidadMedidaId(1L);

        ivaNegativo.setSku(
                "TEST-SERVICE-IVA-NEG"
        );

        ivaNegativo.setNombre(
                "Producto IVA negativo"
        );

        ivaNegativo.setPrecioCompra(
                new BigDecimal("10000.00")
        );

        ivaNegativo.setPrecioVenta(
                new BigDecimal("20000.00")
        );

        ivaNegativo.setAplicaIva(true);

        ivaNegativo.setIvaPorcentaje(
                new BigDecimal("-5.00")
        );

        try {

            productoService.crear(
                    ivaNegativo
            );

            throw new RuntimeException(
                    "El Service permitió IVA negativo"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }


        /*
         * =========================================
         * 8. VALIDAR IVA MAYOR A 100
         * =========================================
         */

        System.out.println();
        System.out.println(
                "8. Probando IVA mayor a 100%..."
        );

        Producto ivaMayor =
                new Producto();

        ivaMayor.setEmpresaId(1L);
        ivaMayor.setUnidadMedidaId(1L);

        ivaMayor.setSku(
                "TEST-SERVICE-IVA-MAYOR"
        );

        ivaMayor.setNombre(
                "Producto IVA mayor"
        );

        ivaMayor.setPrecioCompra(
                new BigDecimal("10000.00")
        );

        ivaMayor.setPrecioVenta(
                new BigDecimal("20000.00")
        );

        ivaMayor.setAplicaIva(true);

        ivaMayor.setIvaPorcentaje(
                new BigDecimal("101.00")
        );

        try {

            productoService.crear(
                    ivaMayor
            );

            throw new RuntimeException(
                    "El Service permitió IVA mayor a 100%"
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "✅ Validación funcionando"
            );

            System.out.println(
                    "Mensaje: "
                            + e.getMessage()
            );
        }


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
                "   PRUEBA PRODUCTO SERVICE EXITOSA"
        );

        System.out.println(
                "================================="
        );

        System.out.println(
                "Producto IVA ID: "
                        + productoIVAId
        );

        System.out.println(
                "Producto sin IVA ID: "
                        + productoSinIVAId
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
