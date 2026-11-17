package edu.uniquindio.cinemauq;

import edu.uniquindio.cinemauq.application.ExportadorPdf;
import edu.uniquindio.cinemauq.application.ExportadorTxt;
import edu.uniquindio.cinemauq.application.FabricaReporte;
import edu.uniquindio.cinemauq.application.PublicadorCompras;
import edu.uniquindio.cinemauq.application.Reporte;
import edu.uniquindio.cinemauq.application.ServicioDisponibilidad;
import edu.uniquindio.cinemauq.application.ServicioReportes;
import edu.uniquindio.cinemauq.application.TipoReporte;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportesTest {
    private Clock fixedClock;
    private ServicioReportes servicioReportes;
    private LocalDate desde;
    private LocalDate hasta;

    @BeforeEach
    public void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-11-15T10:00:00Z"), ZoneId.of("America/Bogota"));
        desde = LocalDate.now(fixedClock).minusDays(30);
        hasta = LocalDate.now(fixedClock).plusDays(30);

        RepositorioUsuarios repositorioUsuarios = new RepositorioUsuariosEnMemoria();
        RepositorioPeliculas repositorioPeliculas = new RepositorioPeliculasEnMemoria();
        RepositorioSalas repositorioSalas = new RepositorioSalasEnMemoria();
        RepositorioFunciones repositorioFunciones = new RepositorioFuncionesEnMemoria();
        RepositorioConfiteria repositorioConfiteria = new RepositorioConfiteriaEnMemoria();
        RepositorioCompras repositorioCompras = new RepositorioComprasEnMemoria();

        ServicioDisponibilidad servicioDisponibilidad = new ServicioDisponibilidad();
        PublicadorCompras publicadorCompras = new PublicadorCompras();

        DatosIniciales.cargarTodo(
                repositorioUsuarios,
                repositorioPeliculas,
                repositorioSalas,
                repositorioFunciones,
                repositorioConfiteria,
                repositorioCompras,
                servicioDisponibilidad,
                publicadorCompras,
                fixedClock
        );

        servicioReportes = new ServicioReportes(
                repositorioCompras,
                repositorioPeliculas,
                repositorioSalas,
                repositorioFunciones,
                repositorioUsuarios,
                fixedClock
        );
    }

    @Test
    public void testCreacionSieteReportes() {
        setUp();
        for (TipoReporte tipo : TipoReporte.values()) {
            FabricaReporte fabrica = servicioReportes.obtenerFabrica(tipo);
            assertNotNull(fabrica);
            Reporte reporte = fabrica.crearReporte(desde, hasta);
            assertNotNull(reporte);
            assertNotNull(reporte.getTitulo());
            assertNotNull(reporte.getColumnas());
            assertFalse(reporte.getColumnas().isEmpty());
            assertNotNull(reporte.getFilas());
            assertNotNull(reporte.getTotales());
            assertFalse(reporte.getTotales().isEmpty());
        }
    }

    @Test
    public void testExportarTxt(@TempDir Path tempDir) throws IOException {
        setUp();
        Reporte reporteVentas = servicioReportes.generarReporte(TipoReporte.VENTAS, desde, hasta);
        Path rutaTxt = tempDir.resolve("reporte_ventas.txt");

        ExportadorTxt exportadorTxt = new ExportadorTxt();
        exportadorTxt.exportar(reporteVentas, rutaTxt);

        assertTrue(Files.exists(rutaTxt));
        String contenido = Files.readString(rutaTxt);
        assertTrue(contenido.contains("Reporte de ventas - CinemaUQ"));
        assertTrue(contenido.contains("Pelicula"));
        assertTrue(contenido.contains("Total ventas"));
    }

    @Test
    public void testExportarPdf(@TempDir Path tempDir) throws IOException {
        setUp();
        Reporte reporteVentas = servicioReportes.generarReporte(TipoReporte.VENTAS, desde, hasta);
        Path rutaPdf = tempDir.resolve("reporte_ventas.pdf");

        ExportadorPdf exportadorPdf = new ExportadorPdf();
        exportadorPdf.exportar(reporteVentas, rutaPdf);

        assertTrue(Files.exists(rutaPdf));
        assertTrue(Files.size(rutaPdf) > 0);
    }
}
