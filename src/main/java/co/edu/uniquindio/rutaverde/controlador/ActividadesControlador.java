package co.edu.uniquindio.rutaverde.controlador;

import co.edu.uniquindio.rutaverde.ContextoAplicacion;
import co.edu.uniquindio.rutaverde.modelo.Actividad;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.EstadoActividad;
import co.edu.uniquindio.rutaverde.modelo.NivelDificultad;
import co.edu.uniquindio.rutaverde.vista.UtilVista;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ActividadesControlador implements Refrescable {

    private final Agencia agencia = ContextoAplicacion.getInstancia().getAgencia();

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtCupo;
    @FXML private ComboBox<NivelDificultad> cmbDificultad;
    @FXML private ComboBox<EstadoActividad> cmbEstado;
    @FXML private TableView<Actividad> tblActividades;
    @FXML private TableColumn<Actividad, String> colCodigo;
    @FXML private TableColumn<Actividad, String> colNombre;
    @FXML private TableColumn<Actividad, String> colMunicipio;
    @FXML private TableColumn<Actividad, Double> colDuracion;
    @FXML private TableColumn<Actividad, Double> colPrecio;
    @FXML private TableColumn<Actividad, Integer> colCupo;
    @FXML private TableColumn<Actividad, NivelDificultad> colDificultad;
    @FXML private TableColumn<Actividad, EstadoActividad> colEstado;

    @FXML
    private void initialize() {
        cmbDificultad.getItems().setAll(NivelDificultad.values());
        cmbEstado.getItems().setAll(EstadoActividad.values());
        cmbEstado.setValue(EstadoActividad.DISPONIBLE);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMunicipio.setCellValueFactory(new PropertyValueFactory<>("municipio"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionHoras"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioPorPersona"));
        colCupo.setCellValueFactory(new PropertyValueFactory<>("cupoMaximoDia"));
        colDificultad.setCellValueFactory(new PropertyValueFactory<>("dificultad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tblActividades.getSelectionModel().selectedItemProperty().addListener((obs, anterior, a) -> mostrar(a));
        refrescar();
    }

    @FXML
    private void onAgregar() {
        try {
            agencia.registrarActividad(new Actividad(txtCodigo.getText(), txtNombre.getText(), txtMunicipio.getText(),
                    UtilVista.leerDecimal(txtDuracion, "La duración"), UtilVista.leerDecimal(txtPrecio, "El precio"),
                    UtilVista.leerEntero(txtCupo, "El cupo"), cmbDificultad.getValue(), cmbEstado.getValue()));
            onLimpiar();
            refrescar();
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        try {
            agencia.actualizarActividad(txtCodigo.getText(), txtNombre.getText(), txtMunicipio.getText(),
                    UtilVista.leerDecimal(txtDuracion, "La duración"), UtilVista.leerDecimal(txtPrecio, "El precio"),
                    UtilVista.leerEntero(txtCupo, "El cupo"), cmbDificultad.getValue(), cmbEstado.getValue());
            refrescar();
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        try {
            if (UtilVista.confirmar("¿Eliminar la actividad " + txtCodigo.getText() + "?")) {
                agencia.eliminarActividad(txtCodigo.getText());
                onLimpiar();
                refrescar();
            }
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        tblActividades.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtNombre.clear();
        txtMunicipio.clear();
        txtDuracion.clear();
        txtPrecio.clear();
        txtCupo.clear();
        cmbDificultad.setValue(null);
        cmbEstado.setValue(EstadoActividad.DISPONIBLE);
        txtCodigo.setDisable(false);
    }

    private void mostrar(Actividad actividad) {
        if (actividad == null) {
            return;
        }
        txtCodigo.setText(actividad.getCodigo());
        txtCodigo.setDisable(true);
        txtNombre.setText(actividad.getNombre());
        txtMunicipio.setText(actividad.getMunicipio());
        txtDuracion.setText(String.valueOf(actividad.getDuracionHoras()));
        txtPrecio.setText(String.valueOf((long) actividad.getPrecioPorPersona()));
        txtCupo.setText(String.valueOf(actividad.getCupoMaximoDia()));
        cmbDificultad.setValue(actividad.getDificultad());
        cmbEstado.setValue(actividad.getEstado());
    }

    @Override
    public void refrescar() {
        tblActividades.getItems().setAll(agencia.getActividades());
        tblActividades.refresh();
    }
}
