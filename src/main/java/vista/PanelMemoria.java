package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import minijuegos.Carta;
import minijuegos.Minijuego;
import minijuegos.MinijuegoMemoria;
import minijuegos.ResultadoJugada;

/**
 * Panel del juego de memoria: una cuadrícula de 4x4 botones que refleja el
 * tablero {@code Carta[][]} de {@link MinijuegoMemoria}.
 * <p>
 * El panel no tiene lógica de juego: le avisa al modelo qué carta se tocó y
 * vuelve a dibujar todo según el estado de las cartas.
 */
public class PanelMemoria extends JPanel {

    /** Milisegundos que se dejan ver dos cartas que no son pareja. */
    private static final int MILIS_MOSTRAR = 900;
    /** Ancho de los textos largos (Swing lo escala, así que ocupa un poco más en pantalla). */
    private static final int ANCHO_TEXTO = 420;

    private final MinijuegoMemoria juego;
    private final Consumer<Minijuego> alTerminar;
    private final JButton[][] botones;
    private final JLabel etiquetaEstado;
    private final Timer temporizadorOcultar;
    private boolean premioAvisado;

    /**
     * Crea el panel.
     *
     * @param juego      lógica del juego de memoria
     * @param alTerminar acción que se ejecuta una vez cuando se encuentran
     *                   todas las parejas (por ejemplo, entregar el premio)
     */
    public PanelMemoria(MinijuegoMemoria juego, Consumer<Minijuego> alTerminar) {
        this.juego = juego;
        this.alTerminar = alTerminar;
        this.botones = new JButton[MinijuegoMemoria.FILAS][MinijuegoMemoria.COLUMNAS];

        setLayout(new BorderLayout(10, 10));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(juego.getNombre(), SwingConstants.CENTER);
        titulo.setFont(Estilo.FUENTE_TITULO);
        titulo.setForeground(Estilo.AZUL_OSCURO);
        JLabel instrucciones = new JLabel(Estilo.textoAjustado(juego.getInstrucciones(), ANCHO_TEXTO),
                SwingConstants.CENTER);
        instrucciones.setFont(Estilo.FUENTE);
        JPanel encabezado = new JPanel(new BorderLayout(5, 5));
        encabezado.setOpaque(false);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(instrucciones, BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        JPanel cuadricula = new JPanel(new GridLayout(MinijuegoMemoria.FILAS, MinijuegoMemoria.COLUMNAS, 8, 8));
        cuadricula.setOpaque(false);
        for (int f = 0; f < MinijuegoMemoria.FILAS; f++) {
            for (int c = 0; c < MinijuegoMemoria.COLUMNAS; c++) {
                JButton boton = new JButton();
                boton.setFont(Estilo.FUENTE_NEGRITA);
                boton.setFocusPainted(false);
                boton.setOpaque(true);
                final int fila = f;
                final int columna = c;
                boton.addActionListener(e -> cartaTocada(fila, columna));
                botones[f][c] = boton;
                cuadricula.add(boton);
            }
        }
        add(cuadricula, BorderLayout.CENTER);

        etiquetaEstado = new JLabel(" ", SwingConstants.CENTER);
        etiquetaEstado.setFont(Estilo.FUENTE_NEGRITA);
        etiquetaEstado.setForeground(Estilo.AZUL_OSCURO);
        add(etiquetaEstado, BorderLayout.SOUTH);

        temporizadorOcultar = new Timer(MILIS_MOSTRAR, e -> {
            juego.ocultarNoEmparejadas();
            refrescar();
        });
        temporizadorOcultar.setRepeats(false);

        refrescar();
    }

    /**
     * Responde al clic en una carta. Mientras se están mostrando dos cartas
     * que no son pareja, los clics se ignoran.
     *
     * @param fila    fila de la carta
     * @param columna columna de la carta
     */
    private void cartaTocada(int fila, int columna) {
        if (temporizadorOcultar.isRunning()) {
            return;
        }
        ResultadoJugada resultado = juego.seleccionar(fila, columna);
        if (resultado == ResultadoJugada.NO_PAREJA) {
            temporizadorOcultar.start();
        }
        refrescar();
        if (juego.estaTerminado() && !premioAvisado) {
            premioAvisado = true;
            alTerminar.accept(juego);
        }
    }

    /**
     * Vuelve a pintar todos los botones según el estado de su carta y
     * actualiza el texto de intentos o el resumen final.
     */
    private void refrescar() {
        for (int f = 0; f < MinijuegoMemoria.FILAS; f++) {
            for (int c = 0; c < MinijuegoMemoria.COLUMNAS; c++) {
                Carta carta = juego.getCarta(f, c);
                JButton boton = botones[f][c];
                if (carta.isEmparejada()) {
                    boton.setText(carta.getIcono());
                    boton.setBackground(Estilo.TEAL_CLARO);
                    boton.setForeground(Estilo.AZUL_OSCURO);
                } else if (carta.isVisible()) {
                    boton.setText(carta.getIcono());
                    boton.setBackground(Estilo.TEAL);
                    boton.setForeground(Color.WHITE);
                } else {
                    boton.setText("?");
                    boton.setBackground(Estilo.AZUL_OSCURO);
                    boton.setForeground(Color.WHITE);
                }
            }
        }
        if (juego.estaTerminado()) {
            etiquetaEstado.setText(Estilo.textoAjustado(juego.getResumen(), ANCHO_TEXTO));
        } else {
            etiquetaEstado.setText("Intentos: " + juego.getIntentos()
                    + "   Parejas: " + juego.getParejasEncontradas() + "/" + MinijuegoMemoria.ICONOS.length);
        }
    }

    /**
     * Devuelve el botón de una casilla (para las pruebas).
     *
     * @param fila    fila
     * @param columna columna
     * @return el botón
     */
    JButton getBoton(int fila, int columna) {
        return botones[fila][columna];
    }

    /**
     * @return texto inferior con intentos o resumen (para las pruebas)
     */
    String getTextoEstado() {
        return etiquetaEstado.getText();
    }
}
