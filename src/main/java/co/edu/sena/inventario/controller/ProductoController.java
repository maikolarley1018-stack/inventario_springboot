package co.edu.sena.inventario.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import co.edu.sena.inventario.model.Producto;

@RestController
public class ProductoController {

    private final List<Producto> productos = List.of(
        new Producto(1L, "Papa pastusa", 2500.0, 50),
        new Producto(2L, "Tomate", 3200.0, 30),
        new Producto(3L, "Fresa", 8500.0, 20)
    );

    @GetMapping("/productos")
    public List<Producto> listarProductos() {
        return productos;
    }

    @GetMapping("/productos/{id}")
    public Producto buscarProducto(@PathVariable Long id) {

    for (Producto producto : productos) {

        if (producto.getId().equals(id)) {
            return producto;
        }
    }

    return null;
}
}