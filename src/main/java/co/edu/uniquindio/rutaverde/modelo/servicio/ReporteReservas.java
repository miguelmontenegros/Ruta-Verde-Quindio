package co.edu.uniquindio.rutaverde.modelo.servicio;

import co.edu.uniquindio.rutaverde.modelo.ProductoTuristico;

public class ReporteReservas {

    private final double ingresos;
    private final int reservasContadas;
    private final ProductoTuristico productoMasViajeros;
    private final int viajerosProductoMasViajeros;

    public ReporteReservas(double ingresos, int reservasContadas, ProductoTuristico productoMasViajeros,
                           int viajerosProductoMasViajeros) {
        this.ingresos = ingresos;
        this.reservasContadas = reservasContadas;
        this.productoMasViajeros = productoMasViajeros;
        this.viajerosProductoMasViajeros = viajerosProductoMasViajeros;
    }

    public double getIngresos() { return ingresos; }
    public int getReservasContadas() { return reservasContadas; }
    public ProductoTuristico getProductoMasViajeros() { return productoMasViajeros; }
    public int getViajerosProductoMasViajeros() { return viajerosProductoMasViajeros; }
}
