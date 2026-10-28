package vista;

import java.awt.Window;

import javax.swing.JPanel;

import controlador.JuegoController;
import modelo.Partida;
import modelo.mundos.Mundo;

/**
 * Lista de mundos con su actividad, el nivel que piden, el progreso y un
 * botón para viajar.
 */
public class DialogoMundos extends DialogoLista {

    private final JuegoController controlador;

    /**
     * Crea el diálogo.
     *
     * @param padre       ventana sobre la que se abre
     * @param controlador controlador que hace los viajes
     */
    public DialogoMundos(Window padre, JuegoController controlador) {
        super(padre, "Mundos");
        this.controlador = controlador;
        setEncabezado("Mundos de la Tadeo");
        refrescar();
    }

    /**
     * Crea una fila por cada mundo.
     *
     * @param filas panel donde se agregan las filas
     */
    @Override
    protected void llenarFilas(JPanel filas) {
        Partida partida = controlador.getPartida();
        for (Mundo mundo : partida.getMundos()) {
            boolean desbloqueado = partida.estaDesbloqueado(mundo);
            boolean actual = mundo == partida.getMundoActual();
            String html = "<b>" + mundo.getNombre() + "</b>  —  " + mundo.getDescripcion()
                    + "<br>Actividad: " + mundo.getNombreActividad()
                    + "  ·  Nivel requerido: " + mundo.getNivelRequerido()
                    + "  ·  Veces realizada: " + partida.getActividadesRealizadas(mundo.getNombre());
            String textoBoton = actual ? "Estás aquí" : (desbloqueado ? "Viajar" : mundo.getMensajeBloqueado());
            filas.add(Estilo.crearFila(html, botonFila(textoBoton, desbloqueado && !actual, () -> {
                dispose();
                controlador.viajarA(mundo.getNombre());
            })));
        }
    }
}
