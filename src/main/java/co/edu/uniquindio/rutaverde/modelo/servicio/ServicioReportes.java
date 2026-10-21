package co.edu.uniquindio.rutaverde.modelo.servicio;

import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.ProductoTuristico;
import co.edu.uniquindio.rutaverde.modelo.ReglaNegocioException;
import co.edu.uniquindio.rutaverde.modelo.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioReportes {

    private final Agencia agencia;

    public ServicioReportes(Agencia agencia) {
        this.agencia = agencia;
    }

    public ReporteReservas generarReporte(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new ReglaNegocioException("Debe indicar la fecha inicial y la final");
        }
        if (fin.isBefore(inicio)) {
            throw new ReglaNegocioException("La fecha final no puede ser anterior a la inicial");
        }

        double ingresos = 0;
        int reservasContadas = 0;

        List<ProductoTuristico> productos = new ArrayList<>();
        List<Integer> viajeros = new ArrayList<>();

        for (Reserva reserva : agencia.getReservasConfirmadas()) {
            if (reserva.viajaEntre(inicio, fin)) {
                ingresos += reserva.calcularValorTotal();
                reservasContadas++;
                int posicion = productos.indexOf(reserva.getProducto());
                if (posicion == -1) {
                    productos.add(reserva.getProducto());
                    viajeros.add(reserva.getNumeroViajeros());
                } else {
                    viajeros.set(posicion, viajeros.get(posicion) + reserva.getNumeroViajeros());
                }
            }
        }

        ProductoTuristico mayor = null;
        int viajerosMayor = 0;
        for (int i = 0; i < productos.size(); i++) {
            if (viajeros.get(i) > viajerosMayor) {
                mayor = productos.get(i);
                viajerosMayor = viajeros.get(i);
            }
        }
        return new ReporteReservas(ingresos, reservasContadas, mayor, viajerosMayor);
    }
}
