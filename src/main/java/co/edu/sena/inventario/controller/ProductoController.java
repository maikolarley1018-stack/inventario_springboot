package co.edu.sena.inventario.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.inventario.model.Producto;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    // Lista de productos en memoria
    private final List<Producto> productos = new ArrayList<>(List.of(
        new Producto(1L, "Papa pastusa", "Tuberculos", 2500.0, 50),
        new Producto(2L, "Tomate", "Verduras", 3200.0, 30),
        new Producto(3L, "Fresa", "Frutas", 8500.0, 20),
        new Producto(4L, "Zanahoria", "Verduras", 2800.0, 40),
        new Producto(5L, "Cebolla", "Verduras", 3000.0, 35)
    ));

    private Long siguienteId = 6L;

    // =========================================================
    // GET - LISTAR PRODUCTOS Y FILTRAR POR NOMBRE / CATEGORIA
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria) {

        List<Producto> resultado = new ArrayList<>(productos);

        // Reto 2: Búsqueda por nombre
        if (nombre != null && !nombre.isBlank()) {
            resultado.removeIf(p -> !p.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }

        // Reto 3: Filtro por categoría
        if (categoria != null && !categoria.isBlank()) {
            resultado.removeIf(p -> !p.getCategoria().equalsIgnoreCase(categoria));
        }

        return ResponseEntity.ok(resultado);
    }

    // =========================================================
    // RETO 2: BUSCAR PRODUCTOS POR NOMBRE (/productos/buscar)
    // =========================================================
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        List<Producto> resultado = productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    // =========================================================
    // RETO 3: FILTRAR POR CATEGORÍA (/productos/categoria)
    // =========================================================
    @GetMapping("/categoria")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@RequestParam String nombre) {
        List<Producto> resultado = productos.stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(nombre))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    // =========================================================
    // RETO 4: FILTRAR POR PRECIO MÁXIMO (/productos/precio)
    // =========================================================
    @GetMapping("/precio")
    public ResponseEntity<List<Producto>> filtrarPorPrecioMaximo(@RequestParam double maximo) {
        List<Producto> resultados = productos.stream()
                .filter(p -> p.getPrecio() <= maximo)
                .toList();
        return ResponseEntity.ok(resultados);
    }

    // =========================================================
    // RETO 8: CONSULTA DE STOCK BAJO (< 10) (/productos/stock-bajo)
    // =========================================================
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> obtenerStockBajo() {
        List<Producto> resultados = productos.stream()
                .filter(p -> p.getCantidad() < 10)
                .toList();
        return ResponseEntity.ok(resultados);
    }

    // =========================================================
    // RETO 9: RESUMEN DEL INVENTARIO (/productos/resumen)
    // =========================================================
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumen() {
        if (productos.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "El inventario está vacío"));
        }

        long totalProductos = productos.size();
        long stockBajo = productos.stream().filter(p -> p.getCantidad() < 10).count();
        
        Producto masCostoso = productos.stream()
                .max(Comparator.comparing(Producto::getPrecio))
                .orElse(null);
                
        Producto masEconomico = productos.stream()
                .min(Comparator.comparing(Producto::getPrecio))
                .orElse(null);

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalProductos", totalProductos);
        resumen.put("productosStockBajo", stockBajo);
        resumen.put("productoMasCostoso", masCostoso != null ? masCostoso.getNombre() : "N/A");
        resumen.put("productoMasEconomico", masEconomico != null ? masEconomico.getNombre() : "N/A");

        return ResponseEntity.ok(resumen);
    }

    // =========================================================
    // RETO 15 (RETO FINAL): FILTRO COMBINADO (/productos/filtrar)
    // =========================================================
    @GetMapping("/filtrar")
    public ResponseEntity<List<Producto>> filtrarCombinado(
            @RequestParam String categoria,
            @RequestParam double precioMaximo) {

        List<Producto> resultados = productos.stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria) && p.getPrecio() <= precioMaximo)
                .toList();

        return ResponseEntity.ok(resultados);
    }

    // =========================================================
    // RETO 6: GET - BUSCAR POR ID (RESPONDE 404 SI NO EXISTE)
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarProducto(@PathVariable Long id) {
        Optional<Producto> productoOpt = productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        return productoOpt.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build()); // 404 Not Found
    }

    // =========================================================
    // RETO 5 Y 7: POST - CREAR PRODUCTO (VALIDADO + 201 CREATED)
    // =========================================================
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        // Reto 5: Impedir datos incorrectos
        if (esInvalido(producto)) {
            return ResponseEntity.badRequest().body("Datos del producto inválidos. Verifique nombre, categoría, precio (>0) y cantidad (>=0)."); // 400 Bad Request
        }

        Producto nuevoProducto = new Producto(
            siguienteId++,
            producto.getNombre(),
            producto.getCategoria(),
            producto.getPrecio(),
            producto.getCantidad()
        );

        productos.add(nuevoProducto);

        // Reto 7: Responder 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // =========================================================
    // RETO 5 Y 6: PUT - ACTUALIZAR PRODUCTO (VALIDADO)
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto datosActualizados) {

        // Reto 5: Validar campos
        if (esInvalido(datosActualizados)) {
            return ResponseEntity.badRequest().body("Datos inválidos para actualización."); // 400 Bad Request
        }

        for (Producto producto : productos) {
            if (producto.getId().equals(id)) {
                producto.setNombre(datosActualizados.getNombre());
                producto.setCategoria(datosActualizados.getCategoria());
                producto.setPrecio(datosActualizados.getPrecio());
                producto.setCantidad(datosActualizados.getCantidad());

                return ResponseEntity.ok(producto);
            }
        }

        return ResponseEntity.notFound().build(); // 404 Not Found si no existe
    }

    // =========================================================
    // DELETE - ELIMINAR PRODUCTO (RESPONDE 404 SI NO EXISTE)
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Long id) {
        boolean eliminado = productos.removeIf(p -> p.getId().equals(id));

        if (eliminado) {
            return ResponseEntity.ok("Producto eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado"); // 404 Not Found[cite: 1]
        }
    }

    // =========================================================
    // PUT - VENDER PRODUCTOS / DESCONTAR INVENTARIO
    // =========================================================
    @PutMapping("/{id}/vender/{cantidad}")
    public ResponseEntity<?> venderProducto(
            @PathVariable Long id,
            @PathVariable Integer cantidad) {

        if (cantidad <= 0) {
            return ResponseEntity.badRequest().body("La cantidad a vender debe ser mayor a cero");
        }

        for (Producto producto : productos) {
            if (producto.getId().equals(id)) {
                if (cantidad > producto.getCantidad()) {
                    return ResponseEntity.badRequest().body("Stock insuficiente para realizar la venta");
                }

                producto.setCantidad(producto.getCantidad() - cantidad);
                return ResponseEntity.ok(producto);
            }
        }

        return ResponseEntity.notFound().build();
    }

    // Método auxiliar para validar los campos requeridos en el Reto 5[cite: 1]
    private boolean esInvalido(Producto p) {
        return p.getNombre() == null || p.getNombre().isBlank() ||
               p.getCategoria() == null || p.getCategoria().isBlank() ||
               p.getPrecio() == null || p.getPrecio() <= 0 ||
               p.getCantidad() == null || p.getCantidad() < 0;
    }
}