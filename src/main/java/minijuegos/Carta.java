package minijuegos;

/**
 * Carta del juego de memoria. Tiene un ícono y puede estar boca abajo,
 * boca arriba o ya emparejada (queda boca arriba para siempre).
 */
public class Carta {

    private final String icono;
    private boolean visible;
    private boolean emparejada;

    /**
     * Crea una carta boca abajo.
     *
     * @param icono nombre del ícono; dos cartas son pareja si tienen el mismo ícono
     */
    public Carta(String icono) {
        this.icono = icono;
    }

    /**
     * Pone la carta boca arriba.
     */
    public void voltear() {
        visible = true;
    }

    /**
     * Pone la carta boca abajo, salvo que ya esté emparejada.
     */
    public void ocultar() {
        if (!emparejada) {
            visible = false;
        }
    }

    /**
     * Marca la carta como emparejada; queda boca arriba.
     */
    public void emparejar() {
        emparejada = true;
        visible = true;
    }

    /**
     * Indica si esta carta forma pareja con otra (mismo ícono).
     *
     * @param otra la otra carta
     * @return {@code true} si tienen el mismo ícono
     */
    public boolean esParejaDe(Carta otra) {
        return icono.equals(otra.icono);
    }

    /**
     * @return nombre del ícono
     */
    public String getIcono() {
        return icono;
    }

    /**
     * @return {@code true} si está boca arriba
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @return {@code true} si ya se encontró su pareja
     */
    public boolean isEmparejada() {
        return emparejada;
    }
}
