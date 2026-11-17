package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Combo;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.DetalleCompra;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.MovimientoTarjeta;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.Producto;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.RepositorioFunciones;
import edu.uniquindio.cinemauq.domain.RepositorioPeliculas;
import edu.uniquindio.cinemauq.domain.RepositorioSalas;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.TipoMovimiento;
import edu.uniquindio.cinemauq.domain.estado.EstadoCancelada;
import edu.uniquindio.cinemauq.domain.estado.EstadoConfirmada;
import edu.uniquindio.cinemauq.domain.estado.EstadoReembolsada;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ServicioEstadisticas {
    private final RepositorioCompras repositorioCompras;
    private final RepositorioPeliculas repositorioPeliculas;
    private final RepositorioSalas repositorioSalas;
    private final RepositorioFunciones repositorioFunciones;
    private final RepositorioUsuarios repositorioUsuarios;

    public ServicioEstadisticas(RepositorioCompras repositorioCompras,
                                RepositorioPeliculas repositorioPeliculas,
                                RepositorioSalas repositorioSalas,
                                RepositorioFunciones repositorioFunciones,
                                RepositorioUsuarios repositorioUsuarios) {
        if (repositorioCompras == null || repositorioPeliculas == null || repositorioSalas == null || repositorioFunciones == null || repositorioUsuarios == null) {
            throw new IllegalArgumentException("Todos los repositorios son obligatorios");
        }
        this.repositorioCompras = repositorioCompras;
        this.repositorioPeliculas = repositorioPeliculas;
        this.repositorioSalas = repositorioSalas;
        this.repositorioFunciones = repositorioFunciones;
        this.repositorioUsuarios = repositorioUsuarios;
    }

    public BigDecimal calcularTotalVentas() {
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                total = total.add(c.getTotal()).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return total;
    }

    public int contarVentasRealizadas() {
        int count = 0;
        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                count++;
            }
        }
        return count;
    }

    public int contarReembolsosRealizados() {
        int count = 0;
        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoCancelada || c.getEstado() instanceof EstadoReembolsada) {
                count++;
            }
        }
        return count;
    }

    public BigDecimal calcularTotalReembolsos() {
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoCancelada || c.getEstado() instanceof EstadoReembolsada) {
                total = total.add(c.getMontoPagadoTarjeta()).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return total;
    }

    public Map<String, Integer> obtenerEntradasPorPelicula() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Pelicula p : repositorioPeliculas.listar()) {
            resultado.put(p.getTitulo(), 0);
        }
        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoConfirmada && c.getFuncion() != null && c.getFuncion().getPelicula() != null) {
                String titulo = c.getFuncion().getPelicula().getTitulo();
                resultado.put(titulo, resultado.getOrDefault(titulo, 0) + c.getEntradas().size());
            }
        }
        return resultado;
    }

    public Map<String, BigDecimal> obtenerVentasPorPelicula() {
        Map<String, BigDecimal> resultado = new LinkedHashMap<>();
        for (Pelicula p : repositorioPeliculas.listar()) {
            resultado.put(p.getTitulo(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }
        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoConfirmada && c.getFuncion() != null && c.getFuncion().getPelicula() != null) {
                String titulo = c.getFuncion().getPelicula().getTitulo();
                BigDecimal actual = resultado.getOrDefault(titulo, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                resultado.put(titulo, actual.add(c.getTotal()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        return resultado;
    }

    public Map<String, Integer> obtenerProductosMasVendidos() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                for (DetalleCompra d : c.getDetalles()) {
                    String nombre = d.getElemento().getNombre();
                    resultado.put(nombre, resultado.getOrDefault(nombre, 0) + d.getCantidad());
                }
            }
        }
        return resultado;
    }

    public Map<String, Double> obtenerOcupacionPorSala() {
        Map<String, Double> resultado = new LinkedHashMap<>();
        List<Sala> salas = repositorioSalas.listar();
        List<Funcion> funciones = repositorioFunciones.listar();
        List<Compra> compras = repositorioCompras.listar();

        for (Sala s : salas) {
            int nFunciones = 0;
            for (Funcion f : funciones) {
                if (f.getSala() != null && f.getSala().getId().equals(s.getId())) {
                    nFunciones++;
                }
            }
            int capTotal = s.getCapacidad() * nFunciones;
            int entradasVendidas = 0;
            for (Compra c : compras) {
                if (c.getEstado() instanceof EstadoConfirmada && c.getFuncion() != null && c.getFuncion().getSala() != null) {
                    if (c.getFuncion().getSala().getId().equals(s.getId())) {
                        entradasVendidas += c.getEntradas().size();
                    }
                }
            }
            double pct = capTotal > 0 ? (entradasVendidas * 100.0) / capTotal : 0.0;
            resultado.put(s.getNombre(), Math.round(pct * 100.0) / 100.0);
        }
        return resultado;
    }

    public Map<String, BigDecimal> obtenerVentasPorCategoriaConfiteria() {
        Map<String, BigDecimal> resultado = new LinkedHashMap<>();
        resultado.put("COMIDA", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        resultado.put("BEBIDA", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        resultado.put("DULCE", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        resultado.put("COMBOS", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                for (DetalleCompra d : c.getDetalles()) {
                    ElementoVendible elem = d.getElemento();
                    if (elem instanceof PromocionDecorator dec) {
                        elem = dec.getElementoOriginal();
                    }
                    String categoria = "COMIDA";
                    if (elem instanceof Combo) {
                        categoria = "COMBOS";
                    } else if (elem instanceof Producto p) {
                        categoria = p.getCategoria().name();
                    }
                    BigDecimal actual = resultado.getOrDefault(categoria, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                    resultado.put(categoria, actual.add(d.getSubtotal()).setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
        return resultado;
    }

    public Map<LocalDate, BigDecimal> obtenerVentasPorDia() {
        Map<LocalDate, BigDecimal> resultado = new TreeMap<>();
        for (Compra c : repositorioCompras.listar()) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                LocalDate dia = c.getFechaCompra().toLocalDate();
                BigDecimal actual = resultado.getOrDefault(dia, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                resultado.put(dia, actual.add(c.getTotal()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        return resultado;
    }

    public Map<LocalDate, BigDecimal> obtenerRecargasPorDia() {
        Map<LocalDate, BigDecimal> resultado = new TreeMap<>();
        for (Cliente cliente : repositorioUsuarios.listarClientes()) {
            if (cliente.getTarjetaVirtual() != null) {
                for (MovimientoTarjeta m : cliente.getTarjetaVirtual().consultarMovimientos()) {
                    if (m.getTipo() == TipoMovimiento.RECARGA) {
                        LocalDate dia = m.getFechaHora().toLocalDate();
                        BigDecimal actual = resultado.getOrDefault(dia, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                        resultado.put(dia, actual.add(m.getMonto()).setScale(2, RoundingMode.HALF_UP));
                    }
                }
            }
        }
        return resultado;
    }
}
