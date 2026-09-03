package co.edu.sena.inventario.repository;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Boss 2: Consultas derivadas requeridas por la guía
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByPrioridad(Prioridad prioridad);
    List<Pedido> findByClienteContainingIgnoreCase(String cliente);

    // Boss 3: Consulta propia del equipo (Pedidos urgentes que siguen pendientes)
    List<Pedido> findByEstadoAndPrioridad(EstadoPedido estado, Prioridad prioridad);
}
