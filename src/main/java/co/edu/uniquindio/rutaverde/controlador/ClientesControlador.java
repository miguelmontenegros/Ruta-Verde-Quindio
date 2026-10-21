package co.edu.uniquindio.rutaverde.controlador;

import co.edu.uniquindio.rutaverde.ContextoAplicacion;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Cliente;
import co.edu.uniquindio.rutaverde.vista.UtilVista;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientesControlador implements Refrescable {

    private final Agencia agencia = ContextoAplicacion.getInstancia().getAgencia();

    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtPais;
    @FXML private TextField txtIdioma;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colDocumento;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colPais;
    @FXML private TableColumn<Cliente, String> colIdioma;

    @FXML
    private void initialize() {
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colPais.setCellValueFactory(new PropertyValueFactory<>("paisOrigen"));
        colIdioma.setCellValueFactory(new PropertyValueFactory<>("idiomaPreferido"));
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, cliente) -> mostrar(cliente));
        refrescar();
    }

    @FXML
    private void onAgregar() {
        try {
            agencia.registrarCliente(new Cliente(txtDocumento.getText(), txtNombre.getText(), txtTelefono.getText(),
                    txtCorreo.getText(), txtPais.getText(), txtIdioma.getText()));
            onLimpiar();
            refrescar();
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        try {
            agencia.actualizarCliente(txtDocumento.getText(), txtNombre.getText(), txtTelefono.getText(),
                    txtCorreo.getText(), txtPais.getText(), txtIdioma.getText());
            refrescar();
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        try {
            if (UtilVista.confirmar("¿Eliminar el cliente " + txtDocumento.getText() + "?")) {
                agencia.eliminarCliente(txtDocumento.getText());
                onLimpiar();
                refrescar();
            }
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        tblClientes.getSelectionModel().clearSelection();
        txtDocumento.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtPais.clear();
        txtIdioma.clear();
        txtDocumento.setDisable(false);
    }

    private void mostrar(Cliente cliente) {
        if (cliente == null) {
            return;
        }
        txtDocumento.setText(cliente.getDocumento());
        txtDocumento.setDisable(true); // el documento no se puede cambiar
        txtNombre.setText(cliente.getNombreCompleto());
        txtTelefono.setText(cliente.getTelefono());
        txtCorreo.setText(cliente.getCorreo());
        txtPais.setText(cliente.getPaisOrigen());
        txtIdioma.setText(cliente.getIdiomaPreferido());
    }

    @Override
    public void refrescar() {
        tblClientes.getItems().setAll(agencia.getClientes());
        tblClientes.refresh();
    }
}
