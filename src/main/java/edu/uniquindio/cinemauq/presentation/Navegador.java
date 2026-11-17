package edu.uniquindio.cinemauq.presentation;

import edu.uniquindio.cinemauq.domain.Administrador;
import edu.uniquindio.cinemauq.domain.Cliente;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Navegador {
    private final Stage stage;
    private final ContextoApp contexto;

    public Navegador(Stage stage, ContextoApp contexto) {
        if (stage == null || contexto == null) {
            throw new IllegalArgumentException("Stage y ContextoApp son obligatorios");
        }
        this.stage = stage;
        this.contexto = contexto;
    }

    public void navegarALogin() {
        LoginControlador controlador = new LoginControlador(this, contexto.getServicioAutenticacion());
        LoginVista vista = new LoginVista(controlador);
        cambiarEscena("CinemaUQ - Inicio de Sesion", vista.construir(), 450, 320);
    }

    public void navegarARegistro() {
        RegistroControlador controlador = new RegistroControlador(this, contexto.getServicioAutenticacion());
        RegistroVista vista = new RegistroVista(controlador);
        cambiarEscena("CinemaUQ - Registro de Cliente", vista.construir(), 450, 360);
    }

    public void navegarAMenuCliente(Cliente cliente) {
        MenuClienteControlador controlador = new MenuClienteControlador(this, contexto, cliente);
        MenuClienteVista vista = new MenuClienteVista(controlador, cliente);
        cambiarEscena("CinemaUQ - Cliente: " + cliente.getNombre(), vista.construir(), 950, 680);
    }

    public void navegarAMenuAdmin(Administrador admin) {
        MenuAdminControlador controlador = new MenuAdminControlador(this, contexto, admin);
        MenuAdminVista vista = new MenuAdminVista(controlador, admin);
        cambiarEscena("CinemaUQ - Administracion: " + admin.getNombre(), vista.construir(), 1050, 720);
    }

    public void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje != null ? mensaje : "Ha ocurrido un error");
        alert.showAndWait();
    }

    public void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacion");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cambiarEscena(String titulo, Parent raiz, double ancho, double alto) {
        Scene scene = new Scene(raiz, ancho, alto);
        stage.setTitle(titulo);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public ContextoApp getContexto() {
        return contexto;
    }
}
