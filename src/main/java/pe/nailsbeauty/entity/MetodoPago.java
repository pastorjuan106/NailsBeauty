package pe.nailsbeauty.entity;

public enum MetodoPago {
    EFECTIVO("Efectivo"),
    YAPE("Yape"),
    PLIN("Plin"),
    TARJETA("Tarjeta");

    private final String valor;

    MetodoPago(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
