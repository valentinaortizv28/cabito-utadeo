package vista;

import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JPanel;

import controlador.JuegoController;
import modelo.Cabito;
import modelo.Partida;
import modelo.items.Inventario;
import modelo.items.Item;
import modelo.items.ResultadoCompra;

/**
 * Tienda Tadeísta: lista todos los objetos con su precio y un botón para
 * comprar. Los bloqueados por nivel y los permanentes ya comprados se
 * muestran pero no se pueden comprar.
 */
public class DialogoTienda extends DialogoLista {

    private final JuegoController controlador;

    /**
     * Crea la tienda.
     *
     * @param padre       ventana sobre la que se abre
     * @param controlador controlador que hace las compras
     */
    public DialogoTienda(Window padre, JuegoController controlador) {
        super(padre, "Tienda Tadeísta");
        this.controlador = controlador;
        refrescar();
    }

    /**
     * Crea una fila por cada objeto de la tienda.
     *
     * @param filas panel donde se agregan las filas
     */
    @Override
    protected void llenarFilas(JPanel filas) {
        Partida partida = controlador.getPartida();
        Cabito cabito = partida.getCabito();
        Inventario inventario = cabito.getInventario();
        setEncabezado("Tienda Tadeísta  —  Tienes " + inventario.getMonedas() + " monedas");

        for (Item item : partida.getTienda().getItems()) {
            boolean desbloqueado = partida.getTienda().estaDesbloqueado(item, cabito.getNivel());
            boolean yaLoTiene = !item.esConsumible() && inventario.tiene(item.getNombre());
            String textoBoton;
            if (!desbloqueado) {
                textoBoton = "Nivel " + item.getNivelRequerido();
            } else if (yaLoTiene) {
                textoBoton = "Comprado";
            } else {
                textoBoton = "Comprar";
            }
            JLabel precio = new JLabel(item.getPrecio() + " monedas");
            precio.setFont(Estilo.FUENTE_NEGRITA);
            precio.setForeground(Estilo.AZUL_OSCURO);
            String html = "<b>" + Estilo.escaparHtml(item.getNombre()) + "</b>  <i>(" + item.getTipo() + ")</i><br>"
                    + Estilo.escaparHtml(item.getDescripcion());
            filas.add(Estilo.crearFila(html, precio,
                    botonFila(textoBoton, desbloqueado && !yaLoTiene, () -> comprar(item))));
        }
    }

    /**
     * Compra un objeto, muestra el resultado y actualiza la lista.
     *
     * @param item objeto a comprar
     */
    private void comprar(Item item) {
        ResultadoCompra resultado = controlador.comprar(item.getNombre());
        boolean exito = resultado == ResultadoCompra.EXITO;
        setMensaje(exito ? "Compraste: " + item.getNombre() : resultado.getMensaje(), exito);
        refrescar();
    }
}
