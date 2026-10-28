package modelo.mundos;

import java.util.Random;

import modelo.Cabito;

/**
 * Módulo 20: talleres y laboratorios, se desbloquea en el nivel 4.
 * Actividad "Taller/Laboratorio".
 */
public class Modulo20 extends Mundo {

    /** Nombre del mundo. */
    public static final String NOMBRE = "Módulo 20";

    private static final int INTELIGENCIA = 5;
    private static final int FELICIDAD = 5;
    private static final int XP = 30;

    /**
     * Crea el Módulo 20 (nivel 4).
     */
    public Modulo20() {
        super(NOMBRE, "Talleres y laboratorios", "Taller/Laboratorio", 4, "fondo_modulo20.png");
    }

    /**
     * Hace un taller: inteligencia +5, felicidad +5 y 30 XP.
     *
     * @param cabito la mascota que hace el taller
     * @param random no se usa en esta actividad
     * @return resultado de la actividad
     */
    @Override
    protected ResultadoActividad hacerActividad(Cabito cabito, Random random) {
        cabito.setInteligencia(cabito.getInteligencia() + INTELIGENCIA);
        cabito.setFelicidad(cabito.getFelicidad() + FELICIDAD);
        return ResultadoActividad.exito("¡Terminaste un taller en el Módulo 20!", 0, XP);
    }
}
