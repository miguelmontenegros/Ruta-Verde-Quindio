package co.edu.uniquindio.rutaverde.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Agencia {

    private final String nombreComercial;
    private final String nit;
    private final String rnt;
    private final String direccion;
    private final String telefono;
    private final String correo;
    private final String paginaWeb;

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Guia> guias = new ArrayList<>();
    private final List<Actividad> actividades = new ArrayList<>();
    private final List<Paquete> paquetes = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private int consecutivoReservas = 0;

    public Agencia(String nombreComercial, String nit, String rnt, String direccion,
                   String telefono, String correo, String paginaWeb) {
        this.nombreComercial = nombreComercial;
        this.nit = nit;
        this.rnt = rnt;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.paginaWeb = paginaWeb;
    }

    public void registrarCliente(Cliente cliente) {
        if (buscarCliente(cliente.getDocumento()) != null) {
            throw new ReglaNegocioException("Ya existe un cliente con documento " + cliente.getDocumento());
        }
        clientes.add(cliente);
    }

    public Cliente buscarCliente(String documento) {
        for (Cliente cliente : clientes) {
            if (cliente.getDocumento().equalsIgnoreCase(documento.trim())) {
                return cliente;
            }
        }
        return null;
    }

    public void actualizarCliente(String documento, String nombre, String telefono, String correo,
                                  String pais, String idioma) {
        obtenerCliente(documento).actualizarDatos(nombre, telefono, correo, pais, idioma);
    }

    public void eliminarCliente(String documento) {
        Cliente cliente = obtenerCliente(documento);
        if (!cliente.getReservas().isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar un cliente con reservas registradas");
        }
        clientes.remove(cliente);
    }

    private Cliente obtenerCliente(String documento) {
        Cliente cliente = buscarCliente(documento);
        if (cliente == null) {
            throw new ReglaNegocioException("No existe un cliente con documento " + documento);
        }
        return cliente;
    }

    public void registrarGuia(Guia guia) {
        if (buscarGuia(guia.getIdentificacion()) != null) {
            throw new ReglaNegocioException("Ya existe un guía con identificación " + guia.getIdentificacion());
        }
        guias.add(guia);
    }

    public Guia buscarGuia(String identificacion) {
        for (Guia guia : guias) {
            if (guia.getIdentificacion().equalsIgnoreCase(identificacion.trim())) {
                return guia;
            }
        }
        return null;
    }

    public void actualizarGuia(String identificacion, String nombre, List<String> idiomas,
                               String telefono, double tarifaDia) {
        obtenerGuia(identificacion).actualizarDatos(nombre, idiomas, telefono, tarifaDia);
    }

    public void eliminarGuia(String identificacion) {
        Guia guia = obtenerGuia(identificacion);
        for (Reserva reserva : reservas) {
            if (guia.equals(reserva.getGuia())) {
                throw new ReglaNegocioException("No se puede eliminar un guía asignado a reservas");
            }
        }
        guias.remove(guia);
    }

    private Guia obtenerGuia(String identificacion) {
        Guia guia = buscarGuia(identificacion);
        if (guia == null) {
            throw new ReglaNegocioException("No existe un guía con identificación " + identificacion);
        }
        return guia;
    }


    public ProductoTuristico buscarProducto(String codigo) {
        for (ProductoTuristico producto : getProductos()) {
            if (producto.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return producto;
            }
        }
        return null;
    }

    public void registrarActividad(Actividad actividad) {
        validarCodigoLibre(actividad.getCodigo());
        actividades.add(actividad);
    }

    public void actualizarActividad(String codigo, String nombre, String municipio, double duracionHoras,
                                    double precioPorPersona, int cupoMaximoDia, NivelDificultad dificultad,
                                    EstadoActividad estado) {
        Actividad actividad = obtenerActividad(codigo);
        actividad.actualizarDatos(nombre, municipio, duracionHoras, precioPorPersona, cupoMaximoDia, dificultad, estado);
    }

    public void eliminarActividad(String codigo) {
        Actividad actividad = obtenerActividad(codigo);
        validarProductoSinUso(actividad);
        actividades.remove(actividad);
    }

    private Actividad obtenerActividad(String codigo) {
        for (Actividad actividad : actividades) {
            if (actividad.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return actividad;
            }
        }
        throw new ReglaNegocioException("No existe una actividad con código " + codigo);
    }

    public void registrarPaquete(Paquete paquete) {
        validarCodigoLibre(paquete.getCodigo());
        paquetes.add(paquete);
    }

    public void eliminarPaquete(String codigo) {
        Paquete paquete = null;
        for (Paquete p : paquetes) {
            if (p.getCodigo().equalsIgnoreCase(codigo.trim())) {
                paquete = p;
            }
        }
        if (paquete == null) {
            throw new ReglaNegocioException("No existe un paquete con código " + codigo);
        }
        validarProductoSinUso(paquete);
        paquetes.remove(paquete);
    }

    private void validarCodigoLibre(String codigo) {
        if (buscarProducto(codigo) != null) {
            throw new ReglaNegocioException("Ya existe un producto con código " + codigo);
        }
    }

    private void validarProductoSinUso(ProductoTuristico producto) {
        for (Paquete paquete : paquetes) {
            if (paquete != producto && paquete.contiene(producto)) {
                throw new ReglaNegocioException("'" + producto.getNombre() + "' hace parte del paquete '"
                        + paquete.getNombre() + "'");
            }
        }
        for (Reserva reserva : reservas) {
            if (reserva.getProducto().contiene(producto)) {
                throw new ReglaNegocioException("'" + producto.getNombre() + "' tiene reservas registradas");
            }
        }
    }

    public void registrarReserva(Reserva reserva) {
        consecutivoReservas++;
        reserva.asignarNumero(consecutivoReservas);
        reservas.add(reserva);
        reserva.getCliente().agregarReserva(reserva);
    }

    public void anularRegistro(Reserva reserva) {
        if (reservas.remove(reserva)) {
            reserva.getCliente().quitarReserva(reserva);
        }
        reserva.revertirConfirmacion();
    }

    public List<Reserva> getReservasConfirmadas() {
        List<Reserva> confirmadas = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.estaConfirmada()) {
                confirmadas.add(reserva);
            }
        }
        return confirmadas;
    }

    public List<ProductoTuristico> getProductos() {
        List<ProductoTuristico> productos = new ArrayList<>(actividades);
        productos.addAll(paquetes);
        return productos;
    }

    public List<Cliente> getClientes() { return Collections.unmodifiableList(clientes); }
    public List<Guia> getGuias() { return Collections.unmodifiableList(guias); }
    public List<Actividad> getActividades() { return Collections.unmodifiableList(actividades); }
    public List<Paquete> getPaquetes() { return Collections.unmodifiableList(paquetes); }
    public List<Reserva> getReservas() { return Collections.unmodifiableList(reservas); }

    public String getNombreComercial() { return nombreComercial; }
    public String getNit() { return nit; }
    public String getRnt() { return rnt; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getPaginaWeb() { return paginaWeb; }
}
