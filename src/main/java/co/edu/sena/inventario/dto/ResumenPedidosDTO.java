package co.edu.sena.inventario.dto;

public class ResumenPedidosDTO {
    private long total;
    private long pendientes;
    private long confirmados;
    private long despachados;
    private long cancelados;
    private long urgentes;

    public ResumenPedidosDTO(long total, long pendientes, long confirmados, long despachados, long cancelados, long urgentes) {
        this.total = total;
        this.pendientes = pendientes;
        this.confirmados = confirmados;
        this.despachados = despachados;
        this.cancelados = cancelados;
        this.urgentes = urgentes;
    }

    public long getTotal() { return total; }
    public long getPendientes() { return pendientes; }
    public long getConfirmados() { return confirmados; }
    public long getDespachados() { return despachados; }
    public long getCancelados() { return cancelados; }
    public long getUrgentes() { return urgentes; }
}