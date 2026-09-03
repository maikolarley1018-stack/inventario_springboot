package co.edu.sena.inventario.service;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.Prioridad;
import co.edu.sena.inventario.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> getTodosPedidos() {
        return pedidoRepository.findAll();
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

        // 3. Verifica existencias y DESCUENTA EL STOCK INMEDIATAMENTE
        productoService.descontarStock(request.getProductoId(), request.getCantidad());

        // 4. Crear e ingresar el pedido en estado PENDIENTE usando JPA
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

        // Liberamos/reponemos el stock al cancelar
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

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado con ID: " + id));
    }

    // ==========================================
    // CONSULTAS Y CENTRO DE CONTROL (Boss 2 y Boss 3)
    // ==========================================

    public List<Pedido> obtenerPendientes() {
        return pedidoRepository.findByEstado(EstadoPedido.PENDIENTE);
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> buscarPorPrioridad(Prioridad prioridad) {
        return pedidoRepository.findByPrioridad(prioridad);
    }

    public List<Pedido> buscarPorCliente(String cliente) {
        return pedidoRepository.findByClienteContainingIgnoreCase(cliente);
    }

    public List<Pedido> buscarUrgentesPendientes() {
        return pedidoRepository.findByEstadoAndPrioridad(EstadoPedido.PENDIENTE, Prioridad.URGENTE);
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
}