package edu.uniquindio.cinemauq.application;

import java.io.IOException;
import java.nio.file.Path;

public interface ExportadorReporte {
    void exportar(Reporte reporte, Path rutaDestino) throws IOException;
}
