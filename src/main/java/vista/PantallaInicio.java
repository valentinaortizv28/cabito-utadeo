package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controlador.JuegoController;
import modelo.Estado;

/**
 * Pantalla inicial: título, Cabito feliz y los botones "Nueva partida" y
 * "Cargar partida".
 */
public class PantallaInicio extends JFrame {

    /**
     * Crea la pantalla inicial (todavía sin mostrarla).
     *
     * @param controlador controlador que recibe la elección del usuario
     */
    public PantallaInicio(JuegoController controlador) {
        super("Cabito: Aventura Tadeísta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBackground(Estilo.AZUL_OSCURO);
        contenido.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Cabito: Aventura Tadeísta", SwingConstants.CENTER);
        titulo.setFont(Estilo.FUENTE_TITULO.deriveFont(30f));
        titulo.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("La mascota virtual de la Universidad Jorge Tadeo Lozano",
                SwingConstants.CENTER);
        subtitulo.setFont(Estilo.FUENTE);
        subtitulo.setForeground(Estilo.TEAL_CLARO);
        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 6));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        contenido.add(encabezado, BorderLayout.NORTH);

        DibujoCabito dibujo = new DibujoCabito();
        dibujo.setEstado(Estado.FELIZ);
        contenido.add(dibujo, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        botones.setOpaque(false);
        botones.add(Estilo.crearBoton("Nueva partida", controlador::nuevaPartida));
        botones.add(Estilo.crearBoton("Cargar partida", controlador::cargarPartida));
        contenido.add(botones, BorderLayout.SOUTH);

        setContentPane(contenido);
        setSize(620, 560);
        setLocationRelativeTo(null);
    }
}
