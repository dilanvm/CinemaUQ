package edu.uniquindio.cinemauq.app;

import edu.uniquindio.cinemauq.application.AcumuladorPuntos;
import edu.uniquindio.cinemauq.application.NotificadorCompra;
import edu.uniquindio.cinemauq.application.PlantillaFuncion;
import edu.uniquindio.cinemauq.application.PublicadorCompras;
import edu.uniquindio.cinemauq.application.ReembolsoCancelacionCliente;
import edu.uniquindio.cinemauq.application.ServicioAutenticacion;
import edu.uniquindio.cinemauq.application.ServicioCancelacion;
import edu.uniquindio.cinemauq.application.ServicioCatalogo;
import edu.uniquindio.cinemauq.application.ServicioCompra;
import edu.uniquindio.cinemauq.application.ServicioConfiteria;
import edu.uniquindio.cinemauq.application.ServicioDisponibilidad;
import edu.uniquindio.cinemauq.application.ServicioEstadisticas;
import edu.uniquindio.cinemauq.application.ServicioPuntos;
import edu.uniquindio.cinemauq.application.ServicioReportes;
import edu.uniquindio.cinemauq.application.ServicioTarjeta;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.RepositorioConfiteria;
import edu.uniquindio.cinemauq.domain.RepositorioFunciones;
import edu.uniquindio.cinemauq.domain.RepositorioPeliculas;
import edu.uniquindio.cinemauq.domain.RepositorioSalas;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;
import edu.uniquindio.cinemauq.infrastructure.DatosIniciales;
import edu.uniquindio.cinemauq.infrastructure.RepositorioComprasEnMemoria;
import edu.uniquindio.cinemauq.infrastructure.RepositorioConfiteriaEnMemoria;
import edu.uniquindio.cinemauq.infrastructure.RepositorioFuncionesEnMemoria;
import edu.uniquindio.cinemauq.infrastructure.RepositorioPeliculasEnMemoria;
import edu.uniquindio.cinemauq.infrastructure.RepositorioSalasEnMemoria;
import edu.uniquindio.cinemauq.infrastructure.RepositorioUsuariosEnMemoria;
import edu.uniquindio.cinemauq.presentation.ContextoApp;
import edu.uniquindio.cinemauq.presentation.Navegador;
import javafx.application.Application;
import javafx.stage.Stage;

import java.time.Clock;
import java.time.ZoneId;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        Clock clock = Clock.system(ZoneId.of("America/Bogota"));

        RepositorioUsuarios repositorioUsuarios = new RepositorioUsuariosEnMemoria();
        RepositorioPeliculas repositorioPeliculas = new RepositorioPeliculasEnMemoria();
        RepositorioSalas repositorioSalas = new RepositorioSalasEnMemoria();
        RepositorioFunciones repositorioFunciones = new RepositorioFuncionesEnMemoria();
        RepositorioConfiteria repositorioConfiteria = new RepositorioConfiteriaEnMemoria();
        RepositorioCompras repositorioCompras = new RepositorioComprasEnMemoria();

        PublicadorCompras publicadorCompras = new PublicadorCompras();
        AcumuladorPuntos acumuladorPuntos = new AcumuladorPuntos(clock);
        NotificadorCompra notificadorCompra = new NotificadorCompra();
        publicadorCompras.registrarObservador(acumuladorPuntos);
        publicadorCompras.registrarObservador(notificadorCompra);

        ServicioDisponibilidad servicioDisponibilidad = new ServicioDisponibilidad();
        ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion(repositorioUsuarios);
        ServicioCatalogo servicioCatalogo = new ServicioCatalogo(repositorioPeliculas, repositorioSalas, repositorioFunciones, repositorioCompras, clock);
        ServicioConfiteria servicioConfiteria = new ServicioConfiteria(repositorioConfiteria);
        ServicioCompra servicioCompra = new ServicioCompra(repositorioCompras, servicioDisponibilidad, publicadorCompras, clock);
        ServicioCancelacion servicioCancelacion = new ServicioCancelacion(repositorioCompras, servicioDisponibilidad, publicadorCompras, new ReembolsoCancelacionCliente(), clock);
        ServicioTarjeta servicioTarjeta = new ServicioTarjeta();
        ServicioPuntos servicioPuntos = new ServicioPuntos();
        ServicioReportes servicioReportes = new ServicioReportes(repositorioCompras, repositorioPeliculas, repositorioSalas, repositorioFunciones, repositorioUsuarios, clock);
        ServicioEstadisticas servicioEstadisticas = new ServicioEstadisticas(repositorioCompras, repositorioPeliculas, repositorioSalas, repositorioFunciones, repositorioUsuarios);

        PlantillaFuncion plantilla = DatosIniciales.cargarTodo(
                repositorioUsuarios,
                repositorioPeliculas,
                repositorioSalas,
                repositorioFunciones,
                repositorioConfiteria,
                repositorioCompras,
                servicioDisponibilidad,
                publicadorCompras,
                clock
        );

        ContextoApp contexto = new ContextoApp(
                clock,
                repositorioUsuarios,
                repositorioCompras,
                repositorioPeliculas,
                repositorioSalas,
                repositorioFunciones,
                repositorioConfiteria,
                servicioAutenticacion,
                servicioCatalogo,
                servicioConfiteria,
                servicioDisponibilidad,
                servicioCompra,
                servicioCancelacion,
                servicioTarjeta,
                servicioPuntos,
                servicioReportes,
                servicioEstadisticas,
                publicadorCompras,
                notificadorCompra,
                acumuladorPuntos,
                plantilla
        );

        Navegador navegador = new Navegador(primaryStage, contexto);
        navegador.navegarALogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
