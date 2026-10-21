package co.edu.uniquindio.rutaverde;

import co.edu.uniquindio.rutaverde.modelo.Actividad;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Cliente;
import co.edu.uniquindio.rutaverde.modelo.EstadoActividad;
import co.edu.uniquindio.rutaverde.modelo.Guia;
import co.edu.uniquindio.rutaverde.modelo.NivelDificultad;
import co.edu.uniquindio.rutaverde.modelo.Paquete;
import co.edu.uniquindio.rutaverde.modelo.Reserva;
import co.edu.uniquindio.rutaverde.modelo.voucher.EmisorVoucherAsesor;
import co.edu.uniquindio.rutaverde.modelo.voucher.EmisorVoucherKiosco;

import java.time.LocalDate;
import java.util.List;

public final class DatosIniciales {

    public static final String TARJETA_VALIDA = "4539 1488 0343 6467";

    private DatosIniciales() {
    }

    public static void cargar(ContextoAplicacion contexto) {
        Agencia agencia = contexto.getAgencia();

        Cliente laura = new Cliente("1094123456", "Laura Gómez Restrepo", "3104567890",
                "laura.gomez@correo.com", "Colombia", "Español");
        Cliente john = new Cliente("P-US998877", "John Miller", "+1 512 555 0199",
                "john.miller@mail.com", "Estados Unidos", "Inglés");
        Cliente amelie = new Cliente("P-FR445566", "Amélie Durand", "+33 6 12 34 56 78",
                "amelie.durand@mail.fr", "Francia", "Francés");
        agencia.registrarCliente(laura);
        agencia.registrarCliente(john);
        agencia.registrarCliente(amelie);

        Guia carlos = new Guia("1094000111", "Carlos Arango", List.of("Español", "Inglés"), "3201112233", 180_000);
        Guia marie = new Guia("1094000222", "Marie Ospina", List.of("Español", "Francés", "Inglés"), "3154445566", 220_000);
        Guia pedro = new Guia("1094000333", "Pedro Cardona", List.of("Español"), "3007778899", 150_000);
        agencia.registrarGuia(carlos);
        agencia.registrarGuia(marie);
        agencia.registrarGuia(pedro);

        Actividad cocora = new Actividad("ACT-01", "Caminata Valle de Cocora", "Salento", 4, 120_000, 20,
                NivelDificultad.MEDIA, EstadoActividad.DISPONIBLE);
        Actividad finca = new Actividad("ACT-02", "Recorrido finca cafetera", "Salento", 3, 95_000, 15,
                NivelDificultad.BAJA, EstadoActividad.DISPONIBLE);
        Actividad aves = new Actividad("ACT-03", "Avistamiento de aves", "Filandia", 5, 140_000, 8,
                NivelDificultad.BAJA, EstadoActividad.DISPONIBLE);
        Actividad parqueCafe = new Actividad("ACT-04", "Parque del Café", "Montenegro", 8, 110_000, 100,
                NivelDificultad.BAJA, EstadoActividad.DISPONIBLE);
        Actividad panaca = new Actividad("ACT-05", "Panaca", "Quimbaya", 7, 100_000, 80,
                NivelDificultad.BAJA, EstadoActividad.DISPONIBLE);
        Actividad rafting = new Actividad("ACT-06", "Rafting río La Vieja", "Alcalá", 4, 160_000, 12,
                NivelDificultad.ALTA, EstadoActividad.SUSPENDIDA);
        for (Actividad actividad : List.of(cocora, finca, aves, parqueCafe, panaca, rafting)) {
            agencia.registrarActividad(actividad);
        }

        Paquete salento = new Paquete("PAQ-01", "Paquete Salento", 10);
        salento.agregar(cocora);
        salento.agregar(finca);

        Paquete parques = new Paquete("PAQ-02", "Paquete Parques", 8);
        parques.agregar(parqueCafe);
        parques.agregar(panaca);

        Paquete quindioTotal = new Paquete("PAQ-03", "Quindío Total", 5);
        quindioTotal.agregar(salento);
        quindioTotal.agregar(parques);
        quindioTotal.agregar(aves);

        Paquete aventura = new Paquete("PAQ-04", "Aventura Extrema", 12);
        aventura.agregar(rafting);
        aventura.agregar(cocora);

        for (Paquete paquete : List.of(salento, parques, quindioTotal, aventura)) {
            agencia.registrarPaquete(paquete);
        }

        LocalDate hoy = LocalDate.now();
        contexto.getFachadaReservas().confirmarReserva(
                new Reserva.Builder(laura, salento, hoy.plusDays(5), 4).conTransporte(true).build(),
                TARJETA_VALIDA, new EmisorVoucherAsesor());
        contexto.getFachadaReservas().confirmarReserva(
                new Reserva.Builder(john, quindioTotal, hoy.plusDays(12), 2).conGuia(carlos).conSeguro(true)
                        .conObservaciones("Vegetariano").build(),
                TARJETA_VALIDA, new EmisorVoucherAsesor());
        contexto.getFachadaReservas().confirmarReserva(
                new Reserva.Builder(amelie, parqueCafe, hoy.plusDays(20), 3).build(),
                TARJETA_VALIDA, new EmisorVoucherKiosco());
    }
}