package edu.uniquindio.cinemauq.presentation;

import edu.uniquindio.cinemauq.application.AcumuladorPuntos;
import edu.uniquindio.cinemauq.application.NotificadorCompra;
import edu.uniquindio.cinemauq.application.PlantillaFuncion;
import edu.uniquindio.cinemauq.application.PublicadorCompras;
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

import java.time.Clock;

public class ContextoApp {
    private final Clock clock;
    private final RepositorioUsuarios repositorioUsuarios;
    private final RepositorioCompras repositorioCompras;
    private final RepositorioPeliculas repositorioPeliculas;
    private final RepositorioSalas repositorioSalas;
    private final RepositorioFunciones repositorioFunciones;
    private final RepositorioConfiteria repositorioConfiteria;
    private final ServicioAutenticacion servicioAutenticacion;
    private final ServicioCatalogo servicioCatalogo;
    private final ServicioConfiteria servicioConfiteria;
    private final ServicioDisponibilidad servicioDisponibilidad;
    private final ServicioCompra servicioCompra;
    private final ServicioCancelacion servicioCancelacion;
    private final ServicioTarjeta servicioTarjeta;
    private final ServicioPuntos servicioPuntos;
    private final ServicioReportes servicioReportes;
    private final ServicioEstadisticas servicioEstadisticas;
    private final PublicadorCompras publicadorCompras;
    private final NotificadorCompra notificadorCompra;
    private final AcumuladorPuntos acumuladorPuntos;
    private PlantillaFuncion plantillaFuncion;

    public ContextoApp(Clock clock,
                       RepositorioUsuarios repositorioUsuarios,
                       RepositorioCompras repositorioCompras,
                       RepositorioPeliculas repositorioPeliculas,
                       RepositorioSalas repositorioSalas,
                       RepositorioFunciones repositorioFunciones,
                       RepositorioConfiteria repositorioConfiteria,
                       ServicioAutenticacion servicioAutenticacion,
                       ServicioCatalogo servicioCatalogo,
                       ServicioConfiteria servicioConfiteria,
                       ServicioDisponibilidad servicioDisponibilidad,
                       ServicioCompra servicioCompra,
                       ServicioCancelacion servicioCancelacion,
                       ServicioTarjeta servicioTarjeta,
                       ServicioPuntos servicioPuntos,
                       ServicioReportes servicioReportes,
                       ServicioEstadisticas servicioEstadisticas,
                       PublicadorCompras publicadorCompras,
                       NotificadorCompra notificadorCompra,
                       AcumuladorPuntos acumuladorPuntos,
                       PlantillaFuncion plantillaFuncion) {
        this.clock = clock;
        this.repositorioUsuarios = repositorioUsuarios;
        this.repositorioCompras = repositorioCompras;
        this.repositorioPeliculas = repositorioPeliculas;
        this.repositorioSalas = repositorioSalas;
        this.repositorioFunciones = repositorioFunciones;
        this.repositorioConfiteria = repositorioConfiteria;
        this.servicioAutenticacion = servicioAutenticacion;
        this.servicioCatalogo = servicioCatalogo;
        this.servicioConfiteria = servicioConfiteria;
        this.servicioDisponibilidad = servicioDisponibilidad;
        this.servicioCompra = servicioCompra;
        this.servicioCancelacion = servicioCancelacion;
        this.servicioTarjeta = servicioTarjeta;
        this.servicioPuntos = servicioPuntos;
        this.servicioReportes = servicioReportes;
        this.servicioEstadisticas = servicioEstadisticas;
        this.publicadorCompras = publicadorCompras;
        this.notificadorCompra = notificadorCompra;
        this.acumuladorPuntos = acumuladorPuntos;
        this.plantillaFuncion = plantillaFuncion;
    }

    public Clock getClock() {
        return clock;
    }

    public RepositorioUsuarios getRepositorioUsuarios() {
        return repositorioUsuarios;
    }

    public RepositorioCompras getRepositorioCompras() {
        return repositorioCompras;
    }

    public RepositorioPeliculas getRepositorioPeliculas() {
        return repositorioPeliculas;
    }

    public RepositorioSalas getRepositorioSalas() {
        return repositorioSalas;
    }

    public RepositorioFunciones getRepositorioFunciones() {
        return repositorioFunciones;
    }

    public RepositorioConfiteria getRepositorioConfiteria() {
        return repositorioConfiteria;
    }

    public ServicioAutenticacion getServicioAutenticacion() {
        return servicioAutenticacion;
    }

    public ServicioCatalogo getServicioCatalogo() {
        return servicioCatalogo;
    }

    public ServicioConfiteria getServicioConfiteria() {
        return servicioConfiteria;
    }

    public ServicioDisponibilidad getServicioDisponibilidad() {
        return servicioDisponibilidad;
    }

    public ServicioCompra getServicioCompra() {
        return servicioCompra;
    }

    public ServicioCancelacion getServicioCancelacion() {
        return servicioCancelacion;
    }

    public ServicioTarjeta getServicioTarjeta() {
        return servicioTarjeta;
    }

    public ServicioPuntos getServicioPuntos() {
        return servicioPuntos;
    }

    public ServicioReportes getServicioReportes() {
        return servicioReportes;
    }

    public ServicioEstadisticas getServicioEstadisticas() {
        return servicioEstadisticas;
    }

    public PublicadorCompras getPublicadorCompras() {
        return publicadorCompras;
    }

    public NotificadorCompra getNotificadorCompra() {
        return notificadorCompra;
    }

    public AcumuladorPuntos getAcumuladorPuntos() {
        return acumuladorPuntos;
    }

    public PlantillaFuncion getPlantillaFuncion() {
        return plantillaFuncion;
    }

    public void setPlantillaFuncion(PlantillaFuncion plantillaFuncion) {
        this.plantillaFuncion = plantillaFuncion;
    }
}
