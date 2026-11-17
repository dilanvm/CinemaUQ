package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.RepositorioFunciones;
import edu.uniquindio.cinemauq.domain.RepositorioPeliculas;
import edu.uniquindio.cinemauq.domain.RepositorioSalas;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

public class ServicioReportes {
    private final RepositorioCompras repositorioCompras;
    private final RepositorioPeliculas repositorioPeliculas;
    private final RepositorioSalas repositorioSalas;
    private final RepositorioFunciones repositorioFunciones;
    private final RepositorioUsuarios repositorioUsuarios;
    private final Clock clock;
    private final ExportadorReporte exportadorTxt;
    private final ExportadorReporte exportadorPdf;

    public ServicioReportes(RepositorioCompras repositorioCompras,
                            RepositorioPeliculas repositorioPeliculas,
                            RepositorioSalas repositorioSalas,
                            RepositorioFunciones repositorioFunciones,
                            RepositorioUsuarios repositorioUsuarios) {
        this(repositorioCompras, repositorioPeliculas, repositorioSalas, repositorioFunciones, repositorioUsuarios, Clock.system(ZoneId.of("America/Bogota")));
    }

    public ServicioReportes(RepositorioCompras repositorioCompras,
                            RepositorioPeliculas repositorioPeliculas,
                            RepositorioSalas repositorioSalas,
                            RepositorioFunciones repositorioFunciones,
                            RepositorioUsuarios repositorioUsuarios,
                            Clock clock) {
        if (repositorioCompras == null || repositorioPeliculas == null || repositorioSalas == null || repositorioFunciones == null || repositorioUsuarios == null) {
            throw new IllegalArgumentException("Todos los repositorios son obligatorios");
        }
        this.repositorioCompras = repositorioCompras;
        this.repositorioPeliculas = repositorioPeliculas;
        this.repositorioSalas = repositorioSalas;
        this.repositorioFunciones = repositorioFunciones;
        this.repositorioUsuarios = repositorioUsuarios;
        this.clock = clock != null ? clock : Clock.system(ZoneId.of("America/Bogota"));
        this.exportadorTxt = new ExportadorTxt();
        this.exportadorPdf = new ExportadorPdf();
    }

    public FabricaReporte obtenerFabrica(TipoReporte tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de reporte es obligatorio");
        }
        return switch (tipo) {
            case VENTAS -> new FabricaReporteVentas(repositorioCompras, repositorioPeliculas, clock);
            case ENTRADAS_POR_PELICULA -> new FabricaReporteEntradasPorPelicula(repositorioCompras, repositorioPeliculas, repositorioFunciones, clock);
            case OCUPACION_SALAS -> new FabricaReporteOcupacionSalas(repositorioCompras, repositorioSalas, repositorioFunciones, clock);
            case PRODUCTOS_VENDIDOS -> new FabricaReporteProductosVendidos(repositorioCompras, clock);
            case RECARGAS -> new FabricaReporteRecargas(repositorioUsuarios, clock);
            case CANCELACIONES_REEMBOLSOS -> new FabricaReporteCancelacionesReembolsos(repositorioCompras, clock);
            case PUNTOS -> new FabricaReportePuntos(repositorioUsuarios, clock);
        };
    }

    public Reporte generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        FabricaReporte fabrica = obtenerFabrica(tipo);
        return fabrica.crearReporte(desde, hasta);
    }

    public void exportarTxt(Reporte reporte, Path destino) throws IOException {
        exportadorTxt.exportar(reporte, destino);
    }

    public void exportarPdf(Reporte reporte, Path destino) throws IOException {
        exportadorPdf.exportar(reporte, destino);
    }
}
