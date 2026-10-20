package co.edu.uniquindio.rutaverde.controlador;

import co.edu.uniquindio.rutaverde.ContextoAplicacion;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Guia;
import co.edu.uniquindio.rutaverde.vista.UtilVista;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class GuiasControlador implements Refrescable {

    private final Agencia agencia = ContextoAplicacion.getInstancia().getAgencia();

    @FXML private TextField txtIdentificacion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtIdiomas;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtTarifa;
    @FXML private TableView<Guia> tblGuias;
    @FXML private TableColumn<Guia, String> colIdentificacion;
    @FXML private TableColumn<Guia, String> colNombre;
    @FXML private TableColumn<Guia, String> colIdiomas;
    @FXML private TableColumn<Guia, String> colTelefono;
    @FXML private TableColumn<Guia, Double> colTarifa;

    @FXML
    private void initialize() {
        colIdentificacion.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIdiomas.setCellValueFactory(c -> new SimpleStringProperty(String.join(", ", c.getValue().getIdiomas())));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colTarifa.setCellValueFactory(new PropertyValueFactory<>("tarifaDia"));
        tblGuias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, guia) -> mostrar(guia));
        refrescar();
    }

    @FXML
    private void onAgregar() {
        try {
            agencia.registrarGuia(new Guia(txtIdentificacion.getText(), txtNombre.getText(), leerIdiomas(),
                    txtTelefono.getText(), UtilVista.leerDecimal(txtTarifa, "La tarifa")));
            onLimpiar();
            refrescar();
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        try {
            agencia.actualizarGuia(txtIdentificacion.getText(), txtNombre.getText(), leerIdiomas(),
                    txtTelefono.getText(), UtilVista.leerDecimal(txtTarifa, "La tarifa"));
            refrescar();
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        try {
            if (UtilVista.confirmar("¿Eliminar el guía " + txtIdentificacion.getText() + "?")) {
                agencia.eliminarGuia(txtIdentificacion.getText());
                onLimpiar();
                refrescar();
            }
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        tblGuias.getSelectionModel().clearSelection();
        txtIdentificacion.clear();
        txtNombre.clear();
        txtIdiomas.clear();
        txtTelefono.clear();
        txtTarifa.clear();
        txtIdentificacion.setDisable(false);
    }

    private List<String> leerIdiomas() {
        List<String> idiomas = new ArrayList<>();
        for (String idioma : txtIdiomas.getText().split(",")) {
            if (!idioma.isBlank()) {
                idiomas.add(idioma.trim());
            }
        }
        return idiomas;
    }

    private void mostrar(Guia guia) {
        if (guia == null) {
            return;
        }
        txtIdentificacion.setText(guia.getIdentificacion());
        txtIdentificacion.setDisable(true);
        txtNombre.setText(guia.getNombre());
        txtIdiomas.setText(String.join(", ", guia.getIdiomas()));
        txtTelefono.setText(guia.getTelefono());
        txtTarifa.setText(String.valueOf((long) guia.getTarifaDia()));
    }

    @Override
    public void refrescar() {
        tblGuias.getItems().setAll(agencia.getGuias());
        tblGuias.refresh();
    }
}
