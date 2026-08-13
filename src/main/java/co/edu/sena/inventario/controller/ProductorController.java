package co.edu.sena.inventario.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.inventario.model.Productor;

@RestController
public class ProductorController {
    private final List<Productor> productores = List.of(
        new Productor(1L, "Finca El Porvenir", "Mosquera"),
        new Productor(2L, "AgroSabana", "Funza"),
        new Productor(3L, "Productos La Esperanza", "Madrid")
    );
    @GetMapping("/productores")
    public List<Productor> listarProductores() {
        return productores;
    }
    @GetMapping("/productores/{id}")
    public Productor buscarProductor(@PathVariable Long id) {
        for (Productor productor : productores) {
            if (productor.getId().equals(id)) {
                return productor;
            }
        }
        return null;
    }
}