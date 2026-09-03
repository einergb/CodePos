# CodePOS

Documentación técnica del proyecto basada en la implementación actual del código fuente y en los tests presentes en el repositorio.

## 1. Visión general

CodePOS es un proyecto Java Maven orientado a la capa de negocio y acceso a datos de un sistema de punto de venta. La infraestructura observada en el código consiste en:

- Java 21 con Maven
- PostgreSQL como motor de persistencia
- JDBC nativo mediante DriverManager
- Arquitectura por capas: model / dao / service / util / dto / config
- Validaciones de negocio en services
- Transacciones manuales para operaciones críticas
- Suite de pruebas ejecutables por consola a través de `main()`

El proyecto no incorpora Spring, JPA, Hibernate ni un framework web. La persistencia se resuelve directamente con `java.sql.Connection`, `PreparedStatement`, `ResultSet`, `CallableStatement` y transacciones controladas por la aplicación.

## 2. Stack y dependencias

La configuración del proyecto se define en [pom.xml](pom.xml):

- `groupId`: `com.codepos`
- `artifactId`: `codepos`
- `version`: `1.0-SNAPSHOT`
- `maven.compiler.release`: `21`
- Dependencia principal:
  - `org.postgresql:postgresql:42.7.7`

Esto confirma que la capa de persistencia usa el driver oficial PostgreSQL JDBC para conectarse a una base PostgreSQL.

## 3. Conexión JDBC

La conexión central está en [src/main/java/com/codepos/config/ConexionBD.java](src/main/java/com/codepos/config/ConexionBD.java).

### 3.1. Variables de entorno

La clase lee las siguientes variables del entorno:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

La URL construida es:

```java
"jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE
```

Y la conexión se realiza con:

```java
DriverManager.getConnection(URL, USER, PASSWORD)
```

### 3.2. Contracto de la clase

`ConexionBD` no es un DataSource ni un pool de conexiones; es una fachada mínima para crear una conexión por cada operación. El método público relevante es:

```java
public static Connection conectar() throws SQLException
```

La clase usa un constructor privado para evitar instanciación.

## 4. Estructura del proyecto

La organización actual del repositorio es:

```text
CodePOS/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── codepos/
│   │   │           ├── config/
│   │   │           │   ├── ConexionBD.java
│   │   │           │   └── TestConexion.java
│   │   │           ├── dao/
│   │   │           │   ├── ClienteDAO.java
│   │   │           │   ├── CompraDAO.java
│   │   │           │   ├── CompraDetalleDAO.java
│   │   │           │   ├── InventarioDAO.java
│   │   │           │   ├── MovimientoInventarioDAO.java
│   │   │           │   ├── PagoDAO.java
│   │   │           │   ├── ProductoDAO.java
│   │   │           │   ├── ProveedorDAO.java
│   │   │           │   ├── VentaDAO.java
│   │   │           │   └── VentaDetalleDAO.java
│   │   │           ├── dto/
│   │   │           │   ├── ResultadoDetalle.java
│   │   │           │   └── ResultadoVenta.java
│   │   │           ├── model/
│   │   │           │   ├── Cliente.java
│   │   │           │   ├── Compra.java
│   │   │           │   ├── CompraDetalle.java
│   │   │           │   ├── Inventario.java
│   │   │           │   ├── MovimientoInventario.java
│   │   │           │   ├── Pago.java
│   │   │           │   ├── Producto.java
│   │   │           │   ├── Proveedor.java
│   │   │           │   ├── Venta.java
│   │   │           │   └── VentaDetalle.java
│   │   │           ├── service/
│   │   │           │   ├── ClienteService.java
│   │   │           │   ├── CompraDetalleService.java
│   │   │           │   ├── CompraService.java
│   │   │           │   ├── InventarioService.java
│   │   │           │   ├── PagoService.java
│   │   │           │   ├── ProductoService.java
│   │   │   │           │   ├── ProveedorService.java
│   │   │           │   ├── VentaDetalleService.java
│   │   │           │   ├── VentaIntegralService.java
│   │   │           │   └── VentaService.java
│   │   │           ├── util/
│   │   │           │   └── CalculadoraVentaUtil.java
│   │   │           ├── CalculadoraVentaUtilTest.java
│   │   │           ├── TestClienteDAO.java
│   │   │           ├── TestClienteService.java
│   │   │           ├── TestCompra.java
│   │   │           ├── TestCompraDetalle.java
│   │   │           ├── TestCompraDetalleService.java
│   │   │           ├── TestCompraService.java
│   │   │           ├── TestInventario.java
│   │   │           ├── TestInventarioService.java
│   │   │           ├── TestMovimientoInventario.java
│   │   │           ├── TestPagoDAO.java
│   │   │           ├── TestPagoService.java
│   │   │           ├── TestProductoDAO.java
│   │   │           ├── TestProductoService.java
│   │   │           ├── TestProveedor.java
│   │   │           ├── TestProveedorService.java
│   │   │           ├── TestVentaDAO.java
│   │   │           ├── TestVentaDetalleDAO.java
│   │   │           ├── TestVentaDetalleService.java
│   │   │           ├── TestVentaIntegralService.java
│   │   │           └── TestVentaService.java
│   │   │           └── servlet/   (directorio presente, sin clases visibles en la estructura actual)
│   │   └── resources/
│   └── test/
│       └── java/
└── target/
```

## 5. Infraestructura funcional

### 5.1. Capa model

Los modelos son POJOs Java con atributos, getters/setters y sin lógica de negocio. La infraestructura actual mapea principalmente entidades persistidas en PostgreSQL.

#### Entidades principales

- [src/main/java/com/codepos/model/Cliente.java](src/main/java/com/codepos/model/Cliente.java)
  - `id`, `empresaId`, `nombre`, `identificacion`, `telefono`, `correo`, `direccion`, `activo`, `createdAt`, `updatedAt`

- [src/main/java/com/codepos/model/Proveedor.java](src/main/java/com/codepos/model/Proveedor.java)
  - `id`, `empresaId`, `nombre`, `identificacion`, `telefono`, `correo`, `direccion`, `activo`, timestamps

- [src/main/java/com/codepos/model/Producto.java](src/main/java/com/codepos/model/Producto.java)
  - `id`, `empresaId`, `categoriaId`, `marcaId`, `unidadMedidaId`, `sku`, `codigoBarras`, `nombre`, `descripcion`, `precioCompra`, `precioVenta`, `aplicaIva`, `ivaPorcentaje`, `activo`, timestamps

- [src/main/java/com/codepos/model/Venta.java](src/main/java/com/codepos/model/Venta.java)
  - `id`, `empresaId`, `sucursalId`, `clienteId`, `authUserId`, `numero`, `fecha`, `estado`, `subtotal`, `descuento`, `impuesto`, `total`, `observaciones`, timestamps

- [src/main/java/com/codepos/model/VentaDetalle.java](src/main/java/com/codepos/model/VentaDetalle.java)
  - `id`, `ventaId`, `productoId`, `cantidad`, `precioVenta`, `descuento`, `impuesto`, `subtotal`, timestamps

- [src/main/java/com/codepos/model/Compra.java](src/main/java/com/codepos/model/Compra.java)
  - `id`, `empresaId`, `sucursalId`, `proveedorId`, `authUserId`, `numero`, `fecha`, `estado`, `subtotal`, `descuento`, `impuesto`, `total`, `observaciones`, timestamps

- [src/main/java/com/codepos/model/CompraDetalle.java](src/main/java/com/codepos/model/CompraDetalle.java)
  - `id`, `compraId`, `productoId`, `cantidad`, `precioCompra`, `descuento`, `impuesto`, `subtotal`, timestamps

- [src/main/java/com/codepos/model/Inventario.java](src/main/java/com/codepos/model/Inventario.java)
  - `id`, `empresaId`, `sucursalId`, `productoId`, `cantidad`, `stockMinimo`, `stockMaximo`, `activo`, timestamps

- [src/main/java/com/codepos/model/MovimientoInventario.java](src/main/java/com/codepos/model/MovimientoInventario.java)
  - `id`, `empresaId`, `sucursalId`, `productoId`, `tipo`, `cantidad`, `stockAnterior`, `stockPosterior`, `motivo`, `referenciaTipo`, `referenciaId`, `authUserId`, `fecha`, timestamps

- [src/main/java/com/codepos/model/Pago.java](src/main/java/com/codepos/model/Pago.java)
  - `id`, `ventaId`, `authUserId`, `metodo`, `monto`, `referencia`, `fecha`, timestamps

### 5.2. Capa DTO

La capa de transferencia de datos usa objetos inmutables para transportar resultados de cálculo:

- [src/main/java/com/codepos/dto/ResultadoDetalle.java](src/main/java/com/codepos/dto/ResultadoDetalle.java)
- [src/main/java/com/codepos/dto/ResultadoVenta.java](src/main/java/com/codepos/dto/ResultadoVenta.java)

Ambas clases encapsulan los campos:

- `subtotal`
- `descuento`
- `impuesto`
- `total`

No incluyen lógica de negocio ni acceso a base de datos.

### 5.3. Utilidad de cálculo

La lógica monetaria central está en [src/main/java/com/codepos/util/CalculadoraVentaUtil.java](src/main/java/com/codepos/util/CalculadoraVentaUtil.java).

#### Responsabilidades detectadas

- Calcular detalle individual
- Calcular venta completa
- Calcular subtotal bruto por detalle
- Calcular descuento total
- Calcular impuesto total
- Calcular total final
- Redondeo monetario con `BigDecimal` y `RoundingMode.HALF_UP`
- Validar lista de detalles
- Validar que no existan negativos en montos
- Validar que una cantidad sea positiva
- Validar que el descuento no supere el subtotal

#### Fórmulas implementadas

- Detalle:

```java
subtotal = cantidad × precioVenta

total = subtotal - descuento + impuesto
```

- Venta:

```java
subtotal = suma de subtotales de los detalles

descuento = suma de descuentos de los detalles

impuesto = suma de impuestos de los detalles

total = subtotal - descuento + impuesto
```

La clase usa una escala fija:

```java
private static final int ESCALA_MONETARIA = 2;
```

Y una constante cero monetaria:

```java
private static final BigDecimal CERO = BigDecimal.ZERO.setScale(...)
```

## 6. Capa DAO

La capa DAO encapsula consultas SQL y mapeo a modelos con JDBC. Los DAOs principales son:

### 6.1. ClienteDAO

Archivo: [src/main/java/com/codepos/dao/ClienteDAO.java](src/main/java/com/codepos/dao/ClienteDAO.java)

Métodos observados:

- `buscarPorId(Long empresaId, Long clienteId)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Cliente cliente)`
- `mapearCliente(ResultSet rs)`

SQL observada:

- `SELECT ... FROM clientes WHERE empresa_id = ? AND id = ?`
- `SELECT ... FROM clientes WHERE empresa_id = ? ORDER BY id`
- `INSERT INTO clientes (...) VALUES (...) RETURNING id`

### 6.2. ProveedorDAO

Archivo: [src/main/java/com/codepos/dao/ProveedorDAO.java](src/main/java/com/codepos/dao/ProveedorDAO.java)

Métodos observados:

- `buscarPorId(Long empresaId, Long proveedorId)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Proveedor proveedor)`
- `mapearProveedor(ResultSet rs)`

### 6.3. ProductoDAO

Archivo: [src/main/java/com/codepos/dao/ProductoDAO.java](src/main/java/com/codepos/dao/ProductoDAO.java)

Métodos observados:

- `buscarPorId(Long empresaId, Long productoId)`
- `buscarPorSku(Long empresaId, String sku)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Producto producto)`
- `mapearProducto(ResultSet rs)`

Este DAO maneja campos como:

- `categoria_id`
- `marca_id`
- `unidad_medida_id`
- `sku`
- `codigo_barras`
- `precio_compra`
- `precio_venta`
- `aplica_iva`
- `iva_porcentaje`
- `activo`

### 6.4. VentaDAO

Archivo: [src/main/java/com/codepos/dao/VentaDAO.java](src/main/java/com/codepos/dao/VentaDAO.java)

Métodos observados:

- `buscarPorId(Long empresaId, Long ventaId)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Venta venta)`
- `crear(Connection conexion, Venta venta)`
- `mapearVenta(ResultSet rs)`

Importante: existe una sobrecarga que acepta `Connection`, lo que permite participar en una transacción abierta por el servicio. Esta es la base de la operación integral.

### 6.5. VentaDetalleDAO

Archivo: [src/main/java/com/codepos/dao/VentaDetalleDAO.java](src/main/java/com/codepos/dao/VentaDetalleDAO.java)

Métodos observados:

- `buscarPorId(Long detalleId)`
- `listarPorVenta(Long ventaId)`
- `crear(VentaDetalle detalle)`
- `crear(Connection conexion, VentaDetalle detalle)`
- `mapearVentaDetalle(ResultSet rs)`

### 6.6. PagoDAO

Archivo: [src/main/java/com/codepos/dao/PagoDAO.java](src/main/java/com/codepos/dao/PagoDAO.java)

Métodos observados:

- `buscarPorId(Long pagoId)`
- `listarPorVenta(Long ventaId)`
- `crear(Pago pago)`
- `crear(Connection connection, Pago pago)`
- `mapearPago(ResultSet rs)`

### 6.7. CompraDAO

Archivo: [src/main/java/com/codepos/dao/CompraDAO.java](src/main/java/com/codepos/dao/CompraDAO.java)

Métodos observados:

- `buscarPorId(Long empresaId, Long compraId)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Compra compra)`
- `mapearCompra(ResultSet rs)`

### 6.8. CompraDetalleDAO

Archivo: [src/main/java/com/codepos/dao/CompraDetalleDAO.java](src/main/java/com/codepos/dao/CompraDetalleDAO.java)

Métodos observados:

- `buscarPorId(Long detalleId)`
- `listarPorCompra(Long compraId)`
- `crear(CompraDetalle detalle)`
- `mapearDetalle(ResultSet rs)`

### 6.9. InventarioDAO

Archivo: [src/main/java/com/codepos/dao/InventarioDAO.java](src/main/java/com/codepos/dao/InventarioDAO.java)

Métodos observados:

- `buscarPorProducto(Long empresaId, Long sucursalId, Long productoId)`
- `buscarPorProducto(Connection connection, Long empresaId, Long sucursalId, Long productoId)`
- `descontarStock(Connection connection, Long inventarioId, BigDecimal cantidad)`
- `mapearInventario(ResultSet resultSet)`

Patrón relevante: la sobrecarga con `Connection` usa `FOR UPDATE` en SQL:

```sql
SELECT ... FROM inventarios
WHERE empresa_id = ?
  AND sucursal_id = ?
  AND producto_id = ?
FOR UPDATE
```

Esto bloquea la fila para evitar condiciones de carrera en la actualización del stock.

### 6.10. MovimientoInventarioDAO

Archivo: [src/main/java/com/codepos/dao/MovimientoInventarioDAO.java](src/main/java/com/codepos/dao/MovimientoInventarioDAO.java)

Métodos observados:

- `registrarMovimiento(Long empresaId, Long sucursalId, Long productoId, String tipo, BigDecimal cantidad, String motivo, String referenciaTipo, Long referenciaId, Integer authUserId)`
- `registrarMovimiento(Connection connection, Long empresaId, Long sucursalId, Long productoId, String tipo, BigDecimal cantidad, String motivo, String referenciaTipo, Long referenciaId, Integer authUserId)`

Esta clase invoca una función PostgreSQL:

```sql
SELECT registrar_movimiento_inventario(?, ?, ?, ?, ?, ?, ?, ?, ?)
```

La referencia al procedimiento es explícita en el comentario del código; la función devuelve el ID del movimiento generado.

## 7. Capa Service

Los services son el punto central de validación y coordinación. No realizan SQL directo, sino que instancian DAOs y delegan la persistencia.

### 7.1. ClienteService

Archivo: [src/main/java/com/codepos/service/ClienteService.java](src/main/java/com/codepos/service/ClienteService.java)

Métodos:

- `buscarPorId(Long empresaId, Long clienteId)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Cliente cliente)`

Validaciones:

- empresa obligatoria
- clienteId obligatorio
- nombre obligatorio
- campos opcionales no pueden quedar vacíos si se envían

### 7.2. ProveedorService

Archivo: [src/main/java/com/codepos/service/ProveedorService.java](src/main/java/com/codepos/service/ProveedorService.java)

Métodos:

- `consultar(Long empresaId, Long proveedorId)`
- `listar(Long empresaId)`
- `crear(Proveedor proveedor)`

Validaciones:

- empresa válida
- nombre obligatorio con longitud mínima de 2 caracteres
- normalización de campos de texto vacíos a `null`

### 7.3. ProductoService

Archivo: [src/main/java/com/codepos/service/ProductoService.java](src/main/java/com/codepos/service/ProductoService.java)

Métodos:

- `buscarPorId(Long empresaId, Long productoId)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Producto producto)`

Validaciones:

- empresa obligatoria
- `unidadMedidaId` obligatoria
- `sku` obligatorio
- `nombre` obligatorio
- `precioCompra` no negativo
- `precioVenta` no negativo
- `aplicaIva` obligatorio
- `ivaPorcentaje` obligatorio y rango válido: `0..100`
- si `aplicaIva = false`, entonces `ivaPorcentaje` debe ser `0`
- si `aplicaIva = true`, entonces `ivaPorcentaje > 0`

### 7.4. VentaService

Archivo: [src/main/java/com/codepos/service/VentaService.java](src/main/java/com/codepos/service/VentaService.java)

Métodos:

- `buscarPorId(Long empresaId, Long ventaId)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Venta venta)`

Reglas de negocio observadas:

- `empresaId` obligatorio
- `sucursalId` obligatorio
- `numero` obligatorio
- `clienteId` válido si se especifica
- `authUserId` válido si se especifica
- montos `subtotal`, `descuento`, `impuesto`, `total` no negativos
- observaciones vacías se rechazan si se envían como cadena en blanco
- al crear una venta nueva, se fuerza `estado = "REGISTRADA"`

### 7.5. VentaDetalleService

Archivo: [src/main/java/com/codepos/service/VentaDetalleService.java](src/main/java/com/codepos/service/VentaDetalleService.java)

Métodos:

- `buscarPorId(Long detalleId)`
- `listarPorVenta(Long ventaId)`
- `crear(VentaDetalle detalle)`

Validaciones:

- `ventaId` obligatorio
- `productoId` obligatorio
- `cantidad` obligatoria y mayor que cero
- `precioVenta` obligatorio y no negativo
- `descuento` no negativo
- `impuesto` no negativo
- `subtotal` obligatorio y no negativo

### 7.6. PagoService

Archivo: [src/main/java/com/codepos/service/PagoService.java](src/main/java/com/codepos/service/PagoService.java)

Métodos:

- `buscarPorId(Long pagoId)`
- `listarPorVenta(Long ventaId)`
- `crear(Pago pago)`

Validaciones:

- `ventaId` obligatorio
- `metodo` obligatorio
- `monto` obligatorio y mayor que cero
- `referencia` opcional, pero si viene vacía se rechaza

### 7.7. CompraService

Archivo: [src/main/java/com/codepos/service/CompraService.java](src/main/java/com/codepos/service/CompraService.java)

Métodos:

- `consultar(Long empresaId, Long compraId)`
- `listarPorEmpresa(Long empresaId)`
- `crear(Compra compra)`

Validaciones observadas:

- empresa válida
- sucursal válida
- proveedor válido
- número de compra obligatorio
- estado permitido: `REGISTRADA`, `APLICADA`, `ANULADA`
- subtotal, descuento, impuesto y total no negativos
- validación de existencia y estado del proveedor en la empresa

### 7.8. InventarioService

Archivo: [src/main/java/com/codepos/service/InventarioService.java](src/main/java/com/codepos/service/InventarioService.java)

Métodos:

- `consultar(Long empresaId, Long sucursalId, Long productoId)`
- `registrarMovimiento(Long empresaId, Long sucursalId, Long productoId, String tipo, BigDecimal cantidad, String motivo, String referenciaTipo, Long referenciaId, Integer authUserId)`

Reglas detectadas:

- `empresaId`, `sucursalId`, `productoId` obligatorios y positivos
- tipo obligatorio
- cantidad obligatoria y mayor que cero
- motivo obligatorio
- si no existe inventario para el producto, lanza `IllegalArgumentException`
- si el inventario tiene `activo = false`, lanza `IllegalStateException`

### 7.9. VentaIntegralService

Archivo: [src/main/java/com/codepos/service/VentaIntegralService.java](src/main/java/com/codepos/service/VentaIntegralService.java)

Este es el servicio central del proyecto y la pieza más relevante desde el punto de vista operativo.

#### Responsabilidades observadas

1. Validar venta, detalles y pago antes de iniciar la operación
2. Validar productos duplicados
3. Calcular la venta con `CalculadoraVentaUtil.calcularVenta(detalles)`
4. Asignar subtotal, descuento, impuesto y total a la entidad `Venta`
5. Validar que el monto del pago coincida con el total calculado
6. Abrir una conexión a PostgreSQL
7. Activar transacción con `connection.setAutoCommit(false)`
8. Validar y bloquear stock de inventario
9. Crear la venta
10. Crear detalles de venta
11. Registrar movimientos de inventario
12. Registrar el pago
13. Marcar la venta como `PAGADA`
14. Ejecutar `commit`
15. Si falla, ejecutar `rollback`

#### Patrón transaccional

La lógica se ejecuta en una sola conexión, y los DAOs de transacciones reciben una referencia a esa misma `Connection` sin abrir/cerrar la conexión localmente.

Esto es clave para mantener consistencia entre:

- `ventas`
- `venta_detalles`
- `inventarios`
- `movimientos_inventario`
- `pagos`

## 8. Flujo de negocio principal: venta integral

El flujo detallado del proyecto se puede resumir así:

```text
cliente o usuario -> VentaIntegralService
  -> validarVenta()
  -> validarDetalles()
  -> validarPago()
  -> validarProductosDuplicados()
  -> CalculadoraVentaUtil.calcularVenta(detalles)
  -> venta.setSubtotal / setDescuento / setImpuesto / setTotal
  -> validación de monto del pago == total de la venta
  -> Connection connection = ConexionBD.conectar()
  -> connection.setAutoCommit(false)
  -> validarInventarios(connection, venta, detalles)
  -> ventaDAO.crear(connection, venta)
  -> foreach detalle: ventaDetalleDAO.crear(connection, detalle)
  -> foreach detalle: inventarioDAO.descontarStock(connection,...)
  -> movimientoDAO.registrarMovimiento(connection,...)
  -> pagoDAO.crear(connection, pago)
  -> venta.setEstado("PAGADA")
  -> commit()
```

Si ocurre cualquier excepción, el servicio se encarga del rollback.

## 9. Autenticación y separación de responsabilidades

La autenticación no está implementada dentro del core POS. El código evidencia una separación clara entre:

- una API o capa de autenticación externa, responsable de validar credenciales, emitir tokens o sesiones y devolver el usuario autenticado
- el núcleo de negocio del POS, que recibe un usuario ya validado y registra esa identidad como auditoría y trazabilidad

Esto se observa en que los modelos contienen campos como `authUserId` en `Venta`, `Pago`, `Compra` y `MovimientoInventario`, pero no hay en la estructura actual ninguna implementación real de login, JWT, sesiones o filtros de seguridad dentro del repositorio.

### 9.1. `authUserId` como metadata de auditoría

Los servicios y modelos usan `authUserId` como referencia del usuario que ejecutó una operación. En la práctica, esto se interpreta como un campo de auditoría, no como una lógica de autenticación.

Ejemplos observados:

- [src/main/java/com/codepos/model/Venta.java](src/main/java/com/codepos/model/Venta.java)
- [src/main/java/com/codepos/model/Pago.java](src/main/java/com/codepos/model/Pago.java)
- [src/main/java/com/codepos/model/Compra.java](src/main/java/com/codepos/model/Compra.java)
- [src/main/java/com/codepos/service/VentaIntegralService.java](src/main/java/com/codepos/service/VentaIntegralService.java)

### 9.2. Arquitectura esperada

La separación funcional más coherente con el código es:

```text
API Auth
  -> login / validate / token / session
  -> devuelve usuario autenticado y permisos

API POS / Core Business
  -> recibe usuario autenticado
  -> ejecuta ventas, inventario, pagos, compras
  -> persiste authUserId como trazabilidad

JDBC / PostgreSQL
  -> persiste entidades del negocio
```

Esto mantiene el núcleo de negocio ajeno a la validación de credenciales, y deja la autenticación en una capa distinta, tal como se recomienda para sistemas con múltiples servicios.

### 9.3. Qué falta en la estructura actual

En la estructura actual no se observa:

- `LoginController`
- `AuthController`
- `SecurityFilter`
- `JWT` o sesión gestionada internamente
- middleware de autorización
- validación de roles por endpoint o operación

Lo que sí existe es una capa de negocio que asume que el usuario ya fue autenticado y que el identificador del actor llega como dato del contexto de operación.

## 10. Convenciones de validación y manejo de errores

El proyecto no usa frameworks de validación externos; todas las comprobaciones se hacen mediante `IllegalArgumentException` y `IllegalStateException`.

Patrones frecuentes:

- campo nulo → `IllegalArgumentException`
- valor negativo → `IllegalArgumentException`
- entidad inexistente → `IllegalArgumentException`
- estado inválido o inconsistencia de negocio → `IllegalStateException` o `IllegalArgumentException`

La capa DAO, por su parte, encapsula errores JDBC en `RuntimeException` con mensajes como:

- `Error al buscar cliente`
- `Error al crear venta`
- `Error al consultar inventario dentro de la transacción`
- `Error al registrar movimiento de inventario`

## 11. Tests del proyecto

El repositorio contiene una batería de pruebas ejecutadas como programas de consola con `public static void main(String[] args)`. No se observa JUnit ni TestNG en el `pom.xml` ni en la estructura actual.

### 10.1. Test utilitarios y matemáticos

- [src/main/java/com/codepos/CalculadoraVentaUtilTest.java](src/main/java/com/codepos/CalculadoraVentaUtilTest.java)
  - Verifica cálculo de detalle
  - Verifica cálculo con `descuento` e `impuesto` nulos
  - Verifica cálculo de venta completa
  - Verifica redondeo
  - Verifica validaciones de negocio

### 10.2. Tests de conexión y DAO

- [src/main/java/com/codepos/config/TestConexion.java](src/main/java/com/codepos/config/TestConexion.java)
  - Prueba de conexión a la base de datos

- [src/main/java/com/codepos/TestClienteDAO.java](src/main/java/com/codepos/TestClienteDAO.java)
- [src/main/java/com/codepos/TestProveedor.java](src/main/java/com/codepos/TestProveedor.java)
- [src/main/java/com/codepos/TestProductoDAO.java](src/main/java/com/codepos/TestProductoDAO.java)
- [src/main/java/com/codepos/TestVentaDAO.java](src/main/java/com/codepos/TestVentaDAO.java)
- [src/main/java/com/codepos/TestVentaDetalleDAO.java](src/main/java/com/codepos/TestVentaDetalleDAO.java)
- [src/main/java/com/codepos/TestCompra.java](src/main/java/com/codepos/TestCompra.java)
- [src/main/java/com/codepos/TestCompraDetalle.java](src/main/java/com/codepos/TestCompraDetalle.java)
- [src/main/java/com/codepos/TestPagoDAO.java](src/main/java/com/codepos/TestPagoDAO.java)
- [src/main/java/com/codepos/TestInventario.java](src/main/java/com/codepos/TestInventario.java)
- [src/main/java/com/codepos/TestMovimientoInventario.java](src/main/java/com/codepos/TestMovimientoInventario.java)

### 10.3. Tests de servicio

- [src/main/java/com/codepos/TestClienteService.java](src/main/java/com/codepos/TestClienteService.java)
- [src/main/java/com/codepos/TestProveedorService.java](src/main/java/com/codepos/TestProveedorService.java)
- [src/main/java/com/codepos/TestProductoService.java](src/main/java/com/codepos/TestProductoService.java)
- [src/main/java/com/codepos/TestVentaService.java](src/main/java/com/codepos/TestVentaService.java)
- [src/main/java/com/codepos/TestVentaDetalleService.java](src/main/java/com/codepos/TestVentaDetalleService.java)
- [src/main/java/com/codepos/TestCompraService.java](src/main/java/com/codepos/TestCompraService.java)
- [src/main/java/com/codepos/TestCompraDetalleService.java](src/main/java/com/codepos/TestCompraDetalleService.java)
- [src/main/java/com/codepos/TestPagoService.java](src/main/java/com/codepos/TestPagoService.java)
- [src/main/java/com/codepos/TestInventarioService.java](src/main/java/com/codepos/TestInventarioService.java)

### 11.4. Test transaccional integral

Archivo clave: [src/main/java/com/codepos/TestVentaIntegralService.java](src/main/java/com/codepos/TestVentaIntegralService.java)

Este test cubre:

- venta correcta
- pago incorrecto
- stock insuficiente
- producto sin inventario
- inventario inactivo
- validación del estado final `PAGADA`
- cálculo total de venta
- movimiento de inventario
- validación de `stock_anterior` y `stock_posterior`
- rollback ante fallo

La prueba prepara stock en la base de datos antes de ejecutar la operación para garantizar condiciones reproducibles.

## 12. Observaciones de la infraestructura actual

### 12.1. Qué existe

- Persistencia JDBC directa
- Capas claras para entidad, acceso a datos y validación
- Transacciones manuales por servicio
- Cálculo monetario centralizado en una utilidad
- Operaciones reutilizables por conexión
- Identificador del usuario ejecutante en modelos relevantes

### 12.2. Qué no se observa

- No hay Spring Boot
- No hay JPA/Hibernate
- No hay framework web MVC
- No hay `@Transactional`
- No hay tests con JUnit o Mockito
- No hay configuración de `application.properties` ni `application.yml`
- No se observa un patrón de repositorios genéricos ni factories
- No se observa una implementación de autenticación dentro del repositorio

### 12.3. Limitaciones de diseño visibles en el código

- Las conexiones se crean con `DriverManager` para cada DAO individual, y la lógica de transacción depende de la disciplina del service que llama a los DAOs con una conexión compartida.
- Muchos métodos de DAO y service lanzan `RuntimeException` encapsulando errores JDBC, lo que dificulta distinguir errores de negocio de errores de infraestructura.
- El proyecto está muy orientado a pruebas manuales de consola; no hay un runner de pruebas automatizado configurado en Maven.
- Existe un directorio `servlet/` en la estructura, pero no se observa implementación concreta en la revisión actual del código.
- La autenticación no está implementada aquí; la identidad del actor se transporta como dato de auditoría y debe llegar desde otra capa.

## 13. Ejecución del proyecto

El proyecto está estructurado como aplicación Java Maven. La ejecución de pruebas observada es manual desde `main()` en cada clase de prueba, por ejemplo:

```bash
mvn compile
java -cp target/classes:... com.codepos.TestVentaIntegralService
```

Sin embargo, la forma exacta de classpath depende del entorno y de la base de datos PostgreSQL disponible. El proyecto no define un `main` global ni una app REST o GUI; la ejecución real se hace por invocación directa de las clases de prueba o de los services desde una aplicación cliente externa.

## 14. Resumen arquitectónico

La infraestructura del sistema puede describirse así:

```text
API Auth (externa)
   ↓
Usuario autenticado / token / principal
   ↓
Core POS (model + service + DAO + util)
   ↓
PostgreSQL
```

El núcleo del sistema sigue siendo el negocio POS: validaciones, transacciones, inventario, cálculo de venta y movimientos de stock. La autenticación funciona como un servicio externo que valida identidad y entrega el usuario autorizado, mientras que el core usa ese identificador como trazabilidad en cada operación crítica.

## 15. Conclusión

CodePOS muestra una implementación Java 21 + JDBC + PostgreSQL que prioriza claridad de capas, validación local en services y control transaccional manual en el flujo de ventas e inventario. La lógica de negocio no está abstraída en un framework ni en un ORM; está aplicada directamente en servicios y validaciones específicas, con SQL explícito y conexiones gestionadas por la aplicación.

La API de autenticación no está incluida en este repositorio; la evidencia del código sugiere que el sistema espera recibir una identidad ya validada desde un servicio externo y registrar ese valor como `authUserId` en las operaciones del negocio. Esta separación es coherente con una arquitectura de POS desacoplada de la autenticación, y con la intención de mantener el núcleo del sistema independiente del mecanismo de login o token.

## 16. Qué debes arreglar para dejar el proyecto listo

La estructura actual ya tiene la base funcional del negocio, pero presenta varios puntos que conviene corregir para evolucionar de un core técnico a una solución más robusta y mantenible.

### 16.1. Definir el contrato de autenticación externamente

- Debe quedar explícito cómo se recibe el usuario autenticado: token, principal, sesión o identificador del servicio externo.
- El repositorio no debe asumir lógica de login, ni generar JWT, ni manejar sesiones localmente.
- La capa del POS debe aceptar únicamente un `authUserId` o un principal validado por otra API.

### 16.2. Agregar la capa web o de entrada

No hay evidencia de una capa de presentación o controlador real en la estructura actual. El proyecto necesita decidir si usará:

- servlets Java
- un backend REST con endpoints HTTP
- una API de integración para registrar ventas, inventario y pagos

Sin esta capa, el core funciona como biblioteca de negocio, pero no como aplicación operativa completa.

### 16.3. Separar mejor la capa de negocio de la infraestructura

Actualmente el patrón se ve claro, pero aún es mejor reforzarlo con:

- un paquete de `api` o `controller` para entrada de datos
- un paquete `application` o `usecase` para orquestación de casos de uso
- servicios dedicados y más granularizados por operación
- DTOs de entrada y salida estrictamente definidos

Esto evitará mezclar reglas de negocio con detalles de JDBC y SQL.

### 16.4. Estandarizar manejo de errores

El proyecto usa `IllegalArgumentException` y `IllegalStateException` de forma consistente, pero conviene formalizar un enfoque de errores de dominio y de infraestructura:

- errores de validación
- errores de negocio
- errores de persistencia
- errores de integración con API externa

Esto facilita que los controladores o servicios superiores respondan con códigos HTTP y mensajes claros.

### 16.5. Adoptar una estrategia de migración de base de datos

El código usa SQL explícito y probablemente asume una estructura de esquema ya creada. Lo recomendable es:

- definir migraciones con Flyway o Liquibase
- versionar el esquema de PostgreSQL
- documentar la estructura real de tablas y columnas
- mantener consistencia entre modelos, DAOs y schema SQL

### 16.6. Añadir configuración externa real

La conexión se construye leyendo variables de entorno, lo cual es útil, pero falta dejarlo más robusto con:

- `application.properties` o `application.yml` si se añade un framework
- perfiles por entorno (`dev`, `test`, `prod`)
- secret management para `DB_PASSWORD`
- validación de configuración al iniciar la aplicación

### 16.7. Consolidar pruebas automatizadas

Hoy los tests están hechos con `main()` y no con JUnit. Para un proyecto más sano, conviene:

- migrar pruebas a JUnit 5
- separar pruebas unitarias, de servicio e integración
- usar fixtures reales o datos controlados
- validar rollback y transacciones en ambiente controlado

### 16.8. Mejorar trazabilidad y auditoría

`authUserId` ya aparece en varios modelos, lo cual es buena señal. Sin embargo, conviene mantenerlo consistente en todos los flujos:

- compras
- ventas
- pagos
- inventario
- movimientos
- eliminaciones o anulaciones

La auditoría debe quedar documentada y aplicada de forma uniforme en cada operación.

### 16.9. Asegurar consistencia de stock y reglas de negocio

La lógica de inventario está bien orientada, pero debe reforzarse con:

- validación uniforme de stock mínimo y máximo
- reglas para devoluciones y anulaciones
- manejo explícito de productos inactivos
- validación del estado de compra/venta antes de cierre

### 16.10. Definir el límite de responsabilidad de cada servicio

El proyecto ya tiene buena separación, pero aun así conviene revisar si algunos servicios están haciendo demasiado:

- `VentaIntegralService` podría quedar como orquestador de casos de uso
- validaciones de stock podrían quedar en un servicio específico de inventario
- pagos y movimientos podrían encapsularse mejor según su dominio

Esto evita que un servicio crezca demasiado y se vuelva difícil de mantener.

## 17. Conclusión final

CodePOS tiene una base sólida para un sistema POS con lógica de negocio clara, acceso JDBC directo y transacciones manuales bien orientadas. La parte más importante ya está construida: dominio, servicios, DAO, cálculo monetario, inventario y control transaccional.

Lo que falta no es rehacer el proyecto desde cero, sino cerrar la capa de entrada, definir la integración con la autenticación externa, establecer una convención de errores y tests más profesionales, y completar la arquitectura operativa para que pueda ejecutarse como sistema real y no solo como núcleo de negocio.

En otras palabras: el proyecto ya tiene la lógica de negocio y la infraestructura de persistencia; ahora le faltan las capas de exposición, autenticación real, observabilidad y automatización de pruebas para terminar de convertirse en una aplicación completa.


