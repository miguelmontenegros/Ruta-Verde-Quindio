package co.edu.uniquindio.rutaverde.controlador;

import co.edu.uniquindio.rutaverde.ContextoAplicacion;
import co.edu.uniquindio.rutaverde.modelo.Actividad;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Cliente;
import co.edu.uniquindio.rutaverde.modelo.Formatos;
import co.edu.uniquindio.rutaverde.modelo.Guia;
import co.edu.uniquindio.rutaverde.modelo.ProductoTuristico;
import co.edu.uniquindio.rutaverde.modelo.Reserva;
import co.edu.uniquindio.rutaverde.modelo.servicio.Correo;
import co.edu.uniquindio.rutaverde.modelo.servicio.FachadaReservas;
import co.edu.uniquindio.rutaverde.modelo.servicio.NotificadorCorreoSimulado;
import co.edu.uniquindio.rutaverde.modelo.servicio.ResultadoConfirmacion;
import co.edu.uniquindio.rutaverde.modelo.voucher.EmisorVoucher;
import co.edu.uniquindio.rutaverde.modelo.voucher.EmisorVoucherAsesor;
import co.edu.uniquindio.rutaverde.modelo.voucher.EmisorVoucherKiosco;
import co.edu.uniquindio.rutaverde.vista.UtilVista;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ReservasControlador implements Refrescable {

    private final Agencia agencia = ContextoAplicacion.getInstancia().getAgencia();
    private final FachadaReservas fachada = ContextoAplicacion.getInstancia().getFachadaReservas();
    private final NotificadorCorreoSimulado notificador = ContextoAplicacion.getInstancia().getNotificador();

    @FXML private ComboBox<String> cmbCanal;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<ProductoTuristico> cmbProducto;
    @FXML private DatePicker dpFecha;
    @FXML private Spinner<Integer> spnViajeros;
    @FXML private ComboBox<Guia> cmbGuia;
    @FXML private CheckBox chkTransporte;
    @FXML private CheckBox chkSeguro;
    @FXML private TextArea txtObservaciones;
    @FXML private TextField txtTarjeta;
    @FXML private Label lblResumen;
    @FXML private TextArea txtResultado;
    @FXML private TableView<Reserva> tblReservas;
    @FXML private TableColumn<Reserva, String> colNumero;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, String> colProducto;
    @FXML private TableColumn<Reserva, String> colFecha;
    @FXML private TableColumn<Reserva, Integer> colViajeros;
    @FXML private TableColumn<Reserva, String> colValor;

    @FXML
    private void initialize() {
        cmbCanal.getItems().addAll("Asesor", "Kiosco");
        cmbCanal.setValue("Asesor");
        spnViajeros.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 2));
        dpFecha.setValue(LocalDate.now().plusDays(7));
        cmbProducto.valueProperty().addListener((obs, anterior, p) -> mostrarCupos());
        dpFecha.valueProperty().addListener((obs, anterior, f) -> mostrarCupos());

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroFormateado"));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNombreCompleto()));
        colProducto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProducto().getNombre()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaViaje().format(Formatos.FECHA)));
        colViajeros.setCellValueFactory(new PropertyValueFactory<>("numeroViajeros"));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(Formatos.moneda(c.getValue().calcularValorTotal())));
        refrescar();
    }

    private Reserva construirReserva() {
        return new Reserva.Builder(cmbCliente.getValue(), cmbProducto.getValue(), dpFecha.getValue(), spnViajeros.getValue())
                .conGuia(cmbGuia.getValue())
                .conTransporte(chkTransporte.isSelected())
                .conSeguro(chkSeguro.isSelected())
                .conObservaciones(txtObservaciones.getText())
                .build();
    }

    @FXML
    private void onCalcular() {
        try {
            Reserva reserva = construirReserva();
            lblResumen.setText("Producto: " + Formatos.moneda(reserva.calcularValorProducto())
                    + "\nGuía: " + Formatos.moneda(reserva.calcularValorGuia())
                    + "\nTransporte: " + Formatos.moneda(reserva.calcularValorTransporte())
                    + "\nSeguro: " + Formatos.moneda(reserva.calcularValorSeguro())
                    + "\nTOTAL: " + Formatos.moneda(reserva.calcularValorTotal()));
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    @FXML
    private void onConfirmar() {
        try {
            Reserva reserva = construirReserva();
            EmisorVoucher emisor;
            if (cmbCanal.getValue().equals("Kiosco")) {
                emisor = new EmisorVoucherKiosco();
            } else {
                emisor = new EmisorVoucherAsesor();
            }
            ResultadoConfirmacion resultado = fachada.confirmarReserva(reserva, txtTarjeta.getText(), emisor);

            Correo correo = notificador.getUltimoEnviado();
            String estado = resultado.isExitosa() ? "CONFIRMADA" : "RECHAZADA";
            txtResultado.setText(estado + ": " + resultado.getMensaje()
                    + "\n\nCorreo enviado a " + correo.getDestinatario()
                    + "\nAsunto: " + correo.getAsunto()
                    + "\n\n" + correo.getMensaje());
            refrescar();
            mostrarCupos();
        } catch (RuntimeException e) {
            UtilVista.error(e.getMessage());
        }
    }

    private void mostrarCupos() {
        ProductoTuristico producto = cmbProducto.getValue();
        LocalDate fecha = dpFecha.getValue();
        if (producto == null || fecha == null) {
            lblResumen.setText("");
            return;
        }
        String texto = "Cupos libres el " + fecha.format(Formatos.FECHA) + ":";
        for (Actividad actividad : producto.obtenerActividades()) {
            texto += "\n  " + actividad.getNombre() + ": " + actividad.consultarCuposDisponibles(fecha);
        }
        lblResumen.setText(texto);
    }

    @FXML
    private void onQuitarGuia() {
        cmbGuia.setValue(null);
    }

    @FXML
    private void onLimpiar() {
        cmbCliente.setValue(null);
        cmbProducto.setValue(null);
        cmbGuia.setValue(null);
        dpFecha.setValue(LocalDate.now().plusDays(7));
        spnViajeros.getValueFactory().setValue(2);
        chkTransporte.setSelected(false);
        chkSeguro.setSelected(false);
        txtObservaciones.clear();
        txtTarjeta.clear();
        lblResumen.setText("");
        txtResultado.clear();
    }

    @Override
    public void refrescar() {
        Cliente cliente = cmbCliente.getValue();
        ProductoTuristico producto = cmbProducto.getValue();
        Guia guia = cmbGuia.getValue();
        cmbCliente.getItems().setAll(agencia.getClientes());
        cmbProducto.getItems().setAll(agencia.getProductos());
        cmbGuia.getItems().setAll(agencia.getGuias());
        cmbCliente.setValue(cliente);
        cmbProducto.setValue(producto);
        cmbGuia.setValue(guia);
        tblReservas.getItems().setAll(agencia.getReservasConfirmadas());
    }
}