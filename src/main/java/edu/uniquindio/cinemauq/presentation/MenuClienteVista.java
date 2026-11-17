package edu.uniquindio.cinemauq.presentation;

import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.Entrada;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.LotePuntos;
import edu.uniquindio.cinemauq.domain.MovimientoPuntos;
import edu.uniquindio.cinemauq.domain.MovimientoTarjeta;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.TipoAsiento;
import edu.uniquindio.cinemauq.domain.estado.EstadoCancelada;
import edu.uniquindio.cinemauq.domain.estado.EstadoConfirmada;
import edu.uniquindio.cinemauq.domain.estado.EstadoReembolsada;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuClienteVista {
    private final MenuClienteControlador controlador;
    private final Cliente cliente;
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Funcion funcionSeleccionada;
    private final List<Asiento> asientosSeleccionados;
    private final Map<ElementoVendible, Integer> confiteriaSeleccionada;

    private GridPane gridAsientos;
    private Label lblResumenCompra;
    private TextField txtPuntosUsar;

    public MenuClienteVista(MenuClienteControlador controlador, Cliente cliente) {
        this.controlador = controlador;
        this.cliente = cliente;
        this.asientosSeleccionados = new ArrayList<>();
        this.confiteriaSeleccionada = new HashMap<>();
    }

    public Parent construir() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(10));

        HBox barraSuperior = new HBox(15);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        barraSuperior.setPadding(new Insets(5, 5, 10, 5));

        Label lblBienvenida = new Label("CinemaUQ - Cliente: " + cliente.getNombre() + " (" + cliente.getCorreo() + ")");
        Button btnCerrarSesion = new Button("Cerrar Sesion");
        btnCerrarSesion.setOnAction(e -> controlador.cerrarSesion());

        barraSuperior.getChildren().addAll(lblBienvenida, btnCerrarSesion);
        raiz.setTop(barraSuperior);

        TabPane pestañas = new TabPane();
        pestañas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabCartelera = new Tab("Cartelera y Compra", construirTabCartelera());
        Tab tabTarjeta = new Tab("Mi Tarjeta", construirTabTarjeta());
        Tab tabPuntos = new Tab("Mis Puntos", construirTabPuntos());
        Tab tabCompras = new Tab("Mis Compras", construirTabCompras());

        pestañas.getTabs().addAll(tabCartelera, tabTarjeta, tabPuntos, tabCompras);
        raiz.setCenter(pestañas);

        return raiz;
    }

    private Parent construirTabCartelera() {
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));

        Label lblPeliculas = new Label("Peliculas en Cartelera:");
        TableView<Pelicula> tablaPeliculas = new TableView<>();
        tablaPeliculas.setPrefHeight(140);

        TableColumn<Pelicula, String> colTitulo = new TableColumn<>("Titulo");
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        TableColumn<Pelicula, String> colGenero = new TableColumn<>("Genero");
        colGenero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenero()));
        TableColumn<Pelicula, String> colDuracion = new TableColumn<>("Duracion");
        colDuracion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDuracionMinutos() + " min"));
        TableColumn<Pelicula, String> colClasif = new TableColumn<>("Clasificacion");
        colClasif.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClasificacionEdad()));
        TableColumn<Pelicula, String> colSinopsis = new TableColumn<>("Sinopsis");
        colSinopsis.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSinopsis()));

        tablaPeliculas.getColumns().addAll(colTitulo, colGenero, colDuracion, colClasif, colSinopsis);
        tablaPeliculas.setItems(FXCollections.observableArrayList(controlador.listarCartelera()));

        Label lblFunciones = new Label("Funciones disponibles para la pelicula seleccionada:");
        TableView<Funcion> tablaFunciones = new TableView<>();
        tablaFunciones.setPrefHeight(120);

        TableColumn<Funcion, String> colFecha = new TableColumn<>("Fecha y Hora");
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaHora().format(FORMATO_FECHA_HORA)));
        TableColumn<Funcion, String> colSala = new TableColumn<>("Sala");
        colSala.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSala().getNombre()));
        TableColumn<Funcion, String> colPrecio = new TableColumn<>("Precio Base");
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getPrecioBase().toPlainString()));

        tablaFunciones.getColumns().addAll(colFecha, colSala, colPrecio);

        tablaPeliculas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nuevaPelicula) -> {
            if (nuevaPelicula != null) {
                tablaFunciones.setItems(FXCollections.observableArrayList(controlador.listarFuncionesDePelicula(nuevaPelicula.getId())));
                limpiarSeleccion();
            }
        });

        Label lblAsientos = new Label("Seleccione los Asientos (* = Preferencial, X = Ocupado):");
        gridAsientos = new GridPane();
        gridAsientos.setHgap(4);
        gridAsientos.setVgap(4);
        gridAsientos.setAlignment(Pos.CENTER);

        ScrollPane scrollAsientos = new ScrollPane(gridAsientos);
        scrollAsientos.setPrefHeight(160);

        tablaFunciones.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nuevaFuncion) -> {
            funcionSeleccionada = nuevaFuncion;
            asientosSeleccionados.clear();
            actualizarGridAsientos();
            actualizarResumen();
        });

        Label lblConfiteria = new Label("Confiteria y Combos:");
        TableView<ElementoVendible> tablaConfiteria = new TableView<>();
        tablaConfiteria.setPrefHeight(130);

        TableColumn<ElementoVendible, String> colProdNombre = new TableColumn<>("Nombre");
        colProdNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        TableColumn<ElementoVendible, String> colProdContenido = new TableColumn<>("Contenido");
        colProdContenido.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContenido()));
        TableColumn<ElementoVendible, String> colProdPrecio = new TableColumn<>("Precio");
        colProdPrecio.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getPrecio().toPlainString()));

        tablaConfiteria.getColumns().addAll(colProdNombre, colProdContenido, colProdPrecio);
        tablaConfiteria.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));

        HBox filaAgregarConfiteria = new HBox(10);
        filaAgregarConfiteria.setAlignment(Pos.CENTER_LEFT);
        Label lblCantidad = new Label("Cantidad:");
        Spinner<Integer> spCantidad = new Spinner<>(1, 10, 1);
        Button btnAgregarProd = new Button("Agregar a la compra");
        btnAgregarProd.setOnAction(e -> {
            ElementoVendible elem = tablaConfiteria.getSelectionModel().getSelectedItem();
            if (elem != null) {
                int cantActual = confiteriaSeleccionada.getOrDefault(elem, 0);
                confiteriaSeleccionada.put(elem, cantActual + spCantidad.getValue());
                actualizarResumen();
            }
        });
        Button btnLimpiarConfiteria = new Button("Limpiar confiteria");
        btnLimpiarConfiteria.setOnAction(e -> {
            confiteriaSeleccionada.clear();
            actualizarResumen();
        });
        filaAgregarConfiteria.getChildren().addAll(lblCantidad, spCantidad, btnAgregarProd, btnLimpiarConfiteria);

        VBox seccionPago = new VBox(8);
        lblResumenCompra = new Label("Total entradas: $0.00 | Confiteria: $0.00 | Total a pagar: $0.00");

        HBox filaPuntos = new HBox(10);
        filaPuntos.setAlignment(Pos.CENTER_LEFT);
        Label lblPuntos = new Label("Puntos a redimir (1 punto = $100):");
        txtPuntosUsar = new TextField("0");
        txtPuntosUsar.setPrefWidth(80);
        txtPuntosUsar.textProperty().addListener((obs, oldV, newV) -> actualizarResumen());

        Button btnPagar = new Button("Confirmar y Pagar Compra");
        btnPagar.setOnAction(e -> {
            int puntos = 0;
            try {
                puntos = Integer.parseInt(txtPuntosUsar.getText().trim());
            } catch (NumberFormatException ignored) {
            }
            Compra compra = controlador.realizarCompra(funcionSeleccionada, asientosSeleccionados, confiteriaSeleccionada, puntos);
            if (compra != null) {
                limpiarSeleccion();
                actualizarGridAsientos();
            }
        });

        filaPuntos.getChildren().addAll(lblPuntos, txtPuntosUsar, btnPagar);
        seccionPago.getChildren().addAll(lblResumenCompra, filaPuntos);

        contenedor.getChildren().addAll(
                lblPeliculas, tablaPeliculas,
                lblFunciones, tablaFunciones,
                lblAsientos, scrollAsientos,
                lblConfiteria, tablaConfiteria, filaAgregarConfiteria,
                seccionPago
        );

        ScrollPane scrollGeneral = new ScrollPane(contenedor);
        scrollGeneral.setFitToWidth(true);
        return scrollGeneral;
    }

    private void actualizarGridAsientos() {
        gridAsientos.getChildren().clear();
        if (funcionSeleccionada == null || funcionSeleccionada.getSala() == null) {
            return;
        }

        List<Asiento> asientos = funcionSeleccionada.getSala().getAsientos();
        for (Asiento a : asientos) {
            int filaIndex = a.getFila().charAt(0) - 'A';
            int colIndex = a.getNumero() - 1;

            boolean disponible = controlador.estaAsientoDisponible(funcionSeleccionada, a);
            String codigo = a.getFila() + a.getNumero();
            if (a.getTipo() == TipoAsiento.PREFERENCIAL) {
                codigo += "*";
            }

            if (!disponible) {
                Button btnOcupado = new Button("X");
                btnOcupado.setDisable(true);
                btnOcupado.setPrefWidth(48);
                gridAsientos.add(btnOcupado, colIndex, filaIndex);
            } else {
                ToggleButton btnAsiento = new ToggleButton(codigo);
                btnAsiento.setPrefWidth(48);
                btnAsiento.setOnAction(e -> {
                    if (btnAsiento.isSelected()) {
                        if (!asientosSeleccionados.contains(a)) {
                            asientosSeleccionados.add(a);
                        }
                    } else {
                        asientosSeleccionados.remove(a);
                    }
                    actualizarResumen();
                });
                gridAsientos.add(btnAsiento, colIndex, filaIndex);
            }
        }
    }

    private void actualizarResumen() {
        BigDecimal totalEntradas = BigDecimal.ZERO;
        if (funcionSeleccionada != null) {
            for (Asiento ignored : asientosSeleccionados) {
                totalEntradas = totalEntradas.add(funcionSeleccionada.getPrecioBase());
            }
        }

        BigDecimal totalConfiteria = BigDecimal.ZERO;
        for (Map.Entry<ElementoVendible, Integer> entry : confiteriaSeleccionada.entrySet()) {
            totalConfiteria = totalConfiteria.add(entry.getKey().getPrecio().multiply(BigDecimal.valueOf(entry.getValue())));
        }

        BigDecimal totalGeneral = totalEntradas.add(totalConfiteria).setScale(2, RoundingMode.HALF_UP);

        int puntos = 0;
        try {
            if (txtPuntosUsar != null && !txtPuntosUsar.getText().trim().isEmpty()) {
                puntos = Integer.parseInt(txtPuntosUsar.getText().trim());
            }
        } catch (NumberFormatException ignored) {
        }

        BigDecimal descuentoPuntos = BigDecimal.valueOf(puntos * 100L).setScale(2, RoundingMode.HALF_UP);
        if (descuentoPuntos.compareTo(totalGeneral) > 0) {
            descuentoPuntos = totalGeneral;
        }

        BigDecimal totalTarjeta = totalGeneral.subtract(descuentoPuntos).setScale(2, RoundingMode.HALF_UP);

        lblResumenCompra.setText("Asientos: " + asientosSeleccionados.size() +
                " ($" + totalEntradas + ") | Confiteria: $" + totalConfiteria +
                " | Total: $" + totalGeneral +
                " | Descuento puntos: $" + descuentoPuntos +
                " | A pagar en tarjeta: $" + totalTarjeta);
    }

    private void limpiarSeleccion() {
        asientosSeleccionados.clear();
        confiteriaSeleccionada.clear();
        if (txtPuntosUsar != null) {
            txtPuntosUsar.setText("0");
        }
        actualizarResumen();
    }

    private Parent construirTabTarjeta() {
        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(15));

        Label lblSaldo = new Label("Saldo disponible en Tarjeta Virtual: $" + controlador.consultarSaldoTarjeta());

        Label lblMovimientos = new Label("Historial de Movimientos de Tarjeta:");
        TableView<MovimientoTarjeta> tablaMovimientos = new TableView<>();

        TableColumn<MovimientoTarjeta, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));
        TableColumn<MovimientoTarjeta, String> colMonto = new TableColumn<>("Monto");
        colMonto.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getMonto().toPlainString()));
        TableColumn<MovimientoTarjeta, String> colFecha = new TableColumn<>("Fecha y Hora");
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaHora().format(FORMATO_FECHA_HORA)));
        TableColumn<MovimientoTarjeta, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        tablaMovimientos.getColumns().addAll(colTipo, colMonto, colFecha, colDesc);
        tablaMovimientos.setItems(FXCollections.observableArrayList(controlador.consultarMovimientosTarjeta()));

        Button btnActualizar = new Button("Actualizar Saldo y Movimientos");
        btnActualizar.setOnAction(e -> {
            lblSaldo.setText("Saldo disponible en Tarjeta Virtual: $" + controlador.consultarSaldoTarjeta());
            tablaMovimientos.setItems(FXCollections.observableArrayList(controlador.consultarMovimientosTarjeta()));
        });

        contenedor.getChildren().addAll(lblSaldo, btnActualizar, lblMovimientos, tablaMovimientos);
        return contenedor;
    }

    private Parent construirTabPuntos() {
        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(15));

        Label lblPuntosVigentes = new Label("Saldo Vigente de Puntos: " + controlador.consultarSaldoPuntos() + " puntos");

        Label lblLotes = new Label("Lotes de Puntos:");
        TableView<LotePuntos> tablaLotes = new TableView<>();
        tablaLotes.setPrefHeight(160);

        TableColumn<LotePuntos, String> colCantOrig = new TableColumn<>("Cantidad Original");
        colCantOrig.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCantidadOriginal())));
        TableColumn<LotePuntos, String> colCantRest = new TableColumn<>("Cantidad Restante");
        colCantRest.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCantidadRestante())));
        TableColumn<LotePuntos, String> colFechaObt = new TableColumn<>("Fecha Obtencion");
        colFechaObt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaObtencion().format(FORMATO_FECHA)));
        TableColumn<LotePuntos, String> colFechaVenc = new TableColumn<>("Fecha Vencimiento");
        colFechaVenc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaVencimiento().format(FORMATO_FECHA)));

        tablaLotes.getColumns().addAll(colCantOrig, colCantRest, colFechaObt, colFechaVenc);
        tablaLotes.setItems(FXCollections.observableArrayList(controlador.consultarLotesPuntos()));

        Label lblMovPuntos = new Label("Movimientos de Puntos:");
        TableView<MovimientoPuntos> tablaMovimientos = new TableView<>();
        tablaMovimientos.setPrefHeight(180);

        TableColumn<MovimientoPuntos, String> colTipoP = new TableColumn<>("Tipo");
        colTipoP.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));
        TableColumn<MovimientoPuntos, String> colCantP = new TableColumn<>("Cantidad");
        colCantP.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        TableColumn<MovimientoPuntos, String> colFechaP = new TableColumn<>("Fecha");
        colFechaP.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(FORMATO_FECHA)));
        TableColumn<MovimientoPuntos, String> colDescP = new TableColumn<>("Descripcion");
        colDescP.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        tablaMovimientos.getColumns().addAll(colTipoP, colCantP, colFechaP, colDescP);
        tablaMovimientos.setItems(FXCollections.observableArrayList(controlador.consultarMovimientosPuntos()));

        Button btnActualizar = new Button("Actualizar Puntos");
        btnActualizar.setOnAction(e -> {
            lblPuntosVigentes.setText("Saldo Vigente de Puntos: " + controlador.consultarSaldoPuntos() + " puntos");
            tablaLotes.setItems(FXCollections.observableArrayList(controlador.consultarLotesPuntos()));
            tablaMovimientos.setItems(FXCollections.observableArrayList(controlador.consultarMovimientosPuntos()));
        });

        contenedor.getChildren().addAll(lblPuntosVigentes, btnActualizar, lblLotes, tablaLotes, lblMovPuntos, tablaMovimientos);
        return contenedor;
    }

    private Parent construirTabCompras() {
        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(15));

        Label lblHistorial = new Label("Historial de Compras:");
        TableView<Compra> tablaCompras = new TableView<>();
        tablaCompras.setPrefHeight(200);

        TableColumn<Compra, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<Compra, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaCompra().format(FORMATO_FECHA_HORA)));
        TableColumn<Compra, String> colPeli = new TableColumn<>("Pelicula");
        colPeli.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFuncion() != null ? c.getValue().getFuncion().getPelicula().getTitulo() : "-"));
        TableColumn<Compra, String> colAsientos = new TableColumn<>("Entradas");
        colAsientos.setCellValueFactory(c -> {
            StringBuilder sb = new StringBuilder();
            for (Entrada e : c.getValue().getEntradas()) {
                if (!sb.isEmpty()) sb.append(", ");
                sb.append(e.getAsiento().getFila()).append(e.getAsiento().getNumero());
            }
            return new SimpleStringProperty(sb.toString());
        });
        TableColumn<Compra, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getTotal().toPlainString()));
        TableColumn<Compra, String> colTarjeta = new TableColumn<>("Pagado Tarjeta");
        colTarjeta.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getMontoPagadoTarjeta().toPlainString()));
        TableColumn<Compra, String> colPuntosU = new TableColumn<>("Puntos Usados");
        colPuntosU.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntosUsados())));
        TableColumn<Compra, String> colPuntosG = new TableColumn<>("Puntos Ganados");
        colPuntosG.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntosGanados())));
        TableColumn<Compra, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c -> {
            String est = "PENDIENTE";
            if (c.getValue().getEstado() instanceof EstadoConfirmada) est = "CONFIRMADA";
            else if (c.getValue().getEstado() instanceof EstadoCancelada) est = "CANCELADA";
            else if (c.getValue().getEstado() instanceof EstadoReembolsada) est = "REEMBOLSADA";
            return new SimpleStringProperty(est);
        });

        tablaCompras.getColumns().addAll(colId, colFecha, colPeli, colAsientos, colTotal, colTarjeta, colPuntosU, colPuntosG, colEstado);
        tablaCompras.setItems(FXCollections.observableArrayList(controlador.listarComprasCliente()));

        Label lblReembolsoInfo = new Label("Seleccione una compra confirmada para consultar el reembolso.");
        Button btnCancelarCompra = new Button("Solicitar Cancelacion de Compra");
        btnCancelarCompra.setDisable(true);

        tablaCompras.getSelectionModel().selectedItemProperty().addListener((obs, oldV, seleccionada) -> {
            if (seleccionada != null && seleccionada.getEstado() instanceof EstadoConfirmada) {
                BigDecimal porcentaje = controlador.calcularPorcentajeReembolso(seleccionada);
                BigDecimal reembolsoEstimado = seleccionada.getMontoPagadoTarjeta().multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
                lblReembolsoInfo.setText("Reembolso aplicable: " + porcentaje.multiply(BigDecimal.valueOf(100)).intValue() + "% ($" + reembolsoEstimado + ")");
                btnCancelarCompra.setDisable(false);
            } else {
                lblReembolsoInfo.setText("Seleccione una compra confirmada para consultar el reembolso.");
                btnCancelarCompra.setDisable(true);
            }
        });

        btnCancelarCompra.setOnAction(e -> {
            Compra sel = tablaCompras.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.cancelarCompra(sel);
                tablaCompras.setItems(FXCollections.observableArrayList(controlador.listarComprasCliente()));
            }
        });

        HBox filaCancelacion = new HBox(15);
        filaCancelacion.setAlignment(Pos.CENTER_LEFT);
        filaCancelacion.getChildren().addAll(lblReembolsoInfo, btnCancelarCompra);

        Label lblNotificaciones = new Label("Notificaciones recibidas:");
        ListView<String> listaNotificaciones = new ListView<>();
        listaNotificaciones.setPrefHeight(120);
        listaNotificaciones.setItems(FXCollections.observableArrayList(controlador.obtenerNotificaciones()));

        Button btnActualizar = new Button("Actualizar Compras y Notificaciones");
        btnActualizar.setOnAction(e -> {
            tablaCompras.setItems(FXCollections.observableArrayList(controlador.listarComprasCliente()));
            listaNotificaciones.setItems(FXCollections.observableArrayList(controlador.obtenerNotificaciones()));
        });

        contenedor.getChildren().addAll(lblHistorial, tablaCompras, filaCancelacion, lblNotificaciones, listaNotificaciones, btnActualizar);
        return contenedor;
    }
}
