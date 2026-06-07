package pe.nailsbeauty.entity;

public enum EstadoFactura {
    PENDIENTE_PAGO("Pendiente de pago"),
    PAGADA("Pagada"),
    ANULADA("Anulada");

    private final String valor;

    EstadoFactura(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
