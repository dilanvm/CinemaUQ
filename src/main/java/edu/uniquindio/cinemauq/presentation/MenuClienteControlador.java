package edu.uniquindio.cinemauq.presentation;

import edu.uniquindio.cinemauq.application.CompraBuilder;
import edu.uniquindio.cinemauq.application.PoliticaReembolso;
import edu.uniquindio.cinemauq.application.ReembolsoCancelacionCliente;
import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.CinemaUQException;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.LotePuntos;
import edu.uniquindio.cinemauq.domain.MovimientoPuntos;
import edu.uniquindio.cinemauq.domain.MovimientoTarjeta;
import edu.uniquindio.cinemauq.domain.Pelicula;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MenuClienteControlador {
    private final Navegador navegador;
    private final ContextoApp contexto;
    private final Cliente cliente;
    private final PoliticaReembolso politicaReembolsoCliente;

    public MenuClienteControlador(Navegador navegador, ContextoApp contexto, Cliente cliente) {
        if (navegador == null || contexto == null || cliente == null) {
            throw new IllegalArgumentException("Navegador, ContextoApp y Cliente son obligatorios");
        }
        this.navegador = navegador;
        this.contexto = contexto;
        this.cliente = cliente;
        this.politicaReembolsoCliente = new ReembolsoCancelacionCliente();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<Pelicula> listarCartelera() {
        return contexto.getServicioCatalogo().consultarCartelera();
    }

    public List<Funcion> listarFuncionesDePelicula(Long idPelicula) {
        return contexto.getServicioCatalogo().consultarFuncionesFuturasPorPelicula(idPelicula);
    }

    public boolean estaAsientoDisponible(Funcion funcion, Asiento asiento) {
        return contexto.getServicioDisponibilidad().estaDisponible(funcion, asiento);
    }

    public List<ElementoVendible> listarConfiteria() {
        return contexto.getServicioConfiteria().listar();
    }

    public BigDecimal consultarSaldoTarjeta() {
        return contexto.getServicioTarjeta().consultarSaldo(cliente);
    }

    public List<MovimientoTarjeta> consultarMovimientosTarjeta() {
        return contexto.getServicioTarjeta().consultarMovimientos(cliente);
    }

    public int consultarSaldoPuntos() {
        LocalDate hoy = LocalDate.now(contexto.getClock());
        return contexto.getServicioPuntos().consultarSaldo(cliente, hoy);
    }

    public List<LotePuntos> consultarLotesPuntos() {
        return contexto.getServicioPuntos().consultarLotes(cliente);
    }

    public List<MovimientoPuntos> consultarMovimientosPuntos() {
        return contexto.getServicioPuntos().consultarMovimientos(cliente);
    }

    public List<Compra> listarComprasCliente() {
        return contexto.getRepositorioCompras().buscarPorCliente(cliente);
    }

    public List<String> obtenerNotificaciones() {
        return contexto.getNotificadorCompra().obtenerNotificaciones(cliente);
    }

    public BigDecimal calcularPorcentajeReembolso(Compra compra) {
        if (compra == null || compra.getFuncion() == null) {
            return BigDecimal.ZERO;
        }
        try {
            LocalDateTime ahora = LocalDateTime.now(contexto.getClock());
            return politicaReembolsoCliente.calcularPorcentaje(compra.getFuncion().getFechaHora(), ahora);
        } catch (CinemaUQException e) {
            return BigDecimal.ZERO;
        }
    }

    public void cancelarCompra(Compra compra) {
        try {
            if (compra == null) {
                navegador.mostrarError("Debe seleccionar una compra para cancelar");
                return;
            }
            contexto.getServicioCancelacion().cancelarPorCliente(compra);
            navegador.mostrarInformacion("Compra cancelada y reembolso procesado exitosamente");
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
        }
    }

    public Compra realizarCompra(Funcion funcion, List<Asiento> asientos, Map<ElementoVendible, Integer> confiteria, int puntosUsar) {
        try {
            if (funcion == null) {
                navegador.mostrarError("Debe seleccionar una funcion");
                return null;
            }
            if (asientos == null || asientos.isEmpty()) {
                navegador.mostrarError("Debe seleccionar al menos un asiento");
                return null;
            }

            CompraBuilder builder = new CompraBuilder(cliente, contexto.getClock());
            for (Asiento a : asientos) {
                builder.agregarEntrada(funcion, a);
            }
            if (confiteria != null) {
                for (Map.Entry<ElementoVendible, Integer> entry : confiteria.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() > 0) {
                        builder.agregarElemento(entry.getKey(), entry.getValue());
                    }
                }
            }
            builder.usarPuntos(puntosUsar);

            Compra compraConstruida = builder.construir();
            Compra compraRealizada = contexto.getServicioCompra().comprar(compraConstruida);
            navegador.mostrarInformacion("Compra #" + compraRealizada.getId() + " completada con exito!\nTotal: $" + compraRealizada.getTotal() + "\nPagado con tarjeta: $" + compraRealizada.getMontoPagadoTarjeta());
            return compraRealizada;
        } catch (CinemaUQException | IllegalArgumentException e) {
            navegador.mostrarError(e.getMessage());
            return null;
        }
    }

    public void cerrarSesion() {
        navegador.navegarALogin();
    }
}
