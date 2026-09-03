package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.dto.ResumenPedidosDTO;
import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.Prioridad;
import co.edu.sena.inventario.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.getTodosPedidos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.buscarPorId(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
        try {
            Pedido creado = pedidoService.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.confirmarPedido(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/confirmar-parcial")
    public ResponseEntity<?> confirmarParcial(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.confirmarParcial(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.cancelarPedido(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/despachar")
    public ResponseEntity<?> despacharPedido(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.despacharPedido(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping({ "/{id}/reabastecer", "/{id}/completar-stock" })
    public ResponseEntity<?> completarPorReabastecimiento(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.completarPorReabastecimiento(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<List<Pedido>> getPorEstado(@RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.buscarPorEstado(estado));
    }

    @GetMapping("/prioridad")
    public ResponseEntity<List<Pedido>> getPorPrioridad(@RequestParam Prioridad prioridad) {
        return ResponseEntity.ok(pedidoService.buscarPorPrioridad(prioridad));
    }

    @GetMapping("/cliente")
    public ResponseEntity<List<Pedido>> getPorCliente(@RequestParam String cliente) {
        return ResponseEntity.ok(pedidoService.buscarPorCliente(cliente));
    }

    @GetMapping("/urgentes")
    public ResponseEntity<List<Pedido>> getUrgentes() {
        return ResponseEntity.ok(pedidoService.buscarUrgentes());
    }

    @GetMapping("/en-riesgo")
    public ResponseEntity<List<Pedido>> getEnRiesgo() {
        return ResponseEntity.ok(pedidoService.buscarEnRiesgo());
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenPedidosDTO> getResumen() {
        return ResponseEntity.ok(pedidoService.obtenerResumen());
    }

    @GetMapping("/siguiente")
    public ResponseEntity<?> getSiguiente() {
        try {
            return ResponseEntity.ok(pedidoService.obtenerSiguiente());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}