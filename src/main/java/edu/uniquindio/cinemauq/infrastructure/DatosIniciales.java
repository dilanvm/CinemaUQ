package edu.uniquindio.cinemauq.infrastructure;

import edu.uniquindio.cinemauq.application.CompraBuilder;
import edu.uniquindio.cinemauq.application.DescuentoPorcentaje;
import edu.uniquindio.cinemauq.application.DescuentoValorFijo;
import edu.uniquindio.cinemauq.application.PlantillaFuncion;
import edu.uniquindio.cinemauq.application.PublicadorCompras;
import edu.uniquindio.cinemauq.application.ReembolsoCancelacionCliente;
import edu.uniquindio.cinemauq.application.ServicioCancelacion;
import edu.uniquindio.cinemauq.application.ServicioCompra;
import edu.uniquindio.cinemauq.application.ServicioDisponibilidad;
import edu.uniquindio.cinemauq.domain.Administrador;
import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.CategoriaProducto;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Combo;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.EstadoFuncion;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.LotePuntos;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.Producto;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.RepositorioConfiteria;
import edu.uniquindio.cinemauq.domain.RepositorioFunciones;
import edu.uniquindio.cinemauq.domain.RepositorioPeliculas;
import edu.uniquindio.cinemauq.domain.RepositorioSalas;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.TipoAsiento;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DatosIniciales {
    public static void cargar(RepositorioUsuarios repositorioUsuarios) {
        cargar(repositorioUsuarios, Clock.system(ZoneId.of("America/Bogota")));
    }

    public static void cargar(RepositorioUsuarios repositorioUsuarios, Clock clock) {
        if (repositorioUsuarios == null) {
            throw new IllegalArgumentException("El repositorio de usuarios es obligatorio");
        }
        Clock reloj = clock != null ? clock : Clock.system(ZoneId.of("America/Bogota"));
        LocalDate hoy = LocalDate.now(reloj);

        Administrador administrador = new Administrador(null, "Administrador Principal", "admin@cinemauq.com", "admin123");
        repositorioUsuarios.guardar(administrador);

        Cliente cliente1 = new Cliente(null, "Cliente Uno", "cliente1@correo.com", "1234");
        cliente1.getTarjetaVirtual().recargar(new BigDecimal("150000.00"));
        cliente1.getCuentaPuntos().acumular(500, hoy, "Lote inicial cliente 1");
        repositorioUsuarios.guardar(cliente1);

        Cliente cliente2 = new Cliente(null, "Cliente Dos", "cliente2@correo.com", "1234");
        cliente2.getTarjetaVirtual().recargar(new BigDecimal("100000.00"));
        cliente2.getCuentaPuntos().acumular(300, hoy, "Lote vigente cliente 2");
        LotePuntos loteProximoVencer = new LotePuntos(200, 200, hoy.minusDays(360), hoy.plusDays(5));
        cliente2.getCuentaPuntos().agregarLote(loteProximoVencer);
        repositorioUsuarios.guardar(cliente2);

        Cliente cliente3 = new Cliente(null, "Cliente Tres", "cliente3@correo.com", "1234");
        cliente3.getTarjetaVirtual().recargar(new BigDecimal("80000.00"));
        cliente3.getTarjetaVirtual().desactivar();
        cliente3.getCuentaPuntos().acumular(100, hoy, "Lote inicial cliente 3");
        repositorioUsuarios.guardar(cliente3);

        Cliente cliente4 = new Cliente(null, "Cliente Cuatro", "cliente4@correo.com", "1234");
        cliente4.getTarjetaVirtual().recargar(new BigDecimal("50000.00"));
        cliente4.getCuentaPuntos().acumular(150, hoy, "Lote inicial cliente 4");
        repositorioUsuarios.guardar(cliente4);
    }

    public static PlantillaFuncion cargarTodo(RepositorioUsuarios repositorioUsuarios,
                                             RepositorioPeliculas repositorioPeliculas,
                                             RepositorioSalas repositorioSalas,
                                             RepositorioFunciones repositorioFunciones,
                                             RepositorioConfiteria repositorioConfiteria,
                                             RepositorioCompras repositorioCompras,
                                             ServicioDisponibilidad servicioDisponibilidad,
                                             PublicadorCompras publicadorCompras,
                                             Clock clock) {
        Clock reloj = clock != null ? clock : Clock.system(ZoneId.of("America/Bogota"));
        cargar(repositorioUsuarios, reloj);

        Pelicula p1 = repositorioPeliculas.guardar(new Pelicula(null, "Interestelar", "Un grupo de exploradores viaja a traves de un agujero de gusano en el espacio.", 169, "+12", "Ciencia Ficcion", true));
        Pelicula p2 = repositorioPeliculas.guardar(new Pelicula(null, "El Padrino", "El patriarca de una dinastia del crimen organizado transfiere el control a su hijo.", 175, "+18", "Drama", true));
        Pelicula p3 = repositorioPeliculas.guardar(new Pelicula(null, "Toy Story", "Las aventuras de un grupo de juguetes que cobran vida cuando los humanos no estan.", 81, "TP", "Animacion", true));
        Pelicula p4 = repositorioPeliculas.guardar(new Pelicula(null, "Inception", "Un ladron que roba secretos corporativos mediante el uso de la tecnologia de suenos.", 148, "+14", "Accion", true));
        Pelicula p5 = repositorioPeliculas.guardar(new Pelicula(null, "Coco", "Un nino viaja a la tierra de los muertos para descubrir el legado de su familia.", 105, "TP", "Animacion", true));
        Pelicula p6 = repositorioPeliculas.guardar(new Pelicula(null, "Gladiador", "Un general romano traicionado busca venganza contra el corrupto emperador.", 155, "+16", "Accion", true));

        String[] filas = {"A", "B", "C", "D", "E", "F", "G", "H"};
        Sala sala1 = new Sala("Sala Tradicional 1", null, new ArrayList<>(), true);
        Sala sala2 = new Sala("Sala MacroXE 2", null, new ArrayList<>(), true);
        Sala sala3 = new Sala("Sala VIP 3", null, new ArrayList<>(), true);

        long idAsiento = 1;
        for (Sala s : List.of(sala1, sala2, sala3)) {
            for (String fila : filas) {
                TipoAsiento tipo = (fila.equals("G") || fila.equals("H")) ? TipoAsiento.PREFERENCIAL : TipoAsiento.GENERAL;
                for (int num = 1; num <= 12; num++) {
                    s.agregarAsiento(new Asiento(idAsiento++, fila, num, tipo));
                }
            }
        }
        sala1 = repositorioSalas.guardar(sala1);
        sala2 = repositorioSalas.guardar(sala2);
        sala3 = repositorioSalas.guardar(sala3);

        LocalDateTime ahora = LocalDateTime.now(reloj);

        Funcion f1 = repositorioFunciones.guardar(new Funcion(null, p1, sala1, ahora.plusMinutes(45), new BigDecimal("12000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f2 = repositorioFunciones.guardar(new Funcion(null, p2, sala2, ahora.plusHours(5), new BigDecimal("15000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f3 = repositorioFunciones.guardar(new Funcion(null, p3, sala3, ahora.plusHours(12), new BigDecimal("11000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f4 = repositorioFunciones.guardar(new Funcion(null, p4, sala1, ahora.plusDays(1).withHour(15).withMinute(0), new BigDecimal("14000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f5 = repositorioFunciones.guardar(new Funcion(null, p5, sala2, ahora.plusDays(1).withHour(19).withMinute(0), new BigDecimal("11000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f6 = repositorioFunciones.guardar(new Funcion(null, p6, sala3, ahora.plusDays(2).withHour(14).withMinute(0), new BigDecimal("16000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f7 = repositorioFunciones.guardar(new Funcion(null, p1, sala1, ahora.plusDays(2).withHour(18).withMinute(0), new BigDecimal("13000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f8 = repositorioFunciones.guardar(new Funcion(null, p2, sala2, ahora.plusDays(3).withHour(16).withMinute(0), new BigDecimal("15000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f9 = repositorioFunciones.guardar(new Funcion(null, p3, sala3, ahora.plusDays(3).withHour(20).withMinute(0), new BigDecimal("12000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f10 = repositorioFunciones.guardar(new Funcion(null, p4, sala1, ahora.plusDays(4).withHour(15).withMinute(30), new BigDecimal("14000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f11 = repositorioFunciones.guardar(new Funcion(null, p5, sala2, ahora.plusDays(4).withHour(19).withMinute(30), new BigDecimal("11000.00"), EstadoFuncion.PROGRAMADA));
        Funcion f12 = repositorioFunciones.guardar(new Funcion(null, p6, sala3, ahora.plusDays(5).withHour(17).withMinute(0), new BigDecimal("16000.00"), EstadoFuncion.PROGRAMADA));

        Producto prod1 = new Producto(null, "Crispetas Grandes", "Balde de crispetas con mantequilla", new BigDecimal("14000.00"), true, CategoriaProducto.COMIDA);
        Producto prod2 = new Producto(null, "Crispetas Medianas", "Bolsa de crispetas con mantequilla", new BigDecimal("11000.00"), true, CategoriaProducto.COMIDA);
        Producto prod3 = new Producto(null, "Gaseosa Grande", "Vaso de gaseosa 32oz", new BigDecimal("8000.00"), true, CategoriaProducto.BEBIDA);
        Producto prod4 = new Producto(null, "Gaseosa Mediana", "Vaso de gaseosa 22oz", new BigDecimal("6500.00"), true, CategoriaProducto.BEBIDA);
        Producto prod5 = new Producto(null, "Agua Mineral", "Botella de agua 600ml", new BigDecimal("4500.00"), true, CategoriaProducto.BEBIDA);
        Producto prod6 = new Producto(null, "Perro Caliente", "Perro caliente con salsas", new BigDecimal("12000.00"), true, CategoriaProducto.COMIDA);
        Producto prod7 = new Producto(null, "Nachos con Queso", "Nachos con queso cheddar caliente", new BigDecimal("10000.00"), true, CategoriaProducto.COMIDA);
        Producto prod8 = new Producto(null, "Chocolatina", "Barra de chocolate con leche", new BigDecimal("5000.00"), true, CategoriaProducto.DULCE);

        repositorioConfiteria.guardar(prod1);
        repositorioConfiteria.guardar(prod2);
        repositorioConfiteria.guardar(prod3);
        repositorioConfiteria.guardar(prod4);
        repositorioConfiteria.guardar(prod5);
        repositorioConfiteria.guardar(prod6);
        repositorioConfiteria.guardar(prod7);
        repositorioConfiteria.guardar(prod8);

        Combo combo1 = new Combo(null, List.of(prod1, prod4, prod4), "Combo Pareja");
        Combo combo2 = new Combo(null, List.of(prod7, prod8), "Combo Dulce");
        Combo combo3 = new Combo(null, List.of(combo1, prod6, prod5), "Super Combo Familiar");

        repositorioConfiteria.guardar(combo1);
        repositorioConfiteria.guardar(combo2);
        repositorioConfiteria.guardar(combo3);

        DescuentoPorcentaje promo1 = new DescuentoPorcentaje(prod1, new BigDecimal("10.00"));
        repositorioConfiteria.guardar(promo1);

        DescuentoValorFijo promo2 = new DescuentoValorFijo(combo1, new BigDecimal("2000.00"));
        repositorioConfiteria.guardar(promo2);

        PlantillaFuncion plantilla = new PlantillaFuncion(p1, sala1, new BigDecimal("15000.00"), p1.getDuracionMinutos());

        ServicioCompra servicioCompra = new ServicioCompra(repositorioCompras, servicioDisponibilidad, publicadorCompras, reloj);
        ServicioCancelacion servicioCancelacion = new ServicioCancelacion(repositorioCompras, servicioDisponibilidad, publicadorCompras, new ReembolsoCancelacionCliente(), reloj);

        Cliente c1 = (Cliente) repositorioUsuarios.buscarPorCorreo("cliente1@correo.com").orElse(null);
        Cliente c2 = (Cliente) repositorioUsuarios.buscarPorCorreo("cliente2@correo.com").orElse(null);

        if (c1 != null) {
            Asiento a1 = f4.getSala().getAsientos().get(0);
            Asiento a2 = f4.getSala().getAsientos().get(1);
            Compra compra1 = new CompraBuilder(c1, reloj)
                    .agregarEntrada(f4, a1)
                    .agregarEntrada(f4, a2)
                    .agregarElemento(prod2, 1)
                    .usarPuntos(0)
                    .construir();
            servicioCompra.comprar(compra1);

            Asiento a3 = f7.getSala().getAsientos().get(2);
            Compra compra2 = new CompraBuilder(c1, reloj)
                    .agregarEntrada(f7, a3)
                    .usarPuntos(0)
                    .construir();
            Compra compra2Realizada = servicioCompra.comprar(compra2);
            servicioCancelacion.cancelarPorCliente(compra2Realizada);
        }

        if (c2 != null) {
            Asiento a4 = f6.getSala().getAsientos().get(0);
            Asiento a5 = f6.getSala().getAsientos().get(1);
            Compra compra3 = new CompraBuilder(c2, reloj)
                    .agregarEntrada(f6, a4)
                    .agregarEntrada(f6, a5)
                    .agregarElemento(combo2, 1)
                    .usarPuntos(0)
                    .construir();
            servicioCompra.comprar(compra3);
        }

        return plantilla;
    }
}
