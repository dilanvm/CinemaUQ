package edu.uniquindio.cinemauq.presentation;

import edu.uniquindio.cinemauq.application.Reporte;
import edu.uniquindio.cinemauq.application.ServicioEstadisticas;
import edu.uniquindio.cinemauq.application.TipoReporte;
import edu.uniquindio.cinemauq.domain.Administrador;
import edu.uniquindio.cinemauq.domain.CategoriaProducto;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.Producto;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.TipoAsiento;
import edu.uniquindio.cinemauq.domain.estado.EstadoCancelada;
import edu.uniquindio.cinemauq.domain.estado.EstadoConfirmada;
import edu.uniquindio.cinemauq.domain.estado.EstadoReembolsada;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuAdminVista {
    private final MenuAdminControlador controlador;
    private final Administrador administrador;
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Reporte reporteActual;

    public MenuAdminVista(MenuAdminControlador controlador, Administrador administrador) {
        this.controlador = controlador;
        this.administrador = administrador;
    }

    public Parent construir() {
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(10));

        HBox barraSuperior = new HBox(15);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        barraSuperior.setPadding(new Insets(5, 5, 10, 5));

        Label lblAdmin = new Label("CinemaUQ - Administrador: " + administrador.getNombre());
        Button btnCerrarSesion = new Button("Cerrar Sesion");
        btnCerrarSesion.setOnAction(e -> controlador.cerrarSesion());

        barraSuperior.getChildren().addAll(lblAdmin, btnCerrarSesion);
        raiz.setTop(barraSuperior);

        TabPane pestañas = new TabPane();
        pestañas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabPeliculas = new Tab("Peliculas", construirTabPeliculas());
        Tab tabSalas = new Tab("Salas y Asientos", construirTabSalas());
        Tab tabFunciones = new Tab("Funciones", construirTabFunciones());
        Tab tabConfiteria = new Tab("Confiteria", construirTabConfiteria());
        Tab tabClientes = new Tab("Clientes y Compras", construirTabClientes());
        Tab tabRecargas = new Tab("Recargas", construirTabRecargas());
        Tab tabReportes = new Tab("Reportes", construirTabReportes());
        Tab tabEstadisticas = new Tab("Estadisticas y Graficos", construirTabEstadisticas());

        pestañas.getTabs().addAll(tabPeliculas, tabSalas, tabFunciones, tabConfiteria, tabClientes, tabRecargas, tabReportes, tabEstadisticas);
        raiz.setCenter(pestañas);

        return raiz;
    }

    private Parent construirTabPeliculas() {
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));

        TableView<Pelicula> tabla = new TableView<>();
        tabla.setPrefHeight(180);

        TableColumn<Pelicula, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<Pelicula, String> colTit = new TableColumn<>("Titulo");
        colTit.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        TableColumn<Pelicula, String> colGen = new TableColumn<>("Genero");
        colGen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenero()));
        TableColumn<Pelicula, String> colDur = new TableColumn<>("Duracion");
        colDur.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDuracionMinutos() + " min"));
        TableColumn<Pelicula, String> colClas = new TableColumn<>("Clasificacion");
        colClas.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClasificacionEdad()));
        TableColumn<Pelicula, String> colAct = new TableColumn<>("Activa");
        colAct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActiva() ? "SI" : "NO"));

        tabla.getColumns().addAll(colId, colTit, colGen, colDur, colClas, colAct);
        tabla.setItems(FXCollections.observableArrayList(controlador.listarPeliculas()));

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);

        TextField txtTitulo = new TextField();
        TextField txtSinopsis = new TextField();
        TextField txtDuracion = new TextField();
        TextField txtClasif = new TextField();
        TextField txtGenero = new TextField();
        CheckBox chkActiva = new CheckBox("Activa en cartelera");
        chkActiva.setSelected(true);

        form.add(new Label("Titulo:"), 0, 0);
        form.add(txtTitulo, 1, 0);
        form.add(new Label("Sinopsis:"), 0, 1);
        form.add(txtSinopsis, 1, 1);
        form.add(new Label("Duracion (min):"), 2, 0);
        form.add(txtDuracion, 3, 0);
        form.add(new Label("Clasificacion:"), 2, 1);
        form.add(txtClasif, 3, 1);
        form.add(new Label("Genero:"), 4, 0);
        form.add(txtGenero, 5, 0);
        form.add(chkActiva, 4, 1);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldV, p) -> {
            if (p != null) {
                txtTitulo.setText(p.getTitulo());
                txtSinopsis.setText(p.getSinopsis());
                txtDuracion.setText(String.valueOf(p.getDuracionMinutos()));
                txtClasif.setText(p.getClasificacionEdad());
                txtGenero.setText(p.getGenero());
                chkActiva.setSelected(p.isActiva());
            }
        });

        HBox botones = new HBox(10);
        Button btnCrear = new Button("Crear Pelicula");
        btnCrear.setOnAction(e -> {
            try {
                int dur = Integer.parseInt(txtDuracion.getText().trim());
                controlador.crearPelicula(txtTitulo.getText(), txtSinopsis.getText(), dur, txtClasif.getText(), txtGenero.getText());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarPeliculas()));
            } catch (NumberFormatException ex) {
                new MenuAdminControlador(controlador.getAdministrador() != null ? null : null, null, null);
            }
        });

        Button btnEditar = new Button("Editar Pelicula");
        btnEditar.setOnAction(e -> {
            Pelicula sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    int dur = Integer.parseInt(txtDuracion.getText().trim());
                    controlador.editarPelicula(sel.getId(), txtTitulo.getText(), txtSinopsis.getText(), dur, txtClasif.getText(), txtGenero.getText(), chkActiva.isSelected());
                    tabla.setItems(FXCollections.observableArrayList(controlador.listarPeliculas()));
                } catch (NumberFormatException ignored) {
                }
            }
        });

        Button btnEliminar = new Button("Eliminar Pelicula");
        btnEliminar.setOnAction(e -> {
            Pelicula sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.eliminarPelicula(sel.getId());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarPeliculas()));
            }
        });

        botones.getChildren().addAll(btnCrear, btnEditar, btnEliminar);
        contenedor.getChildren().addAll(new Label("Gestion de Peliculas:"), tabla, form, botones);
        return contenedor;
    }

    private Parent construirTabSalas() {
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));

        TableView<Sala> tabla = new TableView<>();
        tabla.setPrefHeight(180);

        TableColumn<Sala, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<Sala, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        TableColumn<Sala, String> colCap = new TableColumn<>("Capacidad");
        colCap.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCapacidad())));
        TableColumn<Sala, String> colAct = new TableColumn<>("Activa");
        colAct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActiva() ? "SI" : "NO"));

        tabla.getColumns().addAll(colId, colNom, colCap, colAct);
        tabla.setItems(FXCollections.observableArrayList(controlador.listarSalas()));

        HBox formSala = new HBox(10);
        formSala.setAlignment(Pos.CENTER_LEFT);
        TextField txtNombreSala = new TextField();
        txtNombreSala.setPromptText("Nombre de sala");
        CheckBox chkActivaSala = new CheckBox("Activa");
        chkActivaSala.setSelected(true);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldV, s) -> {
            if (s != null) {
                txtNombreSala.setText(s.getNombre());
                chkActivaSala.setSelected(s.isActiva());
            }
        });

        Button btnCrearSala = new Button("Crear Sala");
        btnCrearSala.setOnAction(e -> {
            controlador.crearSala(txtNombreSala.getText(), chkActivaSala.isSelected());
            tabla.setItems(FXCollections.observableArrayList(controlador.listarSalas()));
        });

        Button btnEditarSala = new Button("Editar Sala");
        btnEditarSala.setOnAction(e -> {
            Sala sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.editarSala(sel.getId(), txtNombreSala.getText(), chkActivaSala.isSelected());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarSalas()));
            }
        });

        Button btnEliminarSala = new Button("Eliminar Sala");
        btnEliminarSala.setOnAction(e -> {
            Sala sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.eliminarSala(sel.getId());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarSalas()));
            }
        });

        formSala.getChildren().addAll(new Label("Nombre:"), txtNombreSala, chkActivaSala, btnCrearSala, btnEditarSala, btnEliminarSala);

        HBox formAsiento = new HBox(10);
        formAsiento.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> cbFila = new ComboBox<>(FXCollections.observableArrayList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"));
        cbFila.setValue("A");
        Spinner<Integer> spNum = new Spinner<>(1, 30, 1);
        ComboBox<TipoAsiento> cbTipo = new ComboBox<>(FXCollections.observableArrayList(TipoAsiento.GENERAL, TipoAsiento.PREFERENCIAL));
        cbTipo.setValue(TipoAsiento.GENERAL);

        Button btnAgregarAsiento = new Button("Agregar Asiento a Sala Seleccionada");
        btnAgregarAsiento.setOnAction(e -> {
            Sala sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.agregarAsiento(sel.getId(), cbFila.getValue(), spNum.getValue(), cbTipo.getValue());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarSalas()));
            }
        });

        formAsiento.getChildren().addAll(new Label("Fila:"), cbFila, new Label("Numero:"), spNum, new Label("Tipo:"), cbTipo, btnAgregarAsiento);

        contenedor.getChildren().addAll(new Label("Gestion de Salas y Asientos:"), tabla, formSala, formAsiento);
        return contenedor;
    }

    private Parent construirTabFunciones() {
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));

        TableView<Funcion> tabla = new TableView<>();
        tabla.setPrefHeight(180);

        TableColumn<Funcion, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<Funcion, String> colPel = new TableColumn<>("Pelicula");
        colPel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPelicula().getTitulo()));
        TableColumn<Funcion, String> colSal = new TableColumn<>("Sala");
        colSal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSala().getNombre()));
        TableColumn<Funcion, String> colFec = new TableColumn<>("Fecha y Hora");
        colFec.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaHora().format(FORMATO_FECHA_HORA)));
        TableColumn<Funcion, String> colPre = new TableColumn<>("Precio Base");
        colPre.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getPrecioBase().toPlainString()));
        TableColumn<Funcion, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadoFuncion().name()));

        tabla.getColumns().addAll(colId, colPel, colSal, colFec, colPre, colEst);
        tabla.setItems(FXCollections.observableArrayList(controlador.listarFunciones()));

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);

        ComboBox<Pelicula> cbPelicula = new ComboBox<>();
        cbPelicula.setItems(FXCollections.observableArrayList(controlador.listarPeliculas()));

        ComboBox<Sala> cbSala = new ComboBox<>();
        cbSala.setItems(FXCollections.observableArrayList(controlador.listarSalas()));

        DatePicker dpFecha = new DatePicker(LocalDate.now());
        TextField txtHora = new TextField("18:00");
        TextField txtPrecio = new TextField("12000.00");

        form.add(new Label("Pelicula:"), 0, 0);
        form.add(cbPelicula, 1, 0);
        form.add(new Label("Sala:"), 2, 0);
        form.add(cbSala, 3, 0);
        form.add(new Label("Fecha:"), 0, 1);
        form.add(dpFecha, 1, 1);
        form.add(new Label("Hora (HH:mm):"), 2, 1);
        form.add(txtHora, 3, 1);
        form.add(new Label("Precio Base:"), 4, 1);
        form.add(txtPrecio, 5, 1);

        HBox botones = new HBox(10);
        Button btnCrear = new Button("Crear Funcion");
        btnCrear.setOnAction(e -> {
            try {
                Pelicula p = cbPelicula.getValue();
                Sala s = cbSala.getValue();
                if (p != null && s != null && dpFecha.getValue() != null) {
                    LocalTime hora = LocalTime.parse(txtHora.getText().trim());
                    LocalDateTime fechaHora = LocalDateTime.of(dpFecha.getValue(), hora);
                    BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
                    controlador.crearFuncion(p.getId(), s.getId(), fechaHora, precio);
                    tabla.setItems(FXCollections.observableArrayList(controlador.listarFunciones()));
                }
            } catch (Exception ignored) {
            }
        });

        Button btnEditar = new Button("Editar Fecha y Precio");
        btnEditar.setOnAction(e -> {
            Funcion sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null && dpFecha.getValue() != null) {
                try {
                    LocalTime hora = LocalTime.parse(txtHora.getText().trim());
                    LocalDateTime fechaHora = LocalDateTime.of(dpFecha.getValue(), hora);
                    BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
                    controlador.editarFuncion(sel.getId(), fechaHora, precio);
                    tabla.setItems(FXCollections.observableArrayList(controlador.listarFunciones()));
                } catch (Exception ignored) {
                }
            }
        });

        Button btnEliminar = new Button("Eliminar Funcion");
        btnEliminar.setOnAction(e -> {
            Funcion sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.eliminarFuncion(sel.getId());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarFunciones()));
            }
        });

        Button btnCancelar = new Button("Cancelar Funcion (Reembolso 100%)");
        btnCancelar.setOnAction(e -> {
            Funcion sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.cancelarFuncion(sel.getId());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarFunciones()));
            }
        });

        botones.getChildren().addAll(btnCrear, btnEditar, btnEliminar, btnCancelar);

        HBox filaPlantilla = new HBox(10);
        filaPlantilla.setAlignment(Pos.CENTER_LEFT);
        DatePicker dpPlantilla = new DatePicker(LocalDate.now().plusDays(1));
        TextField txtHoraPlantilla = new TextField("20:00");
        Button btnPlantilla = new Button("Crear Funcion desde Plantilla");
        btnPlantilla.setOnAction(e -> {
            try {
                LocalTime hora = LocalTime.parse(txtHoraPlantilla.getText().trim());
                LocalDateTime fechaHora = LocalDateTime.of(dpPlantilla.getValue(), hora);
                controlador.crearFuncionDesdePlantilla(fechaHora);
                tabla.setItems(FXCollections.observableArrayList(controlador.listarFunciones()));
            } catch (Exception ignored) {
            }
        });
        filaPlantilla.getChildren().addAll(new Label("Plantilla - Fecha:"), dpPlantilla, new Label("Hora:"), txtHoraPlantilla, btnPlantilla);

        contenedor.getChildren().addAll(new Label("Gestion de Funciones:"), tabla, form, botones, filaPlantilla);
        return contenedor;
    }

    private Parent construirTabConfiteria() {
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));

        TableView<ElementoVendible> tabla = new TableView<>();
        tabla.setPrefHeight(180);

        TableColumn<ElementoVendible, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        TableColumn<ElementoVendible, String> colCont = new TableColumn<>("Contenido");
        colCont.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContenido()));
        TableColumn<ElementoVendible, String> colPrec = new TableColumn<>("Precio Actual");
        colPrec.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getPrecio().toPlainString()));

        tabla.getColumns().addAll(colNom, colCont, colPrec);
        tabla.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));

        GridPane formProd = new GridPane();
        formProd.setHgap(8);
        formProd.setVgap(8);

        TextField txtNomProd = new TextField();
        TextField txtDescProd = new TextField();
        TextField txtPrecioProd = new TextField();
        ComboBox<CategoriaProducto> cbCatProd = new ComboBox<>(FXCollections.observableArrayList(CategoriaProducto.values()));
        cbCatProd.setValue(CategoriaProducto.COMIDA);
        CheckBox chkDispProd = new CheckBox("Disponible");
        chkDispProd.setSelected(true);

        formProd.add(new Label("Nombre Producto:"), 0, 0);
        formProd.add(txtNomProd, 1, 0);
        formProd.add(new Label("Descripcion:"), 2, 0);
        formProd.add(txtDescProd, 3, 0);
        formProd.add(new Label("Precio:"), 0, 1);
        formProd.add(txtPrecioProd, 1, 1);
        formProd.add(new Label("Categoria:"), 2, 1);
        formProd.add(cbCatProd, 3, 1);
        formProd.add(chkDispProd, 4, 1);

        HBox botonesProd = new HBox(10);
        Button btnCrearProd = new Button("Crear Producto");
        btnCrearProd.setOnAction(e -> {
            try {
                BigDecimal p = new BigDecimal(txtPrecioProd.getText().trim());
                controlador.crearProducto(txtNomProd.getText(), txtDescProd.getText(), p, chkDispProd.isSelected(), cbCatProd.getValue());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));
            } catch (Exception ignored) {
            }
        });

        Button btnEditarProd = new Button("Editar Producto Seleccionado");
        btnEditarProd.setOnAction(e -> {
            ElementoVendible sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    BigDecimal p = new BigDecimal(txtPrecioProd.getText().trim());
                    controlador.editarProducto(sel.getNombre(), txtNomProd.getText(), txtDescProd.getText(), p, chkDispProd.isSelected(), cbCatProd.getValue());
                    tabla.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));
                } catch (Exception ignored) {
                }
            }
        });

        Button btnEliminarElem = new Button("Eliminar Elemento Vendible");
        btnEliminarElem.setOnAction(e -> {
            ElementoVendible sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.eliminarProducto(sel.getNombre());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));
            }
        });

        botonesProd.getChildren().addAll(btnCrearProd, btnEditarProd, btnEliminarElem);

        HBox formCombo = new HBox(10);
        formCombo.setAlignment(Pos.CENTER_LEFT);
        TextField txtNomCombo = new TextField();
        txtNomCombo.setPromptText("Nombre del combo");
        ListView<ElementoVendible> listaElementos = new ListView<>();
        listaElementos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaElementos.setPrefHeight(90);
        listaElementos.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));

        Button btnCrearCombo = new Button("Crear Combo con seleccionados");
        btnCrearCombo.setOnAction(e -> {
            List<ElementoVendible> seleccionados = listaElementos.getSelectionModel().getSelectedItems();
            if (seleccionados != null && !seleccionados.isEmpty()) {
                controlador.crearCombo(txtNomCombo.getText(), new ArrayList<>(seleccionados));
                tabla.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));
                listaElementos.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));
            }
        });

        formCombo.getChildren().addAll(new Label("Combo:"), txtNomCombo, new Label("Elementos:"), listaElementos, btnCrearCombo);

        HBox formPromo = new HBox(10);
        formPromo.setAlignment(Pos.CENTER_LEFT);
        TextField txtPct = new TextField("10.00");
        txtPct.setPrefWidth(60);
        Button btnPromoPct = new Button("Aplicar Descuento %");
        btnPromoPct.setOnAction(e -> {
            ElementoVendible sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    controlador.aplicarDescuentoPorcentaje(sel.getNombre(), new BigDecimal(txtPct.getText().trim()));
                    tabla.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));
                } catch (Exception ignored) {
                }
            }
        });

        TextField txtValorFijo = new TextField("2000.00");
        txtValorFijo.setPrefWidth(80);
        Button btnPromoFijo = new Button("Aplicar Descuento Fijo");
        btnPromoFijo.setOnAction(e -> {
            ElementoVendible sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                try {
                    controlador.aplicarDescuentoValorFijo(sel.getNombre(), new BigDecimal(txtValorFijo.getText().trim()));
                    tabla.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));
                } catch (Exception ignored) {
                }
            }
        });

        Button btnQuitarPromo = new Button("Quitar Promocion");
        btnQuitarPromo.setOnAction(e -> {
            ElementoVendible sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controlador.quitarPromocion(sel.getNombre());
                tabla.setItems(FXCollections.observableArrayList(controlador.listarConfiteria()));
            }
        });

        formPromo.getChildren().addAll(new Label("Promociones:"), txtPct, btnPromoPct, txtValorFijo, btnPromoFijo, btnQuitarPromo);

        contenedor.getChildren().addAll(new Label("Catalogo de Confiteria:"), tabla, formProd, botonesProd, formCombo, formPromo);

        ScrollPane scroll = new ScrollPane(contenedor);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private Parent construirTabClientes() {
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));

        Label lblClientes = new Label("Clientes Registrados:");
        TableView<Cliente> tablaClientes = new TableView<>();
        tablaClientes.setPrefHeight(150);

        TableColumn<Cliente, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<Cliente, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        TableColumn<Cliente, String> colCor = new TableColumn<>("Correo");
        colCor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        TableColumn<Cliente, String> colSal = new TableColumn<>("Saldo Tarjeta");
        colSal.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getTarjetaVirtual().consultarSaldo().toPlainString()));
        TableColumn<Cliente, String> colAct = new TableColumn<>("Tarjeta Activa");
        colAct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTarjetaVirtual().isActiva() ? "SI" : "NO"));

        tablaClientes.getColumns().addAll(colId, colNom, colCor, colSal, colAct);
        tablaClientes.setItems(FXCollections.observableArrayList(controlador.listarClientes()));

        Label lblCompras = new Label("Todas las Compras:");
        TableView<Compra> tablaCompras = new TableView<>();
        tablaCompras.setPrefHeight(180);

        TableColumn<Compra, String> colCompId = new TableColumn<>("ID");
        colCompId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<Compra, String> colCompFec = new TableColumn<>("Fecha");
        colCompFec.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaCompra().format(FORMATO_FECHA_HORA)));
        TableColumn<Compra, String> colCompCli = new TableColumn<>("Cliente");
        colCompCli.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente() != null ? c.getValue().getCliente().getNombre() : "-"));
        TableColumn<Compra, String> colCompPel = new TableColumn<>("Pelicula");
        colCompPel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFuncion() != null ? c.getValue().getFuncion().getPelicula().getTitulo() : "-"));
        TableColumn<Compra, String> colCompTot = new TableColumn<>("Total");
        colCompTot.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getTotal().toPlainString()));
        TableColumn<Compra, String> colCompTar = new TableColumn<>("Pagado Tarjeta");
        colCompTar.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getMontoPagadoTarjeta().toPlainString()));
        TableColumn<Compra, String> colCompEst = new TableColumn<>("Estado");
        colCompEst.setCellValueFactory(c -> {
            String est = "PENDIENTE";
            if (c.getValue().getEstado() instanceof EstadoConfirmada) est = "CONFIRMADA";
            else if (c.getValue().getEstado() instanceof EstadoCancelada) est = "CANCELADA";
            else if (c.getValue().getEstado() instanceof EstadoReembolsada) est = "REEMBOLSADA";
            return new SimpleStringProperty(est);
        });

        tablaCompras.getColumns().addAll(colCompId, colCompFec, colCompCli, colCompPel, colCompTot, colCompTar, colCompEst);
        tablaCompras.setItems(FXCollections.observableArrayList(controlador.listarCompras()));

        Button btnRefrescar = new Button("Actualizar Tablas");
        btnRefrescar.setOnAction(e -> {
            tablaClientes.setItems(FXCollections.observableArrayList(controlador.listarClientes()));
            tablaCompras.setItems(FXCollections.observableArrayList(controlador.listarCompras()));
        });

        contenedor.getChildren().addAll(lblClientes, tablaClientes, lblCompras, tablaCompras, btnRefrescar);
        return contenedor;
    }

    private Parent construirTabRecargas() {
        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(15));

        Label lblTitulo = new Label("Gestion y Recargas de Tarjetas Virtuales");

        HBox filaSeleccion = new HBox(10);
        filaSeleccion.setAlignment(Pos.CENTER_LEFT);
        Label lblCliente = new Label("Seleccione Cliente:");
        ComboBox<Cliente> cbClientes = new ComboBox<>();
        cbClientes.setItems(FXCollections.observableArrayList(controlador.listarClientes()));

        filaSeleccion.getChildren().addAll(lblCliente, cbClientes);

        Label lblEstadoActual = new Label("Estado actual: -");
        cbClientes.valueProperty().addListener((obs, oldV, cl) -> {
            if (cl != null) {
                lblEstadoActual.setText("Saldo: $" + cl.getTarjetaVirtual().consultarSaldo() + " | Tarjeta Activa: " + (cl.getTarjetaVirtual().isActiva() ? "SI" : "NO"));
            }
        });

        HBox filaRecarga = new HBox(10);
        filaRecarga.setAlignment(Pos.CENTER_LEFT);
        Label lblMonto = new Label("Monto a recargar ($):");
        TextField txtMonto = new TextField("50000.00");
        Button btnRecargar = new Button("Recargar Tarjeta");
        btnRecargar.setOnAction(e -> {
            Cliente cl = cbClientes.getValue();
            if (cl != null) {
                try {
                    BigDecimal monto = new BigDecimal(txtMonto.getText().trim());
                    controlador.recargarTarjeta(cl, monto);
                    lblEstadoActual.setText("Saldo: $" + cl.getTarjetaVirtual().consultarSaldo() + " | Tarjeta Activa: " + (cl.getTarjetaVirtual().isActiva() ? "SI" : "NO"));
                } catch (Exception ignored) {
                }
            }
        });
        filaRecarga.getChildren().addAll(lblMonto, txtMonto, btnRecargar);

        HBox filaEstado = new HBox(10);
        filaEstado.setAlignment(Pos.CENTER_LEFT);
        Button btnActivar = new Button("Activar Tarjeta");
        btnActivar.setOnAction(e -> {
            Cliente cl = cbClientes.getValue();
            if (cl != null) {
                controlador.cambiarEstadoTarjeta(cl, true);
                lblEstadoActual.setText("Saldo: $" + cl.getTarjetaVirtual().consultarSaldo() + " | Tarjeta Activa: " + (cl.getTarjetaVirtual().isActiva() ? "SI" : "NO"));
            }
        });

        Button btnDesactivar = new Button("Desactivar Tarjeta");
        btnDesactivar.setOnAction(e -> {
            Cliente cl = cbClientes.getValue();
            if (cl != null) {
                controlador.cambiarEstadoTarjeta(cl, false);
                lblEstadoActual.setText("Saldo: $" + cl.getTarjetaVirtual().consultarSaldo() + " | Tarjeta Activa: " + (cl.getTarjetaVirtual().isActiva() ? "SI" : "NO"));
            }
        });

        filaEstado.getChildren().addAll(btnActivar, btnDesactivar);

        contenedor.getChildren().addAll(lblTitulo, filaSeleccion, lblEstadoActual, filaRecarga, filaEstado);
        return contenedor;
    }

    private Parent construirTabReportes() {
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));

        HBox filaControles = new HBox(10);
        filaControles.setAlignment(Pos.CENTER_LEFT);

        ComboBox<TipoReporte> cbTipo = new ComboBox<>(FXCollections.observableArrayList(TipoReporte.values()));
        cbTipo.setValue(TipoReporte.VENTAS);

        DatePicker dpDesde = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker dpHasta = new DatePicker(LocalDate.now().plusMonths(1));

        Button btnGenerar = new Button("Generar Reporte");

        filaControles.getChildren().addAll(new Label("Tipo:"), cbTipo, new Label("Desde:"), dpDesde, new Label("Hasta:"), dpHasta, btnGenerar);

        Label lblTituloReporte = new Label("Seleccione un reporte y haga clic en Generar.");
        TableView<List<String>> tablaReporte = new TableView<>();
        tablaReporte.setPrefHeight(260);

        Label lblTotalesReporte = new Label("");

        HBox filaExportar = new HBox(10);
        filaExportar.setAlignment(Pos.CENTER_LEFT);
        Button btnExportarTxt = new Button("Exportar TXT");
        Button btnExportarPdf = new Button("Exportar PDF");
        btnExportarTxt.setDisable(true);
        btnExportarPdf.setDisable(true);

        filaExportar.getChildren().addAll(btnExportarTxt, btnExportarPdf);

        btnGenerar.setOnAction(e -> {
            TipoReporte tipo = cbTipo.getValue();
            LocalDate desde = dpDesde.getValue();
            LocalDate hasta = dpHasta.getValue();
            reporteActual = controlador.generarReporte(tipo, desde, hasta);

            if (reporteActual != null) {
                lblTituloReporte.setText(reporteActual.getTitulo() + " (Periodo: " +
                        (desde != null ? desde.format(FORMATO_FECHA) : "Inicio") + " al " +
                        (hasta != null ? hasta.format(FORMATO_FECHA) : "Hoy") + ")");

                tablaReporte.getColumns().clear();
                List<String> columnas = reporteActual.getColumnas();
                for (int i = 0; i < columnas.size(); i++) {
                    final int colIndex = i;
                    TableColumn<List<String>, String> col = new TableColumn<>(columnas.get(i));
                    col.setCellValueFactory(data -> {
                        List<String> fila = data.getValue();
                        String valor = (fila != null && colIndex < fila.size()) ? fila.get(colIndex) : "";
                        return new SimpleStringProperty(valor);
                    });
                    tablaReporte.getColumns().add(col);
                }

                tablaReporte.setItems(FXCollections.observableArrayList(reporteActual.getFilas()));

                StringBuilder totalesText = new StringBuilder();
                for (Map.Entry<String, String> entry : reporteActual.getTotales().entrySet()) {
                    totalesText.append(entry.getKey()).append(": ").append(entry.getValue()).append("  |  ");
                }
                lblTotalesReporte.setText(totalesText.toString());

                btnExportarTxt.setDisable(false);
                btnExportarPdf.setDisable(false);
            }
        });

        btnExportarTxt.setOnAction(e -> {
            if (reporteActual != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar Reporte TXT");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
                fileChooser.setInitialFileName("reporte_" + cbTipo.getValue().name().toLowerCase() + ".txt");
                File archivo = fileChooser.showSaveDialog(null);
                if (archivo != null) {
                    controlador.exportarTxt(reporteActual, archivo.toPath());
                }
            }
        });

        btnExportarPdf.setOnAction(e -> {
            if (reporteActual != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar Reporte PDF");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos PDF (*.pdf)", "*.pdf"));
                fileChooser.setInitialFileName("reporte_" + cbTipo.getValue().name().toLowerCase() + ".pdf");
                File archivo = fileChooser.showSaveDialog(null);
                if (archivo != null) {
                    controlador.exportarPdf(reporteActual, archivo.toPath());
                }
            }
        });

        contenedor.getChildren().addAll(filaControles, lblTituloReporte, tablaReporte, lblTotalesReporte, filaExportar);
        return contenedor;
    }

    private Parent construirTabEstadisticas() {
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));

        ServicioEstadisticas stats = controlador.getServicioEstadisticas();

        Label lblMetricas = new Label(
                "Total Ventas Realizadas: " + stats.contarVentasRealizadas() +
                        " ($" + stats.calcularTotalVentas() + ") | Reembolsos Realizados: " +
                        stats.contarReembolsosRealizados() + " ($" + stats.calcularTotalReembolsos() + ")"
        );

        HBox filaSelector = new HBox(10);
        filaSelector.setAlignment(Pos.CENTER_LEFT);
        Label lblGrafico = new Label("Seleccione Grafico:");
        ComboBox<String> cbGraficos = new ComboBox<>(FXCollections.observableArrayList(
                "Entradas por Pelicula (BarChart)",
                "Ventas por Pelicula (BarChart)",
                "Productos mas Vendidos (BarChart)",
                "Ocupacion por Sala (PieChart)",
                "Ventas por Categoria Confiteria (PieChart)",
                "Ventas por Dia (LineChart)",
                "Recargas por Dia (LineChart)"
        ));
        cbGraficos.setValue("Entradas por Pelicula (BarChart)");

        Button btnRefrescar = new Button("Actualizar Estadisticas");

        filaSelector.getChildren().addAll(lblGrafico, cbGraficos, btnRefrescar);

        StackPane panelGrafico = new StackPane();
        panelGrafico.setPrefHeight(380);

        Runnable actualizarGrafico = () -> {
            panelGrafico.getChildren().clear();
            String sel = cbGraficos.getValue();
            if (sel == null) return;

            if (sel.startsWith("Entradas por Pelicula")) {
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Pelicula");
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Entradas");
                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                barChart.setTitle("Entradas Vendidas por Pelicula");
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                serie.setName("Entradas");
                for (Map.Entry<String, Integer> e : stats.obtenerEntradasPorPelicula().entrySet()) {
                    serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
                }
                barChart.getData().add(serie);
                barChart.setLegendVisible(false);
                panelGrafico.getChildren().add(barChart);
            } else if (sel.startsWith("Ventas por Pelicula")) {
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Pelicula");
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Monto ($)");
                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                barChart.setTitle("Ventas en Dinero por Pelicula");
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                serie.setName("Total COP");
                for (Map.Entry<String, BigDecimal> e : stats.obtenerVentasPorPelicula().entrySet()) {
                    serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
                }
                barChart.getData().add(serie);
                barChart.setLegendVisible(false);
                panelGrafico.getChildren().add(barChart);
            } else if (sel.startsWith("Productos mas Vendidos")) {
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Producto / Combo");
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Unidades");
                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                barChart.setTitle("Productos y Combos Mas Vendidos");
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                serie.setName("Unidades");
                for (Map.Entry<String, Integer> e : stats.obtenerProductosMasVendidos().entrySet()) {
                    serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
                }
                barChart.getData().add(serie);
                barChart.setLegendVisible(false);
                panelGrafico.getChildren().add(barChart);
            } else if (sel.startsWith("Ocupacion por Sala")) {
                PieChart pieChart = new PieChart();
                pieChart.setTitle("Porcentaje de Ocupacion por Sala (%)");
                for (Map.Entry<String, Double> e : stats.obtenerOcupacionPorSala().entrySet()) {
                    pieChart.getData().add(new PieChart.Data(e.getKey() + " (" + e.getValue() + "%)", e.getValue()));
                }
                panelGrafico.getChildren().add(pieChart);
            } else if (sel.startsWith("Ventas por Categoria Confiteria")) {
                PieChart pieChart = new PieChart();
                pieChart.setTitle("Ventas por Categoria de Confiteria");
                for (Map.Entry<String, BigDecimal> e : stats.obtenerVentasPorCategoriaConfiteria().entrySet()) {
                    pieChart.getData().add(new PieChart.Data(e.getKey() + " ($" + e.getValue() + ")", e.getValue().doubleValue()));
                }
                panelGrafico.getChildren().add(pieChart);
            } else if (sel.startsWith("Ventas por Dia")) {
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Fecha");
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Total ($)");
                LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Evolucion de Ventas por Dia");
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                serie.setName("Ventas Diarias");
                for (Map.Entry<LocalDate, BigDecimal> e : stats.obtenerVentasPorDia().entrySet()) {
                    serie.getData().add(new XYChart.Data<>(e.getKey().format(FORMATO_FECHA), e.getValue()));
                }
                lineChart.getData().add(serie);
                panelGrafico.getChildren().add(lineChart);
            } else if (sel.startsWith("Recargas por Dia")) {
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Fecha");
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Total ($)");
                LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Evolucion de Recargas por Dia");
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                serie.setName("Recargas Diarias");
                for (Map.Entry<LocalDate, BigDecimal> e : stats.obtenerRecargasPorDia().entrySet()) {
                    serie.getData().add(new XYChart.Data<>(e.getKey().format(FORMATO_FECHA), e.getValue()));
                }
                lineChart.getData().add(serie);
                panelGrafico.getChildren().add(lineChart);
            }
        };

        cbGraficos.setOnAction(e -> actualizarGrafico.run());
        btnRefrescar.setOnAction(e -> {
            lblMetricas.setText("Total Ventas Realizadas: " + stats.contarVentasRealizadas() +
                    " ($" + stats.calcularTotalVentas() + ") | Reembolsos Realizados: " +
                    stats.contarReembolsosRealizados() + " ($" + stats.calcularTotalReembolsos() + ")");
            actualizarGrafico.run();
        });

        actualizarGrafico.run();

        contenedor.getChildren().addAll(lblMetricas, filaSelector, panelGrafico);
        return contenedor;
    }
}
