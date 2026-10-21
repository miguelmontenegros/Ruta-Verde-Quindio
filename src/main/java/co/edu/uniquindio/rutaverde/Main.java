package co.edu.uniquindio.rutaverde;

import co.edu.uniquindio.rutaverde.modelo.Actividad;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Cliente;
import co.edu.uniquindio.rutaverde.modelo.Formatos;
import co.edu.uniquindio.rutaverde.modelo.Guia;
import co.edu.uniquindio.rutaverde.modelo.Paquete;
import co.edu.uniquindio.rutaverde.modelo.Reserva;
import co.edu.uniquindio.rutaverde.modelo.pago.ValidadorLuhn;
import co.edu.uniquindio.rutaverde.modelo.servicio.FachadaReservas;
import co.edu.uniquindio.rutaverde.modelo.servicio.ReporteReservas;
import co.edu.uniquindio.rutaverde.modelo.servicio.ResultadoConfirmacion;
import co.edu.uniquindio.rutaverde.modelo.voucher.EmisorVoucherAsesor;
import co.edu.uniquindio.rutaverde.modelo.voucher.EmisorVoucherKiosco;

import java.time.LocalDate;

public class Main {

    private static final String TARJETA_VALIDA = "4539 1488 0343 6467";
    private static final String TARJETA_INVALIDA = "4539 1488 0343 6468";

    public static void main(String[] args) {
        ContextoAplicacion contexto = ContextoAplicacion.getInstancia();
        Agencia agencia = contexto.getAgencia();
        FachadaReservas fachada = contexto.getFachadaReservas();
        LocalDate hoy = LocalDate.now();

        Cliente laura = agencia.buscarCliente("1094123456");
        Cliente john = agencia.buscarCliente("P-US998877");
        Guia carlos = agencia.buscarGuia("1094000111");
        Guia pedro = agencia.buscarGuia("1094000333");
        Paquete salento = (Paquete) agencia.buscarProducto("PAQ-01");
        Paquete quindioTotal = (Paquete) agencia.buscarProducto("PAQ-03");
        Paquete aventura = (Paquete) agencia.buscarProducto("PAQ-04");
        Actividad cocora = (Actividad) agencia.buscarProducto("ACT-01");
        Actividad aves = (Actividad) agencia.buscarProducto("ACT-03");

        System.out.println("\n=== RN01 Descuento del paquete entre 0 % y 20 % ===");
        probar("Paquete con 20 %", () -> new Paquete("PAQ-90", "Prueba", 20));
        probar("Paquete con 21 %", () -> new Paquete("PAQ-91", "Prueba", 21));

        System.out.println("\n=== RN02 Un paquete no puede contenerse a sí mismo ===");
        probar("Quindío Total dentro de sí mismo", () -> quindioTotal.agregar(quindioTotal));
        probar("Quindío Total dentro de Paquete Salento", () -> salento.agregar(quindioTotal));

        System.out.println("\n=== RN03 Un plan no incluye dos veces la misma actividad ===");
        probar("Cocora en Quindío Total (ya la trae Salento)", () -> quindioTotal.agregar(cocora));

        System.out.println("\n=== RF03 Consulta uniforme (Composite) ===");
        System.out.println("Actividad Cocora: " + Formatos.moneda(cocora.calcularPrecioPorPersona()) + ", "
                + Formatos.horas(cocora.calcularDuracionHoras()) + " h, " + cocora.obtenerActividades().size() + " actividad");
        System.out.println("Paquete Quindío Total: " + Formatos.moneda(quindioTotal.calcularPrecioPorPersona()) + ", "
                + Formatos.horas(quindioTotal.calcularDuracionHoras()) + " h, " + quindioTotal.obtenerActividades().size() + " actividades");

        System.out.println("\n=== RN04 Sin cliente o sin producto no hay reserva ===");
        probar("Reserva sin cliente", () -> new Reserva.Builder(null, cocora, hoy.plusDays(3), 2).build());
        probar("Reserva sin producto", () -> new Reserva.Builder(laura, null, hoy.plusDays(3), 2).build());

        System.out.println("\n=== RN05 La fecha del viaje no puede estar en el pasado ===");
        probar("Viaje para hoy", () -> new Reserva.Builder(laura, cocora, hoy, 2).build());
        probar("Viaje para ayer", () -> new Reserva.Builder(laura, cocora, hoy.minusDays(1), 2).build());

        System.out.println("\n=== RN06 Grupo de 1 a 15 viajeros ===");
        probar("15 viajeros", () -> new Reserva.Builder(laura, cocora, hoy.plusDays(3), 15).build());
        probar("16 viajeros", () -> new Reserva.Builder(laura, cocora, hoy.plusDays(3), 16).build());
        probar("0 viajeros", () -> new Reserva.Builder(laura, cocora, hoy.plusDays(3), 0).build());

        System.out.println("\n=== RN07 El guía habla el idioma del cliente ===");
        probar("John (Inglés) con Carlos (Español, Inglés)",
                () -> new Reserva.Builder(john, cocora, hoy.plusDays(3), 2).conGuia(carlos).build());
        probar("John (Inglés) con Pedro (Español)",
                () -> new Reserva.Builder(john, cocora, hoy.plusDays(3), 2).conGuia(pedro).build());

        System.out.println("\n=== RN08 No se reserva un producto con actividad suspendida ===");
        probar("Aventura Extrema (incluye Rafting suspendido)",
                () -> new Reserva.Builder(laura, aventura, hoy.plusDays(3), 2).build());

        System.out.println("\n=== RN09 y RN10 Días de guía y valor de la reserva ===");
        Reserva ejemplo = new Reserva.Builder(john, quindioTotal, hoy.plusDays(30), 2)
                .conGuia(carlos).conSeguro(true).build();
        System.out.println("Quindío Total dura " + Formatos.horas(quindioTotal.calcularDuracionHoras()) + " h -> "
                + carlos.calcularDiasCobro(quindioTotal.calcularDuracionHoras()) + " días de guía");
        System.out.println("Producto " + Formatos.moneda(ejemplo.calcularValorProducto())
                + " + guía " + Formatos.moneda(ejemplo.calcularValorGuia())
                + " + seguro " + Formatos.moneda(ejemplo.calcularValorSeguro())
                + " = " + Formatos.moneda(ejemplo.calcularValorTotal()));

        System.out.println("\n=== RN11 Algoritmo de Luhn ===");
        System.out.println(TARJETA_VALIDA + " -> " + (ValidadorLuhn.esValido(TARJETA_VALIDA) ? "válida" : "inválida"));
        System.out.println(TARJETA_INVALIDA + " -> " + (ValidadorLuhn.esValido(TARJETA_INVALIDA) ? "válida" : "inválida"));

        System.out.println("\n=== RN12 y RN13 Confirmación (Facade): si falla un paso no se descuenta ningún cupo ===");
        LocalDate fecha = hoy.plusDays(40);
        System.out.println("Cupos de Avistamiento de aves el " + fecha.format(Formatos.FECHA) + ": "
                + aves.consultarCuposDisponibles(fecha));
        confirmar(fachada, "Aves, 9 viajeros (cupo 8)",
                new Reserva.Builder(laura, aves, fecha, 9).build(), TARJETA_VALIDA);
        confirmar(fachada, "Aves, 2 viajeros, tarjeta inválida",
                new Reserva.Builder(laura, aves, fecha, 2).build(), TARJETA_INVALIDA);
        confirmar(fachada, "Quindío Total, 6 viajeros (más de $3.000.000)",
                new Reserva.Builder(laura, quindioTotal, fecha, 6).build(), TARJETA_VALIDA);
        System.out.println("Cupos de Avistamiento de aves después de los rechazos: " + aves.consultarCuposDisponibles(fecha));
        ResultadoConfirmacion exito = confirmar(fachada, "Aves, 2 viajeros, tarjeta válida",
                new Reserva.Builder(laura, aves, fecha, 2).build(), TARJETA_VALIDA);
        System.out.println("Cupos de Avistamiento de aves después de confirmar: " + aves.consultarCuposDisponibles(fecha));

        System.out.println("\n=== RN14 Voucher según el canal (Factory Method) ===");
        System.out.println("-- Asesor (detallado) --\n" + exito.getVoucher());
        ResultadoConfirmacion kiosco = fachada.confirmarReserva(
                new Reserva.Builder(laura, cocora, fecha, 1).build(), TARJETA_VALIDA, new EmisorVoucherKiosco());
        System.out.println("-- Kiosco (tiquete) --\n" + kiosco.getVoucher());

        System.out.println("\n=== RN15 Reporte del periodo (fechas incluidas) ===");
        ReporteReservas reporte = contexto.getServicioReportes().generarReporte(hoy, hoy.plusDays(40));
        System.out.println("Periodo " + hoy.format(Formatos.FECHA) + " a " + hoy.plusDays(40).format(Formatos.FECHA)
                + ": " + reporte.getReservasContadas() + " reservas, ingresos " + Formatos.moneda(reporte.getIngresos())
                + ", producto con más viajeros: " + reporte.getProductoMasViajeros().getNombre()
                + " (" + reporte.getViajerosProductoMasViajeros() + ")");

        System.out.println("\nCorreos enviados: " + contexto.getNotificador().getEnviados().size());
    }

    private static void probar(String caso, Runnable accion) {
        try {
            accion.run();
            System.out.println("[OK]    " + caso);
        } catch (RuntimeException e) {
            System.out.println("[Error] " + caso + ": " + e.getMessage());
        }
    }

    private static ResultadoConfirmacion confirmar(FachadaReservas fachada, String caso, Reserva reserva, String tarjeta) {
        ResultadoConfirmacion resultado = fachada.confirmarReserva(reserva, tarjeta, new EmisorVoucherAsesor());
        System.out.println((resultado.isExitosa() ? "[OK]    " : "[Error] ") + caso + ": " + resultado.getMensaje());
        return resultado;
    }
}