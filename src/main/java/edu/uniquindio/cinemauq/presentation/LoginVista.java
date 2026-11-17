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

public class LoginVista {
    private final LoginControlador controlador;

    public LoginVista(LoginControlador controlador) {
        this.controlador = controlador;
    }

    public Parent construir() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(20));
        raiz.setAlignment(Pos.CENTER);

        Label lblTitulo = new Label("CinemaUQ");
        Label lblSubtitulo = new Label("Inicio de Sesion");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setAlignment(Pos.CENTER);

        Label lblCorreo = new Label("Correo electronico:");
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("ejemplo@correo.com");

        Label lblClave = new Label("Contrasena:");
        PasswordField txtClave = new PasswordField();
        txtClave.setPromptText("Contrasena");

        formulario.add(lblCorreo, 0, 0);
        formulario.add(txtCorreo, 1, 0);
        formulario.add(lblClave, 0, 1);
        formulario.add(txtClave, 1, 1);

        Button btnIngresar = new Button("Iniciar Sesion");
        btnIngresar.setDefaultButton(true);
        btnIngresar.setOnAction(e -> controlador.iniciarSesion(txtCorreo.getText(), txtClave.getText()));

        Button btnRegistrar = new Button("Crear Cuenta");
        btnRegistrar.setOnAction(e -> controlador.irARegistro());

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnIngresar, btnRegistrar);

        raiz.getChildren().addAll(lblTitulo, lblSubtitulo, formulario, botones);
        return raiz;
    }
}
