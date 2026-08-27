package co.edu.sena.inventario.service;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PedidoService {

    private final List<Pedido> pedidos = new ArrayList<>();
    private Long idCounter = 1L;

    public List<Pedido> getTodosPedidos() { 
        return pedidos; 
    }

    public Pedido crearPedido(Pedido request) {
        if (request.getCliente() == null || request.getCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        Pedido nuevo = new Pedido(idCounter++, request.getCliente(), request.getProductoId(), 
            request.getCantidad(), request.getPrioridad());pedidos.add(nuevo);
        return nuevo;
    }

    public Pedido confirmarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos en estado PENDIENTE.");
        }
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedido;
    }

    public Pedido cancelarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() == EstadoPedido.DESPACHADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede cancelar un pedido " + pedido.getEstado());
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        return pedido;
    }

    public Pedido despacharPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden despachar pedidos en estado CONFIRMADO.");
        }
        pedido.setEstado(EstadoPedido.DESPACHADO);
        return pedido;
    }

    public Pedido buscarPorId(Long id) {
        return pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado con ID: " + id));
    }
}