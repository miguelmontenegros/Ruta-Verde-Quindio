package co.edu.uniquindio.rutaverde.controlador;

import co.edu.uniquindio.rutaverde.ContextoAplicacion;
import co.edu.uniquindio.rutaverde.modelo.Formatos;
import co.edu.uniquindio.rutaverde.modelo.servicio.ReporteReservas;
import co.edu.uniquindio.rutaverde.modelo.servicio.ServicioReportes;
import co.edu.uniquindio.rutaverde.vista.UtilVista;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class ReportesControlador implements Refrescable {

    private final ServicioReportes servicioReportes = ContextoAplicacion.getInstancia().getServicioReportes();

    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private Label lblResultado;

    @FXML
    private void initialize() {
        dpInicio.setValue(LocalDate.now());
        dpFin.setValue(LocalDate.now().plusMonths(1));
    }

    @FXML
    private void onGenerar() {
        try {
            ReporteReservas reporte = servicioReportes.generarReporte(dpInicio.getValue(), dpFin.getValue());
            String producto = "Sin reservas en el periodo";
            if (reporte.getProductoMasViajeros() != null) {
                producto = reporte.getProductoMasViajeros().getNombre()
                        + " (" + reporte.getViajerosProductoMasViajeros() + " viajeros)";
            }
            lblResultado.setText("Reservas confirmadas en el periodo: " + reporte.getReservasContadas()
                    + "\nIngresos: " + Formatos.moneda(reporte.getIngresos())
                    + "\nProducto con más viajeros: " + producto);
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @Override
    public void refrescar() {
        lblResultado.setText("");
    }
}
