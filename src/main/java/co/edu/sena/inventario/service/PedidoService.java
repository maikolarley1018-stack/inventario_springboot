package co.edu.sena.inventario.service;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PedidoService {

    @Autowired
    private ProductoService productoService;

    private final List<Pedido> pedidos = new ArrayList<>();
    private Long idCounter = 1L;

    public List<Pedido> getTodosPedidos() {
        return pedidos;
    }

    public Pedido crearPedido(Pedido request) {
        // 1. Validaciones de campos obligatorios
        if (request.getCliente() == null || request.getCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        if (request.getProductoId() == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (request.getPrioridad() == null) {
            throw new IllegalArgumentException("La prioridad es obligatoria.");
        }

        // 2. Obtiene el producto e inspecciona el stock actual
        Producto producto = productoService.buscarPorId(request.getProductoId());

        if (producto.getCantidad() < request.getCantidad()) {
            throw new IllegalArgumentException("No se puede crear el pedido: Stock insuficiente ("
                    + producto.getCantidad() + " disponibles, solicitadas: " + request.getCantidad() + ")");
        }

        // 3. Si hay suficiente stock, crea el pedido normalmente
        Pedido nuevo = new Pedido(idCounter++, request.getCliente(), request.getProductoId(),
                request.getCantidad(), request.getPrioridad());
        pedidos.add(nuevo);
        return nuevo;
    }

    public Pedido confirmarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos en estado PENDIENTE.");
        }

        // Descuenta las unidades del inventario (lanza error si no hay stock)
        productoService.descontarStock(pedido.getProductoId(), pedido.getCantidad());

        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedido;
    }

    public Pedido cancelarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() == EstadoPedido.DESPACHADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede cancelar un pedido " + pedido.getEstado());
        }

        // Si ya estaba confirmado, devuelve el stock al inventario
        if (pedido.getEstado() == EstadoPedido.CONFIRMADO) {
            productoService.reponerStock(pedido.getProductoId(), pedido.getCantidad());
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

    // ==========================================
    // MÉTODOS DE SOPORTE PARA EL BOSS FINAL
    // ==========================================

    public Pedido confirmarParcial(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar parcialmente pedidos PENDIENTES.");
        }

        Producto producto = productoService.buscarPorId(pedido.getProductoId());
        if (producto.getCantidad() <= 0) {
            throw new IllegalStateException("No hay stock disponible para realizar una confirmación parcial.");
        }

        // Si el stock disponible alcanza o es menor, retiene el stock total existente
        int unidadesAConfirmar = Math.min(producto.getCantidad(), pedido.getCantidad());
        productoService.descontarStock(pedido.getProductoId(), unidadesAConfirmar);

        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedido;
    }

    public Pedido completarPorReabastecimiento(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE && pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden reabastecer pedidos PENDIENTES o CONFIRMADOS.");
        }

        // Intenta completar la reserva de stock si se surtió el producto
        productoService.descontarStock(pedido.getProductoId(), pedido.getCantidad());
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedido;
    }
}