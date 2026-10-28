package vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * Base de los diálogos que muestran una lista de filas (tienda, inventario,
 * mundos, minijuegos): un encabezado, la lista con barra de desplazamiento,
 * un mensaje abajo y un botón Cerrar.
 * <p>
 * Cada subclase solo implementa {@link #llenarFilas(JPanel)}; llamando a
 * {@link #refrescar()} la lista se vuelve a construir con los datos nuevos.
 */
public abstract class DialogoLista extends JDialog {

    private final JLabel encabezado;
    private final JPanel filas;
    private final JLabel mensaje;

    /**
     * Crea el diálogo modal (bloquea la ventana principal mientras está abierto).
     *
     * @param padre  ventana sobre la que se abre
     * @param titulo título del diálogo
     */
    protected DialogoLista(Window padre, String titulo) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        encabezado = new JLabel(" ");
        encabezado.setFont(Estilo.FUENTE_TITULO);
        encabezado.setForeground(Estilo.AZUL_OSCURO);
        contenido.add(encabezado, BorderLayout.NORTH);

        filas = new JPanel(new GridLayout(0, 1, 0, 8));
        filas.setOpaque(false);
        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(filas, BorderLayout.NORTH);
        JScrollPane desplazamiento = new JScrollPane(arriba);
        desplazamiento.setBorder(null);
        desplazamiento.getViewport().setBackground(Estilo.FONDO);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        contenido.add(desplazamiento, BorderLayout.CENTER);

        mensaje = new JLabel(" ", SwingConstants.LEFT);
        mensaje.setFont(Estilo.FUENTE_NEGRITA);
        JButton cerrar = Estilo.crearBoton("Cerrar", this::dispose);
        JPanel pie = new JPanel(new BorderLayout(10, 0));
        pie.setOpaque(false);
        pie.add(mensaje, BorderLayout.CENTER);
        pie.add(cerrar, BorderLayout.EAST);
        contenido.add(pie, BorderLayout.SOUTH);

        setContentPane(contenido);
        setSize(720, 580);
        setLocationRelativeTo(padre);
    }

    /**
     * Agrega a {@code filas} una fila por cada elemento a mostrar.
     *
     * @param filas panel donde se agregan las filas (ya está vacío)
     */
    protected abstract void llenarFilas(JPanel filas);

    /**
     * Vuelve a construir la lista con los datos actuales.
     */
    protected final void refrescar() {
        filas.removeAll();
        llenarFilas(filas);
        filas.revalidate();
        filas.repaint();
    }

    /**
     * Cambia el texto del encabezado.
     *
     * @param texto texto del encabezado
     */
    protected void setEncabezado(String texto) {
        encabezado.setText(texto);
    }

    /**
     * Muestra un mensaje abajo, en verde o en rojo.
     *
     * @param texto mensaje
     * @param bueno {@code true} para verde, {@code false} para rojo
     */
    protected void setMensaje(String texto, boolean bueno) {
        mensaje.setText(texto);
        mensaje.setForeground(bueno ? Estilo.CORRECTO : Estilo.INCORRECTO);
    }

    /**
     * @return texto del mensaje inferior (para pruebas)
     */
    String getMensaje() {
        return mensaje.getText();
    }

    /**
     * @return cantidad de filas mostradas (para pruebas)
     */
    int getCantidadFilas() {
        return filas.getComponentCount();
    }

    /**
     * Crea un botón para una fila, deshabilitado y gris si no se puede usar.
     *
     * @param texto      texto del botón
     * @param habilitado si se puede pulsar
     * @param accion     lo que hace al pulsarlo
     * @return el botón
     */
    protected static JButton botonFila(String texto, boolean habilitado, Runnable accion) {
        JButton boton = Estilo.crearBoton(texto, accion);
        boton.setEnabled(habilitado);
        if (!habilitado) {
            boton.setBackground(Estilo.BLOQUEADO);
        }
        return boton;
    }
}
