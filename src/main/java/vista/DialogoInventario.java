package vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controlador.JuegoController;
import modelo.items.Accesorio;
import modelo.items.Comida;
import modelo.items.Inventario;
import modelo.items.Item;
import modelo.items.Juguete;

/**
 * Inventario: una cuadrícula de casillas ({@link CasillaItem}), una por cada
 * objeto que tiene Cabito, con su ícono, su nombre, la cantidad y un botón
 * para comer, usar o equipar según el tipo. Los objetos que no se tienen no
 * aparecen.
 */
public class DialogoInventario extends DialogoLista {

    /** Casillas por fila. */
    public static final int COLUMNAS = 4;

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
        setSize(720, 700);
        setLocationRelativeTo(padre);
        refrescar();
    }

    /**
     * Crea una casilla por cada objeto del inventario, o un mensaje si está vacío.
     *
     * @param filas panel donde se agregan las casillas
     */
    @Override
    protected void llenarFilas(JPanel filas) {
        Inventario inventario = controlador.getPartida().getCabito().getInventario();
        List<Item> items = inventario.getItems();
        setEncabezado("Inventario  —  " + inventario.getMonedas() + " monedas");
        if (items.isEmpty()) {
            filas.setLayout(new BorderLayout());
            JLabel vacio = new JLabel("Tu inventario está vacío. Visita la Tienda Tadeísta.",
                    SwingConstants.CENTER);
            vacio.setFont(Estilo.FUENTE);
            filas.add(vacio, BorderLayout.CENTER);
            return;
        }
        filas.setLayout(new GridLayout(0, COLUMNAS, 12, 12));
        for (Item item : items) {
            filas.add(crearCasilla(item, inventario));
        }
    }

    /**
     * Crea la casilla de un objeto con el botón que le corresponde.
     *
     * @param item       objeto
     * @param inventario inventario de Cabito
     * @return la casilla
     */
    private CasillaItem crearCasilla(Item item, Inventario inventario) {
        String nombre = item.getNombre();
        boolean equipado = inventario.estaEquipado(nombre);
        String textoBoton;
        boolean habilitado = true;
        if (item instanceof Comida) {
            textoBoton = "Comer";
        } else if (item instanceof Accesorio) {
            habilitado = !equipado;
            textoBoton = equipado ? "Equipado" : "Equipar";
        } else if (item instanceof Juguete) {
            habilitado = false;
            textoBoton = "Automático";
        } else {
            textoBoton = "Usar";
        }
        return new CasillaItem(item, inventario.getCantidad(nombre), equipado, textoBoton, habilitado,
                () -> usar(nombre));
    }

    /**
     * Usa un objeto, muestra lo que dijo Cabito y actualiza la cuadrícula.
     *
     * @param nombre nombre del objeto
     */
    private void usar(String nombre) {
        String respuesta = controlador.usarItem(nombre);
        setMensaje(respuesta, !respuesta.startsWith("Ahora no"));
        refrescar();
    }
}
