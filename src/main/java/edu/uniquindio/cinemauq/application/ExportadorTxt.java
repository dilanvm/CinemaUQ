package edu.uniquindio.cinemauq.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExportadorTxt implements ExportadorReporte {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void exportar(Reporte reporte, Path rutaDestino) throws IOException {
        if (reporte == null || rutaDestino == null) {
            throw new IllegalArgumentException("Reporte y ruta de destino son obligatorios");
        }
        if (rutaDestino.getParent() != null) {
            Files.createDirectories(rutaDestino.getParent());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(reporte.getTitulo()).append(System.lineSeparator());

        String desdeStr = reporte.getDesde() != null ? reporte.getDesde().format(FORMATO_FECHA) : "Inicio";
        String hastaStr = reporte.getHasta() != null ? reporte.getHasta().format(FORMATO_FECHA) : "Hoy";
        sb.append("Periodo: ").append(desdeStr).append(" al ").append(hastaStr).append(System.lineSeparator());
        sb.append(System.lineSeparator());

        List<String> columnas = reporte.getColumnas();
        List<List<String>> filas = reporte.getFilas();

        int[] anchos = new int[columnas.size()];
        for (int i = 0; i < columnas.size(); i++) {
            anchos[i] = columnas.get(i).length();
        }
        for (List<String> fila : filas) {
            for (int i = 0; i < fila.size() && i < anchos.length; i++) {
                if (fila.get(i) != null && fila.get(i).length() > anchos[i]) {
                    anchos[i] = fila.get(i).length();
                }
            }
        }

        for (int i = 0; i < columnas.size(); i++) {
            sb.append(String.format("%-" + (anchos[i] + 4) + "s", columnas.get(i)));
        }
        sb.append(System.lineSeparator());

        for (List<String> fila : filas) {
            for (int i = 0; i < fila.size() && i < anchos.length; i++) {
                sb.append(String.format("%-" + (anchos[i] + 4) + "s", fila.get(i) != null ? fila.get(i) : ""));
            }
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());

        for (Map.Entry<String, String> entry : reporte.getTotales().entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(System.lineSeparator());
        }

        Files.writeString(rutaDestino, sb.toString());
    }
}
