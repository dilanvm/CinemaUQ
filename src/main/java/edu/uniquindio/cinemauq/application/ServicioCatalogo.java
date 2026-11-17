package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.EntidadNoEncontradaException;
import edu.uniquindio.cinemauq.domain.EstadoFuncion;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.FuncionInvalidaException;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.RepositorioFunciones;
import edu.uniquindio.cinemauq.domain.RepositorioPeliculas;
import edu.uniquindio.cinemauq.domain.RepositorioSalas;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.TipoAsiento;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ServicioCatalogo {
    private final RepositorioPeliculas repositorioPeliculas;
    private final RepositorioSalas repositorioSalas;
    private final RepositorioFunciones repositorioFunciones;
    private final RepositorioCompras repositorioCompras;
    private final Clock clock;

    public ServicioCatalogo(RepositorioPeliculas repositorioPeliculas,
                            RepositorioSalas repositorioSalas,
                            RepositorioFunciones repositorioFunciones,
                            RepositorioCompras repositorioCompras) {
        this(repositorioPeliculas, repositorioSalas, repositorioFunciones, repositorioCompras, Clock.system(ZoneId.of("America/Bogota")));
    }

    public ServicioCatalogo(RepositorioPeliculas repositorioPeliculas,
                            RepositorioSalas repositorioSalas,
                            RepositorioFunciones repositorioFunciones,
                            RepositorioCompras repositorioCompras,
                            Clock clock) {
        if (repositorioPeliculas == null || repositorioSalas == null || repositorioFunciones == null || repositorioCompras == null) {
            throw new IllegalArgumentException("Todos los repositorios son obligatorios");
        }
        this.repositorioPeliculas = repositorioPeliculas;
        this.repositorioSalas = repositorioSalas;
        this.repositorioFunciones = repositorioFunciones;
        this.repositorioCompras = repositorioCompras;
        this.clock = clock != null ? clock : Clock.system(ZoneId.of("America/Bogota"));
    }

    public Pelicula crearPelicula(String titulo, String sinopsis, int duracionMinutos, String clasificacionEdad, String genero) {
        Pelicula pelicula = new Pelicula(null, titulo, sinopsis, duracionMinutos, clasificacionEdad, genero, true);
        return repositorioPeliculas.guardar(pelicula);
    }

    public Pelicula editarPelicula(Long id, String titulo, String sinopsis, int duracionMinutos, String clasificacionEdad, String genero, boolean activa) {
        Pelicula pelicula = buscarPeliculaPorId(id);
        pelicula.setTitulo(titulo);
        pelicula.setSinopsis(sinopsis);
        pelicula.setDuracionMinutos(duracionMinutos);
        pelicula.setClasificacionEdad(clasificacionEdad);
        pelicula.setGenero(genero);
        pelicula.setActiva(activa);
        return repositorioPeliculas.guardar(pelicula);
    }

    public void eliminarPelicula(Long id) {
        buscarPeliculaPorId(id);
        repositorioPeliculas.eliminar(id);
    }

    public Pelicula buscarPeliculaPorId(Long id) {
        return repositorioPeliculas.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pelicula no encontrada con id: " + id));
    }

    public List<Pelicula> listarPeliculas() {
        return repositorioPeliculas.listar();
    }

    public Sala crearSala(String nombre, boolean activa) {
        Sala sala = new Sala(nombre, null, new ArrayList<>(), activa);
        return repositorioSalas.guardar(sala);
    }

    public Sala editarSala(Long id, String nombre, boolean activa) {
        Sala sala = buscarSalaPorId(id);
        sala.setNombre(nombre);
        sala.setActiva(activa);
        return repositorioSalas.guardar(sala);
    }

    public void eliminarSala(Long id) {
        buscarSalaPorId(id);
        repositorioSalas.eliminar(id);
    }

    public Sala buscarSalaPorId(Long id) {
        return repositorioSalas.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Sala no encontrada con id: " + id));
    }

    public List<Sala> listarSalas() {
        return repositorioSalas.listar();
    }

    public void agregarAsiento(Long idSala, String fila, int numero, TipoAsiento tipo) {
        Sala sala = buscarSalaPorId(idSala);
        Asiento asiento = new Asiento(null, fila, numero, tipo);
        sala.agregarAsiento(asiento);
        repositorioSalas.guardar(sala);
    }

    public Funcion crearFuncion(Long idPelicula, Long idSala, LocalDateTime fechaHora, BigDecimal precioBase) {
        Pelicula pelicula = buscarPeliculaPorId(idPelicula);
        Sala sala = buscarSalaPorId(idSala);
        validarCruceHorario(sala, fechaHora, pelicula.getDuracionMinutos(), null);
        Funcion funcion = new Funcion(null, pelicula, sala, fechaHora, precioBase, EstadoFuncion.PROGRAMADA);
        return repositorioFunciones.guardar(funcion);
    }

    public Funcion crearFuncionDesdePlantilla(PlantillaFuncion plantilla, LocalDateTime fechaHora) {
        if (plantilla == null) {
            throw new IllegalArgumentException("La plantilla es obligatoria");
        }
        validarCruceHorario(plantilla.getSala(), fechaHora, plantilla.getDuracionMinutos(), null);
        Funcion funcion = plantilla.clonarParaFecha(fechaHora);
        return repositorioFunciones.guardar(funcion);
    }

    public Funcion editarFuncion(Long id, LocalDateTime fechaHora, BigDecimal precioBase) {
        Funcion funcion = buscarFuncionPorId(id);
        validarCruceHorario(funcion.getSala(), fechaHora, funcion.getPelicula().getDuracionMinutos(), funcion.getId());
        funcion.setFechaHora(fechaHora);
        funcion.setPrecioBase(precioBase);
        return repositorioFunciones.guardar(funcion);
    }

    public void eliminarFuncion(Long id) {
        Funcion funcion = buscarFuncionPorId(id);
        List<Compra> compras = repositorioCompras.buscarPorFuncion(funcion);
        if (!compras.isEmpty()) {
            throw new FuncionInvalidaException("No se puede eliminar una funcion que tiene compras");
        }
        repositorioFunciones.eliminar(id);
    }

    public Funcion buscarFuncionPorId(Long id) {
        return repositorioFunciones.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Funcion no encontrada con id: " + id));
    }

    public List<Funcion> listarFunciones() {
        return repositorioFunciones.listar();
    }

    public List<Pelicula> consultarCartelera() {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        List<Pelicula> cartelera = new ArrayList<>();
        List<Pelicula> todasPeliculas = repositorioPeliculas.listar();
        List<Funcion> todasFunciones = repositorioFunciones.listar();

        for (Pelicula p : todasPeliculas) {
            if (p.isActiva()) {
                boolean tieneFuncionesFuturas = false;
                for (Funcion f : todasFunciones) {
                    if (f.getPelicula().getId().equals(p.getId())
                            && f.getEstadoFuncion() == EstadoFuncion.PROGRAMADA
                            && f.getFechaHora().isAfter(ahora)) {
                        tieneFuncionesFuturas = true;
                        break;
                    }
                }
                if (tieneFuncionesFuturas) {
                    cartelera.add(p);
                }
            }
        }
        return cartelera;
    }

    public List<Funcion> consultarFuncionesFuturasPorPelicula(Long idPelicula) {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion f : repositorioFunciones.listar()) {
            if (f.getPelicula().getId().equals(idPelicula)
                    && f.getEstadoFuncion() == EstadoFuncion.PROGRAMADA
                    && f.getFechaHora().isAfter(ahora)) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    private void validarCruceHorario(Sala sala, LocalDateTime inicioNueva, int duracionNueva, Long idFuncionExcluir) {
        LocalDateTime finNueva = inicioNueva.plusMinutes(duracionNueva);
        List<Funcion> funciones = repositorioFunciones.listar();

        for (Funcion f : funciones) {
            if (idFuncionExcluir != null && f.getId() != null && f.getId().equals(idFuncionExcluir)) {
                continue;
            }
            if (f.getSala().getId().equals(sala.getId()) && f.getEstadoFuncion() != EstadoFuncion.CANCELADA) {
                LocalDateTime inicioExistente = f.getFechaHora();
                LocalDateTime finExistente = inicioExistente.plusMinutes(f.getPelicula().getDuracionMinutos());

                boolean cruce = inicioNueva.isBefore(finExistente) && finNueva.isAfter(inicioExistente);
                if (cruce) {
                    throw new FuncionInvalidaException("Existe un cruce de horario con la funcion #" + f.getId() + " en la sala " + sala.getNombre());
                }
            }
        }
    }
}
