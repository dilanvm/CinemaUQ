package edu.uniquindio.cinemauq.presentation;

import edu.uniquindio.cinemauq.application.Reporte;
import edu.uniquindio.cinemauq.application.ServicioEstadisticas;
import edu.uniquindio.cinemauq.application.TipoReporte;
import edu.uniquindio.cinemauq.domain.Administrador;
import edu.uniquindio.cinemauq.domain.CategoriaProducto;
import edu.uniquindio.cinemauq.domain.CinemaUQException;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Combo;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.Producto;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.TipoAsiento;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MenuAdminControlador {
    private final Navegador navegador;
    private final ContextoApp contexto;
    private final Administrador administrador;

    public MenuAdminControlador(Navegador navegador, ContextoApp contexto, Administrador administrador) {
        if (navegador == null || contexto == null || administrador == null) {
            throw new IllegalArgumentException("Navegador, ContextoApp y Administrador son obligatorios");
        }
        this.navegador = navegador;
        this.contexto = contexto;
        this.administrador = administrador;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public List<Pelicula> listarPeliculas() {
        return contexto.getServicioCatalogo().listarPeliculas();
    }

    public void crearPelicula(String titulo, String sinopsis, int duracion, String clasificacion, String genero) {
        try {
            contexto.getServicioCatalogo().crearPelicula(titulo, sinopsis, duracion, clasificacion, genero);
            navegador.mostrarInformacion("Pelicula creada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void editarPelicula(Long id, String titulo, String sinopsis, int duracion, String clasificacion, String genero, boolean activa) {
        try {
            contexto.getServicioCatalogo().editarPelicula(id, titulo, sinopsis, duracion, clasificacion, genero, activa);
            navegador.mostrarInformacion("Pelicula actualizada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void eliminarPelicula(Long id) {
        try {
            contexto.getServicioCatalogo().eliminarPelicula(id);
            navegador.mostrarInformacion("Pelicula eliminada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public List<Sala> listarSalas() {
        return contexto.getServicioCatalogo().listarSalas();
    }

    public void crearSala(String nombre, boolean activa) {
        try {
            contexto.getServicioCatalogo().crearSala(nombre, activa);
            navegador.mostrarInformacion("Sala creada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void editarSala(Long id, String nombre, boolean activa) {
        try {
            contexto.getServicioCatalogo().editarSala(id, nombre, activa);
            navegador.mostrarInformacion("Sala actualizada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void eliminarSala(Long id) {
        try {
            contexto.getServicioCatalogo().eliminarSala(id);
            navegador.mostrarInformacion("Sala eliminada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void agregarAsiento(Long idSala, String fila, int numero, TipoAsiento tipo) {
        try {
            contexto.getServicioCatalogo().agregarAsiento(idSala, fila, numero, tipo);
            navegador.mostrarInformacion("Asiento agregado exitosamente a la sala");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public List<Funcion> listarFunciones() {
        return contexto.getServicioCatalogo().listarFunciones();
    }

    public void crearFuncion(Long idPelicula, Long idSala, LocalDateTime fechaHora, BigDecimal precioBase) {
        try {
            contexto.getServicioCatalogo().crearFuncion(idPelicula, idSala, fechaHora, precioBase);
            navegador.mostrarInformacion("Funcion programada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void crearFuncionDesdePlantilla(LocalDateTime fechaHora) {
        try {
            if (contexto.getPlantillaFuncion() == null) {
                navegador.mostrarError("No hay ninguna plantilla de funcion configurada");
                return;
            }
            contexto.getServicioCatalogo().crearFuncionDesdePlantilla(contexto.getPlantillaFuncion(), fechaHora);
            navegador.mostrarInformacion("Funcion creada desde plantilla exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void editarFuncion(Long id, LocalDateTime fechaHora, BigDecimal precioBase) {
        try {
            contexto.getServicioCatalogo().editarFuncion(id, fechaHora, precioBase);
            navegador.mostrarInformacion("Funcion actualizada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void eliminarFuncion(Long id) {
        try {
            contexto.getServicioCatalogo().eliminarFuncion(id);
            navegador.mostrarInformacion("Funcion eliminada exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void cancelarFuncion(Long id) {
        try {
            Funcion funcion = contexto.getServicioCatalogo().buscarFuncionPorId(id);
            contexto.getServicioCancelacion().cancelarFuncion(funcion);
            navegador.mostrarInformacion("Funcion cancelada y compras reembolsadas al 100%");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public List<ElementoVendible> listarConfiteria() {
        return contexto.getServicioConfiteria().listar();
    }

    public void crearProducto(String nombre, String desc, BigDecimal precio, boolean disponible, CategoriaProducto cat) {
        try {
            contexto.getServicioConfiteria().crearProducto(nombre, desc, precio, disponible, cat);
            navegador.mostrarInformacion("Producto creado exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void editarProducto(String nombreActual, String nuevoNombre, String desc, BigDecimal precio, boolean disp, CategoriaProducto cat) {
        try {
            contexto.getServicioConfiteria().editarProducto(nombreActual, nuevoNombre, desc, precio, disp, cat);
            navegador.mostrarInformacion("Producto actualizado exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void eliminarProducto(String nombre) {
        try {
            contexto.getServicioConfiteria().eliminarProducto(nombre);
            navegador.mostrarInformacion("Elemento de confiteria eliminado");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void crearCombo(String nombre, List<ElementoVendible> elementos) {
        try {
            contexto.getServicioConfiteria().crearCombo(nombre, elementos);
            navegador.mostrarInformacion("Combo creado exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void aplicarDescuentoPorcentaje(String nombre, BigDecimal porcentaje) {
        try {
            contexto.getServicioConfiteria().aplicarDescuentoPorcentaje(nombre, porcentaje);
            navegador.mostrarInformacion("Descuento porcentual aplicado exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void aplicarDescuentoValorFijo(String nombre, BigDecimal valor) {
        try {
            contexto.getServicioConfiteria().aplicarDescuentoValorFijo(nombre, valor);
            navegador.mostrarInformacion("Descuento fijo aplicado exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void quitarPromocion(String nombre) {
        try {
            contexto.getServicioConfiteria().quitarPromocion(nombre);
            navegador.mostrarInformacion("Promocion removida exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public List<Cliente> listarClientes() {
        return contexto.getRepositorioUsuarios().listarClientes();
    }

    public List<Compra> listarCompras() {
        return contexto.getRepositorioCompras().listar();
    }

    public void recargarTarjeta(Cliente cliente, BigDecimal monto) {
        try {
            if (cliente == null) {
                navegador.mostrarError("Debe seleccionar un cliente");
                return;
            }
            contexto.getServicioTarjeta().recargar(administrador, cliente, monto);
            navegador.mostrarInformacion("Recarga de $" + monto.toPlainString() + " realizada exitosamente para " + cliente.getNombre());
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public void cambiarEstadoTarjeta(Cliente cliente, boolean activa) {
        try {
            if (cliente == null) {
                navegador.mostrarError("Debe seleccionar un cliente");
                return;
            }
            contexto.getServicioTarjeta().cambiarEstado(administrador, cliente, activa);
            navegador.mostrarInformacion("Tarjeta " + (activa ? "activada" : "desactivada") + " exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public Reporte generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        try {
            return contexto.getServicioReportes().generarReporte(tipo, desde, hasta);
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
            return null;
        }
    }

    public void exportarTxt(Reporte reporte, Path destino) {
        try {
            contexto.getServicioReportes().exportarTxt(reporte, destino);
            navegador.mostrarInformacion("Reporte exportado exitosamente a: " + destino.getFileName());
        } catch (IOException | CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError("Error al exportar reporte TXT: " + e.getMessage());
        }
    }

    public void exportarPdf(Reporte reporte, Path destino) {
        try {
            contexto.getServicioReportes().exportarPdf(reporte, destino);
            navegador.mostrarInformacion("Reporte exportado exitosamente a: " + destino.getFileName());
        } catch (IOException | CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError("Error al exportar reporte PDF: " + e.getMessage());
        }
    }

    public ServicioEstadisticas getServicioEstadisticas() {
        return contexto.getServicioEstadisticas();
    }

    public void cerrarSesion() {
        navegador.navegarALogin();
    }
}
