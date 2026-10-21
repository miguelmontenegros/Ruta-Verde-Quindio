package co.edu.uniquindio.rutaverde.controlador;

import co.edu.uniquindio.rutaverde.ContextoAplicacion;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class PrincipalControlador {

    @FXML private Label lblAgencia;
    @FXML private Label lblDatosAgencia;
    @FXML private TabPane tabPane;

    @FXML private ReservasControlador reservasController;
    @FXML private PaquetesControlador paquetesController;
    @FXML private ActividadesControlador actividadesController;
    @FXML private ClientesControlador clientesController;
    @FXML private GuiasControlador guiasController;
    @FXML private ReportesControlador reportesController;

    @FXML
    private void initialize() {
        Agencia agencia = ContextoAplicacion.getInstancia().getAgencia();
        lblAgencia.setText(agencia.getNombreComercial());
        lblDatosAgencia.setText("NIT " + agencia.getNit() + "  ·  RNT " + agencia.getRnt() + "  ·  " + agencia.getDireccion());
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, anterior, nuevo) -> refrescarTodo());
    }

    private void refrescarTodo() {
        reservasController.refrescar();
        paquetesController.refrescar();
        actividadesController.refrescar();
        clientesController.refrescar();
        guiasController.refrescar();
        reportesController.refrescar();
    }
}