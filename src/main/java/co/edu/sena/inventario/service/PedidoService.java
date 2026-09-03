package co.edu.sena.inventario.service;

import co.edu.sena.inventario.dto.ResumenPedidosDTO;
import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.Prioridad;
import co.edu.sena.inventario.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> getTodosPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado con ID: " + id));
    }

    public Pedido crearPedido(Pedido request) {
        // Garantizar que no se sobrescriban registros
        request.setId(null);

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

        // 2. Descontar stock (Lanza IllegalStateException si no hay suficiente stock)
        productoService.descontarStock(request.getProductoId(), request.getCantidad());

        // 3. Configurar estado e insertar
        request.setEstado(EstadoPedido.PENDIENTE);
        if (request.getUnidadesReservadas() == null) request.setUnidadesReservadas(0);
        if (request.getUnidadesFaltantes() == null) request.setUnidadesFaltantes(0);

        return pedidoRepository.save(request);
    }

    public Pedido confirmarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos en estado PENDIENTE.");
        }
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    public Pedido cancelarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() == EstadoPedido.DESPACHADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede cancelar un pedido " + pedido.getEstado());
        }

        productoService.reponerStock(pedido.getProductoId(), pedido.getCantidad());

        pedido.setEstado(EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    public Pedido despacharPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden despachar pedidos en estado CONFIRMADO.");
        }
        pedido.setEstado(EstadoPedido.DESPACHADO);
        return pedidoRepository.save(pedido);
    }

    public Pedido confirmarParcial(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar parcialmente pedidos PENDIENTES.");
        }
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    public Pedido completarPorReabastecimiento(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE && pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden reabastecer pedidos PENDIENTES o CONFIRMADOS.");
        }
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    // ==========================================
    // CONSULTAS
    // ==========================================

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> buscarPorPrioridad(Prioridad prioridad) {
        return pedidoRepository.findByPrioridad(prioridad);
    }

    public List<Pedido> buscarPorCliente(String cliente) {
        return pedidoRepository.findByClienteContainingIgnoreCase(cliente);
    }

    public List<Pedido> buscarUrgentes() {
        return pedidoRepository.findByPrioridad(Prioridad.URGENTE).stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE || p.getEstado() == EstadoPedido.CONFIRMADO)
                .collect(Collectors.toList());
    }

    public List<Pedido> buscarEnRiesgo() {
        return pedidoRepository.findByEstadoAndPrioridadIn(
                EstadoPedido.PENDIENTE, 
                List.of(Prioridad.URGENTE, Prioridad.ALTA)
        );
    }

    public ResumenPedidosDTO obtenerResumen() {
        List<Pedido> todos = pedidoRepository.findAll();
        long total = todos.size();
        long pendientes = todos.stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).count();
        long confirmados = todos.stream().filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO).count();
        long despachados = todos.stream().filter(p -> p.getEstado() == EstadoPedido.DESPACHADO).count();
        long cancelados = todos.stream().filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count();
        long urgentes = todos.stream().filter(p -> p.getPrioridad() == Prioridad.URGENTE).count();

        return new ResumenPedidosDTO(total, pendientes, confirmados, despachados, cancelados, urgentes);
    }

    public Pedido obtenerSiguiente() {
        List<Prioridad> ordenPrioridad = List.of(Prioridad.URGENTE, Prioridad.ALTA, Prioridad.MEDIA, Prioridad.BAJA);

        return pedidoRepository.findByEstado(EstadoPedido.PENDIENTE).stream()
                .min(Comparator.comparing((Pedido p) -> ordenPrioridad.indexOf(p.getPrioridad()))
                        .thenComparing(Pedido::getId))
                .orElseThrow(() -> new NoSuchElementException("No hay pedidos pendientes por atender."));
    }
}