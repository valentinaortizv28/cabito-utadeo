package vista;

import java.awt.Window;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JPanel;

import controlador.JuegoController;
import minijuegos.Minijuego;
import modelo.Partida;

/**
 * Lista de minijuegos con sus instrucciones. Si uno no se puede jugar
 * (mundo bloqueado o Cabito cansado/enfermo/dormido) se explica por qué.
 */
public class DialogoMinijuegos extends DialogoLista {

    private final JuegoController controlador;

    /**
     * Crea el diálogo.
     *
     * @param padre       ventana sobre la que se abre
     * @param controlador controlador que abre los minijuegos
     */
    public DialogoMinijuegos(Window padre, JuegoController controlador) {
        super(padre, "Minijuegos");
        this.controlador = controlador;
        setEncabezado("Minijuegos");
        refrescar();
    }

    /**
     * Crea una fila por cada minijuego.
     *
     * @param filas panel donde se agregan las filas
     */
    @Override
    protected void llenarFilas(JPanel filas) {
        Partida partida = controlador.getPartida();
        List<Supplier<Minijuego>> fabricas = List.of(partida::crearMinijuegoMemoria,
                partida::crearMinijuegoPreguntas);
        for (Supplier<Minijuego> fabrica : fabricas) {
            Minijuego muestra = fabrica.get();
            boolean puede = partida.puedeJugar(muestra);
            String html = "<b>" + muestra.getNombre() + "</b>  —  " + muestra.getNombreMundo() + "<br>"
                    + Estilo.escaparHtml(muestra.getInstrucciones());
            if (!puede) {
                html += "<br><font color='#cf222e'>" + Estilo.escaparHtml(partida.getMotivoNoPuedeJugar(muestra))
                        + "</font>";
            }
            filas.add(Estilo.crearFila(html, botonFila("Jugar", puede, () -> {
                dispose();
                controlador.abrirMinijuego(fabrica.get());
            })));
        }
    }
}
