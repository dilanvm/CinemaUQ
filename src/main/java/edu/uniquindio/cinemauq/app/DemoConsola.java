package edu.uniquindio.cinemauq.app;

import edu.uniquindio.cinemauq.application.AcumuladorPuntos;
import edu.uniquindio.cinemauq.application.CompraBuilder;
import edu.uniquindio.cinemauq.application.NotificadorCompra;
import edu.uniquindio.cinemauq.application.PublicadorCompras;
import edu.uniquindio.cinemauq.application.ReembolsoCancelacionCliente;
import edu.uniquindio.cinemauq.application.ServicioAutenticacion;
import edu.uniquindio.cinemauq.application.ServicioCancelacion;
import edu.uniquindio.cinemauq.application.ServicioCompra;
import edu.uniquindio.cinemauq.application.ServicioDisponibilidad;
import edu.uniquindio.cinemauq.application.ServicioPuntos;
import edu.uniquindio.cinemauq.application.ServicioTarjeta;
import edu.uniquindio.cinemauq.domain.Administrador;
import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.EstadoFuncion;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.Producto;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.TipoAsiento;
import edu.uniquindio.cinemauq.infrastructure.DatosIniciales;
import edu.uniquindio.cinemauq.infrastructure.RepositorioComprasEnMemoria;
import edu.uniquindio.cinemauq.infrastructure.RepositorioUsuariosEnMemoria;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class DemoConsola {
    public static void main(String[] args) {
        Clock clock = Clock.system(ZoneId.of("America/Bogota"));
        RepositorioUsuarios repositorioUsuarios = new RepositorioUsuariosEnMemoria();
        RepositorioCompras repositorioCompras = new RepositorioComprasEnMemoria();

        DatosIniciales.cargar(repositorioUsuarios, clock);

        ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion(repositorioUsuarios);
        ServicioTarjeta servicioTarjeta = new ServicioTarjeta();
        ServicioDisponibilidad servicioDisponibilidad = new ServicioDisponibilidad();
        ServicioPuntos servicioPuntos = new ServicioPuntos();

        PublicadorCompras publicadorCompras = new PublicadorCompras();
        AcumuladorPuntos acumuladorPuntos = new AcumuladorPuntos(clock);
        NotificadorCompra notificadorCompra = new NotificadorCompra();
        publicadorCompras.registrarObservador(acumuladorPuntos);
        publicadorCompras.registrarObservador(notificadorCompra);

        ServicioCompra servicioCompra = new ServicioCompra(repositorioCompras, servicioDisponibilidad, publicadorCompras, clock);
        ServicioCancelacion servicioCancelacion = new ServicioCancelacion(
                repositorioCompras,
                servicioDisponibilidad,
                publicadorCompras,
                new ReembolsoCancelacionCliente(),
                clock
        );

        Cliente cliente = servicioAutenticacion.registrar("Dilan Ruiz", "dilan@correo.com", "clave123");
        Administrador admin = (Administrador) servicioAutenticacion.iniciarSesion("admin@cinemauq.com", "admin123");

        servicioTarjeta.recargar(admin, cliente, new BigDecimal("60000.00"));
        System.out.println("Saldo tras recarga: " + servicioTarjeta.consultarSaldo(cliente));

        Pelicula pelicula = new Pelicula(1L, "Interestelar", "Ciencia ficcion", 169, "+12", "Sci-Fi");
        Sala sala = new Sala("Sala 1", 1L, new ArrayList<>(), true);
        Asiento asientoA1 = new Asiento(1L, "A", 1, TipoAsiento.GENERAL);
        Asiento asientoA2 = new Asiento(2L, "A", 2, TipoAsiento.PREFERENCIAL);
        sala.agregarAsiento(asientoA1);
        sala.agregarAsiento(asientoA2);

        LocalDateTime horarioFuturo = LocalDateTime.now(clock).plusDays(2);
        Funcion funcion = new Funcion(1L, pelicula, sala, horarioFuturo, new BigDecimal("12000.00"), EstadoFuncion.PROGRAMADA);

        Producto gaseosa = new Producto("Gaseosa Mediana", "Bebida", new BigDecimal("5000.00"), true);

        Compra compra = new CompraBuilder(cliente, clock)
                .agregarEntrada(funcion, asientoA1)
                .agregarElemento(gaseosa, 1)
                .usarPuntos(0)
                .construir();

        Compra compraRealizada = servicioCompra.comprar(compra);
        System.out.println("Compra confirmada id: " + compraRealizada.getId());
        System.out.println("Total pagado: " + compraRealizada.getTotal());
        System.out.println("Saldo tarjeta restante: " + servicioTarjeta.consultarSaldo(cliente));
        System.out.println("Puntos vigentes cliente: " + servicioPuntos.consultarSaldo(cliente, LocalDate.now(clock)));

        servicioCancelacion.cancelarPorCliente(compraRealizada);
        System.out.println("Compra cancelada exitosamente");
        System.out.println("Saldo tarjeta tras reembolso: " + servicioTarjeta.consultarSaldo(cliente));
        System.out.println("Puntos vigentes cliente tras cancelacion: " + servicioPuntos.consultarSaldo(cliente, LocalDate.now(clock)));
        System.out.println("Notificaciones del cliente: " + notificadorCompra.obtenerNotificaciones(cliente));
    }
}
