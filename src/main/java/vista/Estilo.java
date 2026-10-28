package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Colores, fuentes y pequeñas utilidades usadas en toda la interfaz.
 * Tenerlos en un solo lugar permite cambiar el aspecto del juego editando
 * una sola clase.
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
    /** Gris para elementos bloqueados. */
    public static final Color BLOQUEADO = new Color(200, 205, 212);
    /** Verde para respuestas correctas y valores buenos. */
    public static final Color CORRECTO = new Color(46, 160, 67);
    /** Ámbar para valores regulares. */
    public static final Color ADVERTENCIA = new Color(230, 160, 30);
    /** Rojo para respuestas incorrectas y valores críticos. */
    public static final Color INCORRECTO = new Color(207, 34, 46);

    /** Fuente pequeña. */
    public static final Font FUENTE_PEQUENA = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    /** Fuente normal. */
    public static final Font FUENTE = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    /** Fuente en negrita. */
    public static final Font FUENTE_NEGRITA = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    /** Fuente para títulos. */
    public static final Font FUENTE_TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    /** Ancho del texto de las filas de los diálogos (Swing lo escala un poco). */
    private static final int ANCHO_TEXTO_FILA = 380;

    /** No se crean objetos de esta clase: todo es estático. */
    private Estilo() {
    }

    /**
     * Cambia los caracteres especiales de HTML para que un texto (por
     * ejemplo, el nombre que escribió el usuario) se muestre tal cual.
     *
     * @param texto texto original
     * @return texto seguro para poner dentro de HTML
     */
    public static String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\n", "<br>");
    }

    /**
     * Convierte un texto en HTML centrado con ancho fijo, para que las
     * etiquetas ({@code JLabel}) hagan salto de línea en vez de cortarse.
     *
     * @param texto texto a mostrar (texto plano)
     * @param ancho ancho máximo en píxeles
     * @return el texto envuelto en HTML
     */
    public static String textoAjustado(String texto, int ancho) {
        return "<html><div style='text-align:center;width:" + ancho + "px'>" + escaparHtml(texto)
                + "</div></html>";
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

    /**
     * Crea un botón con el estilo institucional y una acción.
     *
     * @param texto  texto del botón
     * @param accion lo que hace al pulsarlo
     * @return el botón
     */
    public static JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        aplicarEstiloBoton(boton);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    /**
     * Crea una fila para las listas de los diálogos (tienda, inventario,
     * mundos...): texto a la izquierda y componentes a la derecha.
     *
     * @param html    contenido de la izquierda en HTML (sin la etiqueta html)
     * @param derecha componentes de la derecha (precio, botones...)
     * @return la fila
     */
    public static JPanel crearFila(String html, JComponent... derecha) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setBackground(Color.WHITE);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL_CLARO, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        JLabel texto = new JLabel("<html><div style='width:" + ANCHO_TEXTO_FILA + "px'>" + html + "</div></html>");
        texto.setFont(FUENTE);
        fila.add(texto, BorderLayout.CENTER);
        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelDerecha.setOpaque(false);
        for (JComponent componente : derecha) {
            panelDerecha.add(componente);
        }
        fila.add(panelDerecha, BorderLayout.EAST);
        return fila;
    }

    /**
     * Color para un atributo según qué tan bien está (100 = muy bien).
     *
     * @param valorBueno valor del atributo donde 100 es lo mejor
     * @return verde, ámbar o rojo
     */
    public static Color colorAtributo(int valorBueno) {
        if (valorBueno >= 60) {
            return CORRECTO;
        } else if (valorBueno >= 30) {
            return ADVERTENCIA;
        } else {
            return INCORRECTO;
        }
    }

    /**
     * Convierte el nombre de un enum en un texto para mostrar
     * (por ejemplo, {@code HAMBRIENTO} → "Hambriento").
     *
     * @param nombreEnum nombre en mayúsculas
     * @return el mismo nombre con solo la primera letra en mayúscula
     */
    public static String nombreBonito(String nombreEnum) {
        String minusculas = nombreEnum.toLowerCase();
        return Character.toUpperCase(minusculas.charAt(0)) + minusculas.substring(1);
    }
}
