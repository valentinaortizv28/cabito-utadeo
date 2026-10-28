package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import controlador.JuegoController;
import modelo.Cabito;
import modelo.Partida;

/**
 * Barra superior: título, nombre y nivel de Cabito, barra de experiencia,
 * monedas y los botones Guardar y Cargar.
 */
public class BarraSuperior extends JPanel {

    private final JLabel etiquetaNivel;
    private final JProgressBar barraExperiencia;
    private final JLabel etiquetaMonedas;

    /**
     * Crea la barra.
     *
     * @param controlador controlador que recibe Guardar y Cargar
     */
    public BarraSuperior(JuegoController controlador) {
        setLayout(new BorderLayout(20, 0));
        setBackground(Estilo.AZUL_OSCURO);
        setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel titulo = new JLabel("Cabito: Aventura Tadeísta");
        titulo.setFont(Estilo.FUENTE_TITULO);
        titulo.setForeground(Color.WHITE);
        add(titulo, BorderLayout.WEST);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        centro.setOpaque(false);
        etiquetaNivel = new JLabel();
        etiquetaNivel.setFont(Estilo.FUENTE_NEGRITA);
        etiquetaNivel.setForeground(Color.WHITE);
        barraExperiencia = new JProgressBar();
        barraExperiencia.setStringPainted(true);
        barraExperiencia.setFont(Estilo.FUENTE_PEQUENA);
        barraExperiencia.setForeground(Estilo.TEAL);
        barraExperiencia.setBackground(Color.WHITE);
        barraExperiencia.setPreferredSize(new Dimension(180, 22));
        etiquetaMonedas = new JLabel();
        etiquetaMonedas.setFont(Estilo.FUENTE_NEGRITA);
        etiquetaMonedas.setForeground(new Color(255, 214, 90));
        centro.add(etiquetaNivel);
        centro.add(barraExperiencia);
        centro.add(etiquetaMonedas);
        add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(Estilo.crearBoton("Guardar", controlador::guardarPartida));
        botones.add(Estilo.crearBoton("Cargar", controlador::cargarPartida));
        add(botones, BorderLayout.EAST);
    }

    /**
     * Actualiza nivel, experiencia y monedas.
     *
     * @param partida partida en curso
     */
    public void actualizar(Partida partida) {
        Cabito cabito = partida.getCabito();
        etiquetaNivel.setText(cabito.getNombre() + "  ·  Nivel " + cabito.getNivel());
        barraExperiencia.setMaximum(cabito.getExperienciaParaSubir());
        barraExperiencia.setValue(cabito.getExperiencia());
        barraExperiencia.setString("XP " + cabito.getExperiencia() + " / " + cabito.getExperienciaParaSubir());
        etiquetaMonedas.setText("Monedas: " + cabito.getInventario().getMonedas());
    }

    /**
     * @return texto de las monedas (para pruebas)
     */
    String getTextoMonedas() {
        return etiquetaMonedas.getText();
    }
}
