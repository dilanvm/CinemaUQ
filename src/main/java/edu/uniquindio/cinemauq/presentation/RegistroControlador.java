package edu.uniquindio.cinemauq.presentation;

import edu.uniquindio.cinemauq.application.ServicioAutenticacion;
import edu.uniquindio.cinemauq.domain.CinemaUQException;
import edu.uniquindio.cinemauq.domain.Cliente;

public class RegistroControlador {
    private final Navegador navegador;
    private final ServicioAutenticacion servicioAutenticacion;

    public RegistroControlador(Navegador navegador, ServicioAutenticacion servicioAutenticacion) {
        this.navegador = navegador;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    public void registrar(String nombre, String correo, String contrasena) {
        try {
            if (nombre == null || nombre.trim().isEmpty() || correo == null || correo.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
                navegador.mostrarError("Todos los campos son obligatorios");
                return;
            }
            Cliente cliente = servicioAutenticacion.registrar(nombre.trim(), correo.trim(), contrasena.trim());
            navegador.mostrarInformacion("Registro exitoso. Bienvenido a CinemaUQ.");
            navegador.navegarAMenuCliente(cliente);
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void irALogin() {
        navegador.navegarALogin();
    }
}
