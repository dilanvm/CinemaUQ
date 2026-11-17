package edu.uniquindio.cinemauq;

import edu.uniquindio.cinemauq.application.DescuentoPorcentaje;
import edu.uniquindio.cinemauq.application.DescuentoValorFijo;
import edu.uniquindio.cinemauq.domain.CategoriaProducto;
import edu.uniquindio.cinemauq.domain.Combo;
import edu.uniquindio.cinemauq.domain.Producto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompositeDecoratorTest {

    @Test
    public void testComboAnidadoPrecioYContenido() {
        Producto crispetas = new Producto(1L, "Crispetas", "Maiz pisingallo", new BigDecimal("10000.00"), true, CategoriaProducto.COMIDA);
        Producto gaseosa = new Producto(2L, "Gaseosa", "Bebida cola", new BigDecimal("5000.00"), true, CategoriaProducto.BEBIDA);
        Producto chocolate = new Producto(3L, "Chocolatina", "Chocolate con leche", new BigDecimal("4000.00"), true, CategoriaProducto.DULCE);

        Combo comboBasico = new Combo(10L, List.of(crispetas, gaseosa), "Combo Basico");
        assertEquals(new BigDecimal("15000.00"), comboBasico.getPrecio());

        Combo comboAnidado = new Combo(20L, List.of(comboBasico, chocolate), "Combo Familiar");
        assertEquals(new BigDecimal("19000.00"), comboAnidado.getPrecio());

        String contenido = comboAnidado.getContenido();
        assertTrue(contenido.contains("Combo Basico"));
        assertTrue(contenido.contains("Chocolatina"));
    }

    @Test
    public void testDescuentoPorcentajeSobreCombo() {
        Producto perro = new Producto(1L, "Perro Caliente", "Pan y salchicha", new BigDecimal("12000.00"), true, CategoriaProducto.COMIDA);
        Producto jugo = new Producto(2L, "Jugo", "Jugo natural", new BigDecimal("8000.00"), true, CategoriaProducto.BEBIDA);

        Combo combo = new Combo(1L, List.of(perro, jugo), "Combo Almuerzo");
        assertEquals(new BigDecimal("20000.00"), combo.getPrecio());

        DescuentoPorcentaje conDescuento = new DescuentoPorcentaje(combo, new BigDecimal("15.00"));
        assertEquals(new BigDecimal("17000.00"), conDescuento.getPrecio());
    }

    @Test
    public void testDescuentoValorFijoSobreComboAnidado() {
        Producto nachos = new Producto(1L, "Nachos", "Nachos con queso", new BigDecimal("10000.00"), true, CategoriaProducto.COMIDA);
        Producto agua = new Producto(2L, "Agua", "Agua mineral", new BigDecimal("4000.00"), true, CategoriaProducto.BEBIDA);
        Combo comboSimple = new Combo(1L, List.of(nachos, agua), "Combo Snack");

        Producto dulce = new Producto(3L, "Gomitas", "Dulce", new BigDecimal("3000.00"), true, CategoriaProducto.DULCE);
        Combo comboAnidado = new Combo(2L, List.of(comboSimple, dulce), "Super Combo");
        assertEquals(new BigDecimal("17000.00"), comboAnidado.getPrecio());

        DescuentoValorFijo conDescuentoFijo = new DescuentoValorFijo(comboAnidado, new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("14000.00"), conDescuentoFijo.getPrecio());
    }
}
