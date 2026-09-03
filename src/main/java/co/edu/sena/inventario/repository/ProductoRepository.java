package co.edu.sena.inventario.repository;

import co.edu.sena.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Consultas derivadas para el Nivel 10
    List<Producto> findByCategoria(String categoria);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCantidadLessThan(Integer cantidad);
}