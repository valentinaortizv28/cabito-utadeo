package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import modelo.Cabito;
import modelo.items.Inventario;

/**
 * Panel derecho: una barra de progreso por cada atributo de Cabito, su
 * estado actual y los accesorios que tiene puestos.
 * <p>
 * El color de cada barra indica qué tan bien está el atributo: verde,
 * ámbar o rojo. La barra de "Hambre" muestra {@code 100 - saciedad}, así
 * que ahí una barra alta es mala (y se pinta roja).
 */
public class PanelAtributos extends JPanel {

    private static final int ANCHO = 230;

    private final JProgressBar barraHambre;
    private final JProgressBar barraEnergia;
    private final JProgressBar barraFelicidad;
    private final JProgressBar barraHigiene;
    private final JProgressBar barraInteligencia;
    private final JLabel etiquetaEstado;
    private final JLabel etiquetaAccesorios;

    /**
     * Crea el panel con todas las barras en 0.
     */
    public PanelAtributos() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(ANCHO, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Estilo.TEAL_CLARO),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel titulo = new JLabel("Atributos");
        titulo.setFont(Estilo.FUENTE_TITULO);
        titulo.setForeground(Estilo.AZUL_OSCURO);
        agregar(titulo);
        add(Box.createVerticalStrut(10));

        barraHambre = agregarBarra("Hambre", "Entre más alta, más hambre tiene Cabito");
        barraEnergia = agregarBarra("Energía", null);
        barraFelicidad = agregarBarra("Felicidad", null);
        barraHigiene = agregarBarra("Higiene", null);
        barraInteligencia = agregarBarra("Inteligencia", null);

        add(Box.createVerticalStrut(14));
        etiquetaEstado = new JLabel();
        etiquetaEstado.setFont(Estilo.FUENTE_NEGRITA);
        etiquetaEstado.setForeground(Estilo.AZUL_OSCURO);
        agregar(etiquetaEstado);
        add(Box.createVerticalStrut(10));
        etiquetaAccesorios = new JLabel();
        etiquetaAccesorios.setFont(Estilo.FUENTE_PEQUENA);
        agregar(etiquetaAccesorios);
        add(Box.createVerticalGlue());
    }

    /**
     * Agrega un componente alineado a la izquierda.
     */
    private void agregar(JLabel etiqueta) {
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(etiqueta);
    }

    /**
     * Agrega el nombre de un atributo y su barra.
     *
     * @param nombre      nombre del atributo
     * @param explicacion tooltip opcional
     * @return la barra creada
     */
    private JProgressBar agregarBarra(String nombre, String explicacion) {
        JLabel etiqueta = new JLabel(nombre);
        etiqueta.setFont(Estilo.FUENTE);
        etiqueta.setToolTipText(explicacion);
        agregar(etiqueta);
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setStringPainted(true);
        barra.setFont(Estilo.FUENTE_PEQUENA);
        barra.setBackground(Estilo.FONDO);
        barra.setBorderPainted(false);
        barra.setToolTipText(explicacion);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        add(barra);
        add(Box.createVerticalStrut(8));
        return barra;
    }

    /**
     * Actualiza las barras y textos con los datos de Cabito.
     *
     * @param cabito la mascota
     */
    public void actualizar(Cabito cabito) {
        poner(barraHambre, cabito.getHambre(), cabito.getSaciedad());
        poner(barraEnergia, cabito.getEnergia(), cabito.getEnergia());
        poner(barraFelicidad, cabito.getFelicidad(), cabito.getFelicidad());
        poner(barraHigiene, cabito.getHigiene(), cabito.getHigiene());
        poner(barraInteligencia, cabito.getInteligencia(), cabito.getInteligencia());

        etiquetaEstado.setText("<html>Estado: " + Estilo.nombreBonito(cabito.getEstado().name()) + "</html>");
        Inventario inventario = cabito.getInventario();
        if (inventario.getEquipados().isEmpty()) {
            etiquetaAccesorios.setText("<html>Sin accesorios puestos</html>");
        } else {
            etiquetaAccesorios.setText("<html>Lleva puesto:<br>"
                    + Estilo.escaparHtml(String.join("\n", inventario.getEquipados())) + "</html>");
        }
    }

    /**
     * Pone el valor y el color de una barra.
     *
     * @param barra      barra a actualizar
     * @param mostrado   número que se muestra
     * @param valorBueno valor donde 100 es lo mejor (decide el color)
     */
    private void poner(JProgressBar barra, int mostrado, int valorBueno) {
        barra.setValue(mostrado);
        barra.setString(String.valueOf(mostrado));
        barra.setForeground(Estilo.colorAtributo(valorBueno));
    }

    /**
     * @return la barra de hambre (para pruebas)
     */
    JProgressBar getBarraHambre() {
        return barraHambre;
    }
}
