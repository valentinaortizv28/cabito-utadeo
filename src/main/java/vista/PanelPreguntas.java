package vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import minijuegos.Minijuego;
import minijuegos.MinijuegoPreguntas;
import minijuegos.Pregunta;

/**
 * Panel del minijuego de preguntas de la Biblioteca: muestra una pregunta
 * con cuatro botones de opción, indica si la respuesta fue correcta y
 * permite pasar a la siguiente.
 */
public class PanelPreguntas extends JPanel {

    private static final int OPCIONES = 4;
    /** Ancho de los textos largos (Swing lo escala, así que ocupa un poco más en pantalla). */
    private static final int ANCHO_TEXTO = 420;

    private final MinijuegoPreguntas juego;
    private final Consumer<Minijuego> alTerminar;
    private final JLabel etiquetaProgreso;
    private final JLabel etiquetaEnunciado;
    private final JButton[] botonesOpcion;
    private final JLabel etiquetaRetroalimentacion;
    private final JButton botonSiguiente;
    private boolean premioAvisado;
    private boolean esperandoSiguiente;

    /**
     * Crea el panel.
     *
     * @param juego      lógica del minijuego de preguntas
     * @param alTerminar acción que se ejecuta una vez al responder la última
     *                   pregunta y pulsar "Ver resultado"
     */
    public PanelPreguntas(MinijuegoPreguntas juego, Consumer<Minijuego> alTerminar) {
        this.juego = juego;
        this.alTerminar = alTerminar;

        setLayout(new BorderLayout(10, 10));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(juego.getNombre(), SwingConstants.CENTER);
        titulo.setFont(Estilo.FUENTE_TITULO);
        titulo.setForeground(Estilo.AZUL_OSCURO);
        etiquetaProgreso = new JLabel(" ", SwingConstants.CENTER);
        etiquetaProgreso.setFont(Estilo.FUENTE);
        etiquetaEnunciado = new JLabel(" ", SwingConstants.CENTER);
        etiquetaEnunciado.setFont(Estilo.FUENTE_NEGRITA);
        etiquetaEnunciado.setForeground(Estilo.AZUL_OSCURO);

        JPanel encabezado = new JPanel(new BorderLayout(5, 8));
        encabezado.setOpaque(false);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(etiquetaProgreso, BorderLayout.CENTER);
        encabezado.add(etiquetaEnunciado, BorderLayout.SOUTH);
        add(encabezado, BorderLayout.NORTH);

        JPanel panelOpciones = new JPanel(new GridLayout(OPCIONES, 1, 8, 8));
        panelOpciones.setOpaque(false);
        botonesOpcion = new JButton[OPCIONES];
        for (int i = 0; i < OPCIONES; i++) {
            JButton boton = new JButton();
            Estilo.aplicarEstiloBoton(boton);
            final int indice = i;
            boton.addActionListener(e -> opcionElegida(indice));
            botonesOpcion[i] = boton;
            panelOpciones.add(boton);
        }
        add(panelOpciones, BorderLayout.CENTER);

        etiquetaRetroalimentacion = new JLabel(" ", SwingConstants.CENTER);
        etiquetaRetroalimentacion.setFont(Estilo.FUENTE_NEGRITA);
        botonSiguiente = new JButton("Siguiente");
        Estilo.aplicarEstiloBoton(botonSiguiente);
        botonSiguiente.addActionListener(e -> siguiente());
        JPanel pie = new JPanel(new BorderLayout(5, 5));
        pie.setOpaque(false);
        pie.add(etiquetaRetroalimentacion, BorderLayout.CENTER);
        pie.add(botonSiguiente, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);

        mostrarPregunta();
    }

    /**
     * Muestra la pregunta actual y habilita sus opciones.
     */
    private void mostrarPregunta() {
        Pregunta pregunta = juego.getPreguntaActual();
        etiquetaProgreso.setText("Pregunta " + juego.getNumeroPregunta() + " de "
                + MinijuegoPreguntas.PREGUNTAS_POR_PARTIDA + "   •   Correctas: " + juego.getCorrectas());
        etiquetaEnunciado.setText(Estilo.textoAjustado(pregunta.getEnunciado(), ANCHO_TEXTO));
        List<String> opciones = pregunta.getOpciones();
        for (int i = 0; i < OPCIONES; i++) {
            boolean existe = i < opciones.size();
            botonesOpcion[i].setVisible(existe);
            botonesOpcion[i].setText(existe ? opciones.get(i) : "");
            botonesOpcion[i].setBackground(Estilo.TEAL);
        }
        etiquetaRetroalimentacion.setText(" ");
        botonSiguiente.setEnabled(false);
        esperandoSiguiente = false;
    }

    /**
     * Revisa la opción elegida, colorea la correcta (y la elegida si fue
     * incorrecta) y habilita el botón para continuar. Mientras no se pulse
     * "Siguiente", los demás clics en las opciones se ignoran (no se
     * deshabilitan los botones para que el texto se siga leyendo bien).
     *
     * @param indice opción elegida
     */
    private void opcionElegida(int indice) {
        Pregunta pregunta = juego.getPreguntaActual();
        if (pregunta == null || esperandoSiguiente) {
            return;
        }
        esperandoSiguiente = true;
        boolean acierto = juego.responder(indice);
        botonesOpcion[pregunta.getIndiceCorrecta()].setBackground(Estilo.CORRECTO);
        if (acierto) {
            etiquetaRetroalimentacion.setText("¡Correcto!");
            etiquetaRetroalimentacion.setForeground(Estilo.CORRECTO);
        } else {
            botonesOpcion[indice].setBackground(Estilo.INCORRECTO);
            etiquetaRetroalimentacion.setText("Incorrecto. Era: " + pregunta.getRespuestaCorrecta());
            etiquetaRetroalimentacion.setForeground(Estilo.INCORRECTO);
        }
        botonSiguiente.setText(juego.estaTerminado() ? "Ver resultado" : "Siguiente");
        botonSiguiente.setEnabled(true);
    }

    /**
     * Pasa a la siguiente pregunta o, si ya no quedan, muestra el resultado
     * y avisa que el minijuego terminó.
     */
    private void siguiente() {
        if (!juego.estaTerminado()) {
            mostrarPregunta();
            return;
        }
        etiquetaProgreso.setText("¡Terminaste!");
        etiquetaEnunciado.setText(Estilo.textoAjustado(juego.getResumen(), ANCHO_TEXTO));
        for (JButton boton : botonesOpcion) {
            boton.setVisible(false);
        }
        etiquetaRetroalimentacion.setText(" ");
        botonSiguiente.setEnabled(false);
        if (!premioAvisado) {
            premioAvisado = true;
            alTerminar.accept(juego);
        }
    }

    /**
     * @param indice posición de la opción
     * @return botón de esa opción (para las pruebas)
     */
    JButton getBotonOpcion(int indice) {
        return botonesOpcion[indice];
    }

    /**
     * @return botón "Siguiente" (para las pruebas)
     */
    JButton getBotonSiguiente() {
        return botonSiguiente;
    }

    /**
     * @return texto del enunciado o del resumen final (para las pruebas)
     */
    String getTextoEnunciado() {
        return etiquetaEnunciado.getText();
    }
}
