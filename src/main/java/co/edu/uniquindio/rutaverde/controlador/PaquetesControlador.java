package co.edu.uniquindio.rutaverde.controlador;

import co.edu.uniquindio.rutaverde.ContextoAplicacion;
import co.edu.uniquindio.rutaverde.modelo.Actividad;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Formatos;
import co.edu.uniquindio.rutaverde.modelo.Paquete;
import co.edu.uniquindio.rutaverde.modelo.ProductoTuristico;
import co.edu.uniquindio.rutaverde.vista.UtilVista;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class PaquetesControlador implements Refrescable {

    private final Agencia agencia = ContextoAplicacion.getInstancia().getAgencia();

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescuento;
    @FXML private ListView<Paquete> lstPaquetes;
    @FXML private TreeView<ProductoTuristico> treeComposicion;
    @FXML private ComboBox<ProductoTuristico> cmbComponente;
    @FXML private Label lblConsulta;
    @FXML private ListView<String> lstActividades;

    @FXML
    private void initialize() {
        lstPaquetes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, p) -> mostrarArbol(p));
        treeComposicion.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nodo) -> {
            if (nodo != null) {
                consultar(nodo.getValue());
            }
        });
        refrescar();
    }

    @FXML
    private void onCrear() {
        try {
            Paquete paquete = new Paquete(txtCodigo.getText(), txtNombre.getText(),
                    UtilVista.leerDecimal(txtDescuento, "El descuento"));
            agencia.registrarPaquete(paquete);
            txtCodigo.clear();
            txtNombre.clear();
            txtDescuento.clear();
            refrescar();
            lstPaquetes.getSelectionModel().select(paquete);
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Paquete paquete = lstPaquetes.getSelectionModel().getSelectedItem();
        try {
            if (paquete != null && UtilVista.confirmar("¿Eliminar el paquete " + paquete.getNombre() + "?")) {
                agencia.eliminarPaquete(paquete.getCodigo());
                refrescar();
            }
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onAgregarComponente() {
        Paquete paquete = lstPaquetes.getSelectionModel().getSelectedItem();
        if (paquete == null) {
            UtilVista.error("Seleccione el paquete al que desea agregar");
            return;
        }
        try {
            paquete.agregar(cmbComponente.getValue()); // valida ciclos y actividades repetidas
            mostrarArbol(paquete);
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    private void mostrarArbol(Paquete paquete) {
        if (paquete == null) {
            treeComposicion.setRoot(null);
            return;
        }
        TreeItem<ProductoTuristico> raiz = crearNodo(paquete);
        treeComposicion.setRoot(raiz);
        treeComposicion.getSelectionModel().select(raiz);
    }

    private TreeItem<ProductoTuristico> crearNodo(ProductoTuristico producto) {
        TreeItem<ProductoTuristico> nodo = new TreeItem<>(producto);
        nodo.setExpanded(true);
        if (producto instanceof Paquete) {
            for (ProductoTuristico componente : ((Paquete) producto).getComponentes()) {
                nodo.getChildren().add(crearNodo(componente));
            }
        }
        return nodo;
    }

    private void consultar(ProductoTuristico producto) {
        lblConsulta.setText(producto.getTipo() + ": " + producto.getNombre()
                + "\nPrecio por persona: " + Formatos.moneda(producto.calcularPrecioPorPersona())
                + "\nDuración: " + Formatos.horas(producto.calcularDuracionHoras()) + " horas"
                + "\nDisponible: " + (producto.estaDisponible() ? "Sí" : "No, tiene actividades suspendidas"));
        lstActividades.getItems().clear();
        for (Actividad actividad : producto.obtenerActividades()) {
            lstActividades.getItems().add(actividad.getNombre() + " (" + actividad.getMunicipio() + ")");
        }
    }

    @Override
    public void refrescar() {
        Paquete seleccionado = lstPaquetes.getSelectionModel().getSelectedItem();
        lstPaquetes.getItems().setAll(agencia.getPaquetes());
        cmbComponente.getItems().setAll(agencia.getProductos());
        if (seleccionado != null && lstPaquetes.getItems().contains(seleccionado)) {
            lstPaquetes.getSelectionModel().select(seleccionado);
            mostrarArbol(seleccionado);
        } else {
            lstPaquetes.getSelectionModel().selectFirst();
        }
    }
}
