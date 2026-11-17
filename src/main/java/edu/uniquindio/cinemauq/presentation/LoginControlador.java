package edu.uniquindio.cinemauq.presentation;

import edu.uniquindio.cinemauq.application.ServicioAutenticacion;
import edu.uniquindio.cinemauq.domain.Administrador;
import edu.uniquindio.cinemauq.domain.CinemaUQException;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Usuario;

public class LoginControlador {
    private final Navegador navegador;
    private final ServicioAutenticacion servicioAutenticacion;

    public LoginControlador(Navegador navegador, ServicioAutenticacion servicioAutenticacion) {
        this.navegador = navegador;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    public void iniciarSesion(String correo, String contrasena) {
        try {
            if (correo == null || correo.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
                navegador.mostrarError("El correo y la contrasena son obligatorios");
                return;
            }
            Usuario usuario = servicioAutenticacion.iniciarSesion(correo.trim(), contrasena.trim());
            if (usuario instanceof Cliente cliente) {
                navegador.navegarAMenuCliente(cliente);
            } else if (usuario instanceof Administrador admin) {
                navegador.navegarAMenuAdmin(admin);
            }
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void irARegistro() {
        navegador.navegarARegistro();
    }
}
