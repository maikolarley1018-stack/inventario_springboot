package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.dto.ResumenPedidosDTO;
import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.service.PedidoConsultaService;
import co.edu.sena.inventario.service.PedidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/pedidos")
public class PedidoConsultaController {

    @Autowired
    private PedidoConsultaService consultaService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/pendientes")
    public ResponseEntity<List<Pedido>> getPendientes() {
        return ResponseEntity.ok(consultaService.obtenerPendientes(pedidoService.getTodosPedidos()));
    }

    @GetMapping("/urgentes")
    public ResponseEntity<List<Pedido>> obtenerUrgentes() {
        return ResponseEntity.ok(consultaService.obtenerUrgentes(pedidoService.getTodosPedidos()));
    }

    @GetMapping("/estado")
    public ResponseEntity<List<Pedido>> getPorEstado(@RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(consultaService.obtenerPorEstado(pedidoService.getTodosPedidos(), estado));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenPedidosDTO> getResumen() {
        return ResponseEntity.ok(consultaService.obtenerResumen(pedidoService.getTodosPedidos()));
    }

    @GetMapping("/siguiente")
    public ResponseEntity<?> getSiguiente() {
        try {
            return ResponseEntity.ok(consultaService.obtenerSiguiente(pedidoService.getTodosPedidos()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/en-riesgo")
    public ResponseEntity<List<Pedido>> getEnRiesgo() {
        return ResponseEntity.ok(consultaService.obtenerEnRiesgo(pedidoService.getTodosPedidos()));
    }
}