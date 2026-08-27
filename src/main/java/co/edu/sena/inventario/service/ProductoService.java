package co.edu.sena.inventario.service;

import co.edu.sena.inventario.model.Producto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductoService {

    private final List<Producto> productos = new ArrayList<>(List.of(
        new Producto(1L, "Papa pastusa", "Tubérculos", 2500.0, 50),
        new Producto(2L, "Tomate", "Verduras", 3200.0, 30),
        new Producto(3L, "Fresa", "Frutas", 8500.0, 20),
        new Producto(4L, "Zanahoria", "Verduras", 2800.0, 9),
        new Producto(5L, "Cebolla", "Verduras", 3000.0, 35)
    ));

    public List<Producto> getTodos() {
        return productos;
    }

    public Producto buscarPorId(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + id));
    }

    public void descontarStock(Long id, int cantidad) {
        Producto p = buscarPorId(id);
        if (p.getCantidad() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + p.getNombre());
        }
        p.setCantidad(p.getCantidad() - cantidad);
    }

    public void reponerStock(Long id, int cantidad) {
        Producto p = buscarPorId(id);
        p.setCantidad(p.getCantidad() + cantidad);
    }
}