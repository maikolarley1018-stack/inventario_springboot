package co.edu.sena.inventario.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.inventario.model.Producto;

@RestController
public class ProductoController {

    // Lista de productos
    private final List<Producto> productos = new ArrayList<>(List.of(
        new Producto(1L, "Papa pastusa", "Tuberculos", 2500.0, 50),
        new Producto(2L, "Tomate", "Verduras", 3200.0, 30),
        new Producto(3L, "Fresa", "Frutas", 8500.0, 20),
        new Producto(4L, "Zanahoria", "Verduras", 2800.0, 40),
        new Producto(5L, "Cebolla", "Verduras", 3000.0, 35)
    ));

    // ID para el siguiente producto
    private Long siguienteId = 6L;


    // =========================================================
    // GET - LISTAR PRODUCTOS Y FILTRAR POR NOMBRE/CATEGORIA
    // =========================================================

    @GetMapping("/productos")
    public List<Producto> listarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria) {

        List<Producto> resultado = new ArrayList<>(productos);

        // Filtro por nombre
        if (nombre != null && !nombre.isEmpty()) {
            resultado.removeIf(producto ->
                !producto.getNombre()
                        .toLowerCase()
                        .contains(nombre.toLowerCase())
            );
        }

        // Filtro por categoria
        if (categoria != null && !categoria.isEmpty()) {
            resultado.removeIf(producto ->
                !producto.getCategoria()
                        .equalsIgnoreCase(categoria)
            );
        }

        return resultado;
    }


    // =========================================================
    // GET - BUSCAR PRODUCTO POR ID
    // =========================================================

    @GetMapping("/productos/{id}")
    public Producto buscarProducto(@PathVariable Long id) {

        for (Producto producto : productos) {

            if (producto.getId().equals(id)) {
                return producto;
            }
        }

        return null;
    }


    // =========================================================
    // POST - CREAR PRODUCTO
    // =========================================================

    @PostMapping("/productos")
    public Producto crearProducto(@RequestBody Producto producto) {

        Producto nuevoProducto = new Producto(
            siguienteId,
            producto.getNombre(),
            producto.getCategoria(),
            producto.getPrecio(),
            producto.getCantidad()
        );

        productos.add(nuevoProducto);

        siguienteId++;

        return nuevoProducto;
    }


    // =========================================================
    // PUT - ACTUALIZAR PRODUCTO
    // =========================================================

    @PutMapping("/productos/{id}")
    public Producto actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto datosActualizados) {

        for (Producto producto : productos) {

            if (producto.getId().equals(id)) {

                producto.setNombre(datosActualizados.getNombre());
                producto.setCategoria(datosActualizados.getCategoria());
                producto.setPrecio(datosActualizados.getPrecio());
                producto.setCantidad(datosActualizados.getCantidad());

                return producto;
            }
        }

        return null;
    }


    // =========================================================
    // DELETE - ELIMINAR PRODUCTO
    // =========================================================

    @DeleteMapping("/productos/{id}")
    public String eliminarProducto(@PathVariable Long id) {

        for (Producto producto : productos) {

            if (producto.getId().equals(id)) {

                productos.remove(producto);

                return "Producto eliminado correctamente";
            }
        }

        return "Producto no encontrado";
    }


    // =========================================================
    // PUT - VENDER PRODUCTOS / DESCONTAR INVENTARIO
    // =========================================================

    @PutMapping("/productos/{id}/vender/{cantidad}")
    public Producto venderProducto(
            @PathVariable Long id,
            @PathVariable Integer cantidad) {

        for (Producto producto : productos) {

            if (producto.getId().equals(id)) {

                // Verificar que la cantidad sea mayor que cero
                if (cantidad <= 0) {
                    return null;
                }

                // Verificar que haya suficiente inventario
                if (cantidad > producto.getCantidad()) {
                    return null;
                }

                // Operacion matematica
                int nuevaCantidad = producto.getCantidad() - cantidad;

                // Actualizar inventario
                producto.setCantidad(nuevaCantidad);

                return producto;
            }
        }

        return null;
    }
}