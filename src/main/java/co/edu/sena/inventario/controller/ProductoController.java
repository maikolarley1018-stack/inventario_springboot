package co.edu.sena.inventario.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.inventario.model.Producto;

@RestController
public class ProductoController {

    private final List<Producto> productos = new ArrayList<>(List.of(
        new Producto(1L, "Papa pastusa", 2500.0, 50),
        new Producto(2L, "Tomate", 3200.0, 30),
        new Producto(3L, "Fresa", 8500.0, 20),
        new Producto(4L, "Zanahoria", 2800.0, 40),
        new Producto(5L, "Cebolla", 3000.0, 35)
    ));

    private Long siguienteId = 6L;


    // GET - Obtener todos los productos
    @GetMapping("/productos")
    public List<Producto> listarProductos() {
        return productos;
    }


    // GET - Obtener un producto por ID
    @GetMapping("/productos/{id}")
    public Producto buscarProducto(@PathVariable Long id) {

        for (Producto producto : productos) {

            if (producto.getId().equals(id)) {
                return producto;
            }
        }

        return null;
    }


    // POST - Crear un producto
    @PostMapping("/productos")
    public Producto crearProducto(@RequestBody Producto producto) {

        Producto nuevoProducto = new Producto(
            siguienteId,
            producto.getNombre(),
            producto.getPrecio(),
            producto.getCantidad()
        );

        productos.add(nuevoProducto);
        siguienteId++;

        return nuevoProducto;
    }


    // PUT - Actualizar un producto
    @PutMapping("/productos/{id}")
    public Producto actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto datosActualizados) {

        for (Producto producto : productos) {

            if (producto.getId().equals(id)) {

                producto.setNombre(datosActualizados.getNombre());
                producto.setPrecio(datosActualizados.getPrecio());
                producto.setCantidad(datosActualizados.getCantidad());

                return producto;
            }
        }

        return null;
    }


    // DELETE - Eliminar un producto
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
}