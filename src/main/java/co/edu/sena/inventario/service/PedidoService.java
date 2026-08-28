package co.edu.sena.inventario.service;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.Prioridad;
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

        // 2. Validar que la prioridad exista
        try {
            Prioridad.valueOf(request.getPrioridad().toString().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Prioridad no válida. Las opciones permitidas son: BAJA, MEDIA, ALTA, URGENTE.");
        }

        // 3. Verifica existencias y DESCUENTA EL STOCK INMEDIATAMENTE al crear
        // (descontarStock lanzará excepción si no hay suficiente cantidad disponible)
        productoService.descontarStock(request.getProductoId(), request.getCantidad());

        // 4. Crear e ingresar el pedido en estado PENDIENTE
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

        // Como el stock ya fue apartado al crear el pedido, solo actualizamos el estado
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedido;
    }

    public Pedido cancelarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() == EstadoPedido.DESPACHADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede cancelar un pedido " + pedido.getEstado());
        }

        // Como el stock fue reservado al crear el pedido, SIEMPRE liberamos/reponemos
        // el stock al cancelar (siempre que esté en PENDIENTE o CONFIRMADO)
        productoService.reponerStock(pedido.getProductoId(), pedido.getCantidad());

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
    // CONSULTAS Y CENTRO DE CONTROL
    // ==========================================

    public List<Pedido> obtenerPendientes() {
        return pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                .toList();
    }

    // ==========================================
    // MÉTODOS DE SOPORTE PARA EL BOSS FINAL
    // ==========================================

    public Pedido confirmarParcial(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar parcialmente pedidos PENDIENTES.");
        }

        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedido;
    }

    public Pedido completarPorReabastecimiento(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE && pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden reabastecer pedidos PENDIENTES o CONFIRMADOS.");
        }

        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedido;
    }
}