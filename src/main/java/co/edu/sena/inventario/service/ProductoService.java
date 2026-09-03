package co.edu.sena.inventario.service;

import co.edu.sena.inventario.model.Producto;
import co.edu.sena.inventario.repository.ProductoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Cargar datos iniciales en la BD si esta vacia al iniciar la app
    @PostConstruct
    public void initData() {
        if (productoRepository.count() == 0) {
            productoRepository.saveAll(List.of(
                new Producto(null, "Papa pastusa", "Tubérculos", 2500.0, 50),
                new Producto(null, "Tomate", "Verduras", 3200.0, 30),
                new Producto(null, "Fresa", "Frutas", 8500.0, 20),
                new Producto(null, "Zanahoria", "Verduras", 2800.0, 9),
                new Producto(null, "Cebolla", "Verduras", 3000.0, 35)
            ));
        }
    }

    public List<Producto> getTodos() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + id));
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void descontarStock(Long id, int cantidad) {
        Producto p = buscarPorId(id);
        if (p.getCantidad() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + p.getNombre());
        }
        p.setCantidad(p.getCantidad() - cantidad);
        productoRepository.save(p);
    }

    public void reponerStock(Long id, int cantidad) {
        Producto p = buscarPorId(id);
        p.setCantidad(p.getCantidad() + cantidad);
        productoRepository.save(p);
    }

    // Métodos para exponer las consultas derivadas
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorStockBajo(Integer limite) {
        return productoRepository.findByCantidadLessThan(limite);
    }
}