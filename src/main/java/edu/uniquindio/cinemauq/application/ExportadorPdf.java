package edu.uniquindio.cinemauq.application;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExportadorPdf implements ExportadorReporte {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void exportar(Reporte reporte, Path rutaDestino) throws IOException {
        if (reporte == null || rutaDestino == null) {
            throw new IllegalArgumentException("Reporte y ruta de destino son obligatorios");
        }
        if (rutaDestino.getParent() != null) {
            Files.createDirectories(rutaDestino.getParent());
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yPosition = 750;

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(margin, yPosition);
                content.showText(limpiarTexto(reporte.getTitulo()));
                content.endText();

                yPosition -= 20;

                String desdeStr = reporte.getDesde() != null ? reporte.getDesde().format(FORMATO_FECHA) : "Inicio";
                String hastaStr = reporte.getHasta() != null ? reporte.getHasta().format(FORMATO_FECHA) : "Hoy";
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(margin, yPosition);
                content.showText("Periodo: " + desdeStr + " al " + hastaStr);
                content.endText();

                yPosition -= 30;

                List<String> columnas = reporte.getColumnas();
                List<List<String>> filas = reporte.getFilas();

                StringBuilder headerBuilder = new StringBuilder();
                for (String col : columnas) {
                    headerBuilder.append(String.format("%-20s", col));
                }
                content.beginText();
                content.setFont(PDType1Font.COURIER_BOLD, 9);
                content.newLineAtOffset(margin, yPosition);
                content.showText(limpiarTexto(headerBuilder.toString()));
                content.endText();

                yPosition -= 15;

                content.setFont(PDType1Font.COURIER, 8);
                for (List<String> fila : filas) {
                    if (yPosition < 60) {
                        break;
                    }
                    StringBuilder rowBuilder = new StringBuilder();
                    for (String celda : fila) {
                        rowBuilder.append(String.format("%-20s", celda != null ? celda : ""));
                    }
                    content.beginText();
                    content.newLineAtOffset(margin, yPosition);
                    content.showText(limpiarTexto(rowBuilder.toString()));
                    content.endText();
                    yPosition -= 12;
                }

                yPosition -= 15;

                content.setFont(PDType1Font.HELVETICA_BOLD, 9);
                for (Map.Entry<String, String> entry : reporte.getTotales().entrySet()) {
                    if (yPosition < 40) {
                        break;
                    }
                    content.beginText();
                    content.newLineAtOffset(margin, yPosition);
                    content.showText(limpiarTexto(entry.getKey() + ": " + entry.getValue()));
                    content.endText();
                    yPosition -= 12;
                }
            }

            document.save(rutaDestino.toFile());
        }
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\r", " ").replace("\n", " ").trim();
    }
}
