package edu.uniquindio.cinemauq.presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RegistroVista {
    private final RegistroControlador controlador;

    public RegistroVista(RegistroControlador controlador) {
        this.controlador = controlador;
    }

    public Parent construir() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(20));
        raiz.setAlignment(Pos.CENTER);

        Label lblTitulo = new Label("CinemaUQ");
        Label lblSubtitulo = new Label("Registro de Nuevo Cliente");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setAlignment(Pos.CENTER);

        Label lblNombre = new Label("Nombre completo:");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre y Apellido");

        Label lblCorreo = new Label("Correo electronico:");
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("ejemplo@correo.com");

        Label lblClave = new Label("Contrasena:");
        PasswordField txtClave = new PasswordField();
        txtClave.setPromptText("Contrasena");

        formulario.add(lblNombre, 0, 0);
        formulario.add(txtNombre, 1, 0);
        formulario.add(lblCorreo, 0, 1);
        formulario.add(txtCorreo, 1, 1);
        formulario.add(lblClave, 0, 2);
        formulario.add(txtClave, 1, 2);

        Button btnRegistrar = new Button("Registrarse");
        btnRegistrar.setDefaultButton(true);
        btnRegistrar.setOnAction(e -> controlador.registrar(txtNombre.getText(), txtCorreo.getText(), txtClave.getText()));

        Button btnVolver = new Button("Volver al Login");
        btnVolver.setOnAction(e -> controlador.irALogin());

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnRegistrar, btnVolver);

        raiz.getChildren().addAll(lblTitulo, lblSubtitulo, formulario, botones);
        return raiz;
    }
}
