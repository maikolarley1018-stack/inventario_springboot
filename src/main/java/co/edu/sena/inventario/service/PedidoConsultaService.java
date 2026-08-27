package co.edu.sena.inventario.service;

import co.edu.sena.inventario.dto.ResumenPedidosDTO;
import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.Prioridad;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoConsultaService {

    // GET /pedidos/pendientes
    public List<Pedido> obtenerPendientes(List<Pedido> listaPedidos) {
        return listaPedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                .collect(Collectors.toList());
    }

    // GET /pedidos/urgentes
    public List<Pedido> obtenerUrgentes(List<Pedido> listaPedidos) {
        return listaPedidos.stream()
                .filter(p -> p.getPrioridad() == Prioridad.URGENTE)
                .collect(Collectors.toList());
    }

    // GET /pedidos/estado?estado=CONFIRMADO
    public List<Pedido> obtenerPorEstado(List<Pedido> listaPedidos, EstadoPedido estado) {
        return listaPedidos.stream()
                .filter(p -> p.getEstado() == estado)
                .collect(Collectors.toList());
    }

    // GET /pedidos/resumen
    public ResumenPedidosDTO obtenerResumen(List<Pedido> listaPedidos) {
        long total = listaPedidos.size();
        long pendientes = listaPedidos.stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).count();
        long confirmados = listaPedidos.stream().filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO).count();
        long despachados = listaPedidos.stream().filter(p -> p.getEstado() == EstadoPedido.DESPACHADO).count();
        long cancelados = listaPedidos.stream().filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count();
        long urgentes = listaPedidos.stream().filter(p -> p.getPrioridad() == Prioridad.URGENTE).count();

        return new ResumenPedidosDTO(total, pendientes, confirmados, despachados, cancelados, urgentes);
    }

    // GET /pedidos/siguiente (Algoritmo de prioridad: URGENTE > ALTA > MEDIA > BAJA, desempate por ID)
    public Pedido obtenerSiguiente(List<Pedido> listaPedidos) {
        List<Prioridad> ordenPrioridad = List.of(Prioridad.URGENTE, Prioridad.ALTA, Prioridad.MEDIA, Prioridad.BAJA);

        return listaPedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                .min(Comparator.comparing((Pedido p) -> ordenPrioridad.indexOf(p.getPrioridad()))
                        .thenComparing(Pedido::getId))
                .orElseThrow(() -> new NoSuchElementException("No hay pedidos pendientes por atender."));
    }

    // GET /pedidos/en-riesgo (Endpoint sorpresa: Detectar pedidos urgentes o de alta prioridad aún pendientes)
    public List<Pedido> obtenerEnRiesgo(List<Pedido> listaPedidos) {
        return listaPedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                .filter(p -> p.getPrioridad() == Prioridad.URGENTE || p.getPrioridad() == Prioridad.ALTA)
                .collect(Collectors.toList());
    }
}