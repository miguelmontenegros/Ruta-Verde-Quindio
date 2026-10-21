package co.edu.uniquindio.rutaverde.modelo;

import java.time.LocalDate;

public class Reserva {

    public static final double VALOR_TRANSPORTE_POR_VIAJERO = 35_000;
    public static final double VALOR_SEGURO_POR_VIAJERO = 15_000;
    public static final int MIN_VIAJEROS = 1;
    public static final int MAX_VIAJEROS = 15;

    private int numero;
    private final Cliente cliente;
    private final ProductoTuristico producto;
    private final LocalDate fechaViaje;
    private final int numeroViajeros;
    private final Guia guia;
    private final boolean incluyeTransporte;
    private final boolean incluyeSeguro;
    private final String observaciones;
    private EstadoReserva estado = EstadoReserva.PENDIENTE;
    private String codigoAutorizacionPago;

    private Reserva(Builder builder) {
        this.cliente = builder.cliente;
        this.producto = builder.producto;
        this.fechaViaje = builder.fechaViaje;
        this.numeroViajeros = builder.numeroViajeros;
        this.guia = builder.guia;
        this.incluyeTransporte = builder.incluyeTransporte;
        this.incluyeSeguro = builder.incluyeSeguro;
        this.observaciones = builder.observaciones;
    }

    public double calcularValorProducto() {
        return producto.calcularPrecioPorPersona() * numeroViajeros;
    }

    public double calcularValorTransporte() {
        return incluyeTransporte ? VALOR_TRANSPORTE_POR_VIAJERO * numeroViajeros : 0;
    }

    public double calcularValorSeguro() {
        return incluyeSeguro ? VALOR_SEGURO_POR_VIAJERO * numeroViajeros : 0;
    }

   public double calcularValorGuia() {
        return guia == null ? 0 : guia.calcularCosto(producto.calcularDuracionHoras());
    }

   public double calcularValorTotal() {
        return calcularValorProducto() + calcularValorTransporte() + calcularValorSeguro() + calcularValorGuia();
    }

   public void confirmar(String codigoAutorizacionPago) {
        this.codigoAutorizacionPago = codigoAutorizacionPago;
        this.estado = EstadoReserva.CONFIRMADA;
    }

    void asignarNumero(int numero) {
        this.numero = numero;
    }

    void revertirConfirmacion() {
        this.numero = 0;
        this.codigoAutorizacionPago = null;
        this.estado = EstadoReserva.PENDIENTE;
    }

    public boolean estaConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }

    public boolean viajaEntre(LocalDate inicio, LocalDate fin) {
        return !fechaViaje.isBefore(inicio) && !fechaViaje.isAfter(fin);
    }

    public int getNumero() { return numero; }
    public String getNumeroFormateado() { return numero == 0 ? "(sin número)" : String.format("RV-%05d", numero); }
    public Cliente getCliente() { return cliente; }
    public ProductoTuristico getProducto() { return producto; }
    public LocalDate getFechaViaje() { return fechaViaje; }
    public int getNumeroViajeros() { return numeroViajeros; }
    public Guia getGuia() { return guia; }
    public boolean isIncluyeTransporte() { return incluyeTransporte; }
    public boolean isIncluyeSeguro() { return incluyeSeguro; }
    public String getObservaciones() { return observaciones; }
    public EstadoReserva getEstado() { return estado; }
    public String getCodigoAutorizacionPago() { return codigoAutorizacionPago; }

    @Override
    public String toString() {
        return getNumeroFormateado() + " - " + cliente.getNombreCompleto() + " - " + producto.getNombre();
    }

    public static class Builder {

        private final Cliente cliente;
        private final ProductoTuristico producto;
        private final LocalDate fechaViaje;
        private final int numeroViajeros;
        private Guia guia;
        private boolean incluyeTransporte;
        private boolean incluyeSeguro;
        private String observaciones = "";

        public Builder(Cliente cliente, ProductoTuristico producto, LocalDate fechaViaje, int numeroViajeros) {
            this.cliente = cliente;
            this.producto = producto;
            this.fechaViaje = fechaViaje;
            this.numeroViajeros = numeroViajeros;
        }

        public Builder conGuia(Guia guia) {
            this.guia = guia;
            return this;
        }

        public Builder conTransporte(boolean incluyeTransporte) {
            this.incluyeTransporte = incluyeTransporte;
            return this;
        }

        public Builder conSeguro(boolean incluyeSeguro) {
            this.incluyeSeguro = incluyeSeguro;
            return this;
        }

        public Builder conObservaciones(String observaciones) {
            this.observaciones = observaciones == null ? "" : observaciones.trim();
            return this;
        }

        public Reserva build() {
            return build(LocalDate.now());
        }

        public Reserva build(LocalDate hoy) {
            if (cliente == null) {
                throw new ReglaNegocioException("No puede existir una reserva sin cliente");
            }
            if (producto == null) {
                throw new ReglaNegocioException("No puede existir una reserva sin producto");
            }
            if (fechaViaje == null) {
                throw new ReglaNegocioException("La fecha del viaje es obligatoria");
            }
            if (fechaViaje.isBefore(hoy)) {
                throw new ReglaNegocioException("La fecha del viaje no puede estar en el pasado");
            }
            if (numeroViajeros < MIN_VIAJEROS || numeroViajeros > MAX_VIAJEROS) {
                throw new ReglaNegocioException("El grupo debe tener entre 1 y 15 viajeros");
            }
            if (guia != null && !guia.hablaIdioma(cliente.getIdiomaPreferido())) {
                throw new ReglaNegocioException("El guía " + guia.getNombre() + " no habla "
                        + cliente.getIdiomaPreferido() + ", el idioma preferido del cliente");
            }
            if (!producto.estaDisponible()) {
                throw new ReglaNegocioException("El producto '" + producto.getNombre()
                        + "' incluye al menos una actividad suspendida");
            }
            if (producto.obtenerActividades().isEmpty()) {
                throw new ReglaNegocioException("El producto '" + producto.getNombre() + "' no tiene actividades");
            }
            if (producto instanceof Paquete && !((Paquete) producto).tieneActividadesUnicas()) {
                throw new ReglaNegocioException("El plan incluye dos veces la misma actividad");
            }
            return new Reserva(this);
        }
    }
}
