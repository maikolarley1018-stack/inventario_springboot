package co.edu.sena.inventario.model;

public class Pedido {
    private Long id;
    private String cliente;
    private Long productoId;
    private Integer cantidad;
    private Prioridad prioridad;
    private EstadoPedido estado;
    private Integer unidadesReservadas;
    private Integer unidadesFaltantes;

    public Pedido() {
    }

    public Pedido(Long id, String cliente, Long productoId, Integer cantidad, Prioridad prioridad) {
        this.id = id;
        this.cliente = cliente;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.prioridad = prioridad;
        this.estado = EstadoPedido.PENDIENTE;
        this.unidadesReservadas = 0;
        this.unidadesFaltantes = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Integer getUnidadesReservadas() {
        return unidadesReservadas;
    }

    public void setUnidadesReservadas(Integer unidadesReservadas) {
        this.unidadesReservadas = unidadesReservadas;
    }

    public Integer getUnidadesFaltantes() {
        return unidadesFaltantes;
    }

    public void setUnidadesFaltantes(Integer unidadesFaltantes) {
        this.unidadesFaltantes = unidadesFaltantes;
    }
}