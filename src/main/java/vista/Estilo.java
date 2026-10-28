package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Colores y fuentes institucionales usados en toda la interfaz. Tenerlos en
 * un solo lugar permite cambiar el aspecto del juego editando una clase.
 */
public final class Estilo {

    /** Azul oscuro institucional (fondos y barras). */
    public static final Color AZUL_OSCURO = new Color(11, 37, 69);
    /** Teal institucional (botones y acentos). */
    public static final Color TEAL = new Color(18, 163, 154);
    /** Teal claro (cartas emparejadas, resaltados). */
    public static final Color TEAL_CLARO = new Color(168, 230, 224);
    /** Fondo claro de los paneles. */
    public static final Color FONDO = new Color(244, 248, 251);
    /** Verde para respuestas correctas. */
    public static final Color CORRECTO = new Color(46, 160, 67);
    /** Rojo para respuestas incorrectas. */
    public static final Color INCORRECTO = new Color(207, 34, 46);

    /** Fuente normal. */
    public static final Font FUENTE = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    /** Fuente en negrita. */
    public static final Font FUENTE_NEGRITA = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    /** Fuente para títulos. */
    public static final Font FUENTE_TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    /** No se crean objetos de esta clase: todo es estático. */
    private Estilo() {
    }

    /**
     * Convierte un texto en HTML centrado con ancho fijo, para que las
     * etiquetas ({@code JLabel}) hagan salto de línea en vez de cortarse.
     *
     * @param texto texto a mostrar
     * @param ancho ancho máximo en píxeles
     * @return el texto envuelto en HTML
     */
    public static String textoAjustado(String texto, int ancho) {
        return "<html><div style='text-align:center;width:" + ancho + "px'>" + texto + "</div></html>";
    }

    /**
     * Da a un botón el estilo institucional: fondo teal y texto blanco.
     *
     * @param boton botón a modificar
     */
    public static void aplicarEstiloBoton(JButton boton) {
        boton.setFont(FUENTE_NEGRITA);
        boton.setBackground(TEAL);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
