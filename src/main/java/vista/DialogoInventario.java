package vista;

import java.awt.Window;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import controlador.JuegoController;
import modelo.items.Accesorio;
import modelo.items.Comida;
import modelo.items.Inventario;
import modelo.items.Item;
import modelo.items.Juguete;

/**
 * Inventario: muestra los objetos de Cabito con su cantidad y un botón
 * para comer, usar o equipar según el tipo.
 */
public class DialogoInventario extends DialogoLista {

    private final JuegoController controlador;

    /**
     * Crea el inventario.
     *
     * @param padre       ventana sobre la que se abre
     * @param controlador controlador que usa los objetos
     */
    public DialogoInventario(Window padre, JuegoController controlador) {
        super(padre, "Inventario");
        this.controlador = controlador;
        refrescar();
    }

    /**
     * Crea una fila por cada objeto del inventario.
     *
     * @param filas panel donde se agregan las filas
     */
    @Override
    protected void llenarFilas(JPanel filas) {
        Inventario inventario = controlador.getPartida().getCabito().getInventario();
        List<Item> items = inventario.getItems();
        setEncabezado("Inventario  —  " + inventario.getMonedas() + " monedas");
        if (items.isEmpty()) {
            JLabel vacio = new JLabel("Tu inventario está vacío. Visita la Tienda Tadeísta.");
            vacio.setFont(Estilo.FUENTE);
            filas.add(vacio);
            return;
        }
        for (Item item : items) {
            String nombre = item.getNombre();
            String html = "<b>" + Estilo.escaparHtml(nombre) + "</b>";
            if (item.esConsumible()) {
                html += "  x" + inventario.getCantidad(nombre);
            }
            html += "  <i>(" + item.getTipo() + ")</i><br>" + Estilo.escaparHtml(item.getDescripcion());

            String textoBoton;
            boolean habilitado = true;
            if (item instanceof Comida) {
                textoBoton = "Comer";
            } else if (item instanceof Accesorio) {
                habilitado = !inventario.estaEquipado(nombre);
                textoBoton = habilitado ? "Equipar" : "Equipado";
            } else if (item instanceof Juguete) {
                habilitado = false;
                textoBoton = "Automático";
            } else {
                textoBoton = "Usar";
            }
            filas.add(Estilo.crearFila(html, botonFila(textoBoton, habilitado, () -> usar(nombre))));
        }
    }

    /**
     * Usa un objeto, muestra lo que dijo Cabito y actualiza la lista.
     *
     * @param nombre nombre del objeto
     */
    private void usar(String nombre) {
        String respuesta = controlador.usarItem(nombre);
        setMensaje(respuesta, !respuesta.startsWith("Ahora no"));
        refrescar();
    }
}
