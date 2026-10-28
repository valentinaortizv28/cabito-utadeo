package vista;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import modelo.Historial;
import modelo.Partida;

/**
 * Logros e historial de la partida, en dos pestañas. Los datos vienen de
 * las {@code LinkedList} de {@link Historial}.
 */
public class DialogoLogros extends JDialog {

    /** Cuántas acciones recientes se muestran en el historial. */
    private static final int ACCIONES_A_MOSTRAR = 50;

    /**
     * Crea el diálogo.
     *
     * @param padre   ventana sobre la que se abre
     * @param partida partida en curso
     */
    public DialogoLogros(Window padre, Partida partida) {
        super(padre, "Logros", ModalityType.APPLICATION_MODAL);
        Historial historial = partida.getHistorial();

        List<String> logros = new ArrayList<>(historial.getLogros());
        if (logros.isEmpty()) {
            logros.add("Todavía no tienes logros. ¡Sube de nivel para conseguirlos!");
        }
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(Estilo.FUENTE);
        pestanas.addTab("Logros (" + historial.getLogros().size() + ")", crearLista(logros));
        pestanas.addTab("Historial reciente", crearLista(historial.getUltimasAcciones(ACCIONES_A_MOSTRAR)));

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        contenido.add(pestanas, BorderLayout.CENTER);
        contenido.add(Estilo.crearBoton("Cerrar", this::dispose), BorderLayout.SOUTH);
        setContentPane(contenido);
        setSize(620, 520);
        setLocationRelativeTo(padre);
    }

    /**
     * Crea una lista con barra de desplazamiento.
     *
     * @param elementos textos a mostrar
     * @return la lista dentro de un panel desplazable
     */
    private JScrollPane crearLista(List<String> elementos) {
        JList<String> lista = new JList<>(elementos.toArray(new String[0]));
        lista.setFont(Estilo.FUENTE);
        lista.setFixedCellHeight(30);
        return new JScrollPane(lista);
    }
}
