package co.edu.sena.inventario.service;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PedidoService {

    private final List<Pedido> pedidos = new ArrayList<>();
    private final Map<Long, Integer> inventario = new HashMap<>(Map.of(
            1L, 50, 2L, 30, 3L, 20, 4L, 9, 5L, 35));
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
                request.getCantidad(), request.getPrioridad());
        pedidos.add(nuevo);
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

    // BOSS FINAL - PARTE 1: Reserva parcial cuando no alcanza el stock
    public Map<String, Object> confirmarParcial(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo un pedido PENDIENTE puede pasar a EN_ESPERA_STOCK.");
        }

        int stockActual = inventario.getOrDefault(pedido.getProductoId(), 0);
        if (stockActual >= pedido.getCantidad()) {
            throw new IllegalStateException("Hay stock suficiente: use PUT /pedidos/{id}/confirmar en su lugar.");
        }
        if (stockActual <= 0) {
            throw new IllegalStateException("No hay unidades disponibles para reservar.");
        }

        int unidadesFaltantes = pedido.getCantidad() - stockActual;
        inventario.put(pedido.getProductoId(), 0);
        pedido.setEstado(EstadoPedido.EN_ESPERA_STOCK);
        pedido.setUnidadesReservadas(stockActual);
        pedido.setUnidadesFaltantes(unidadesFaltantes);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Stock reservado parcialmente. Pedido en EN_ESPERA_STOCK.");
        respuesta.put("pedidoId", pedido.getId());
        respuesta.put("estado", pedido.getEstado());
        respuesta.put("unidadesReservadas", stockActual);
        respuesta.put("unidadesFaltantes", unidadesFaltantes);
        return respuesta;
    }

    // BOSS FINAL - PARTE 2: Completar pedido tras reabastecimiento
    public Map<String, Object> completarPorReabastecimiento(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.EN_ESPERA_STOCK) {
            throw new IllegalStateException("Solo un pedido EN_ESPERA_STOCK puede completarse por reabastecimiento.");
        }

        int unidadesFaltantes = pedido.getUnidadesFaltantes();
        int stockActual = inventario.getOrDefault(pedido.getProductoId(), 0);
        if (stockActual < unidadesFaltantes) {
            throw new IllegalStateException("Aún no hay stock suficiente para completar el pedido. Faltan "
                    + (unidadesFaltantes - stockActual) + " unidades.");
        }

        inventario.put(pedido.getProductoId(), stockActual - unidadesFaltantes);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedido.setUnidadesFaltantes(0);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Pedido completado y confirmado tras reabastecimiento.");
        respuesta.put("pedidoId", pedido.getId());
        respuesta.put("estado", pedido.getEstado());
        return respuesta;
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