package modelo.mundos;

import java.util.Random;

import modelo.Cabito;
import modelo.items.Actividad;

/**
 * Biblioteca: se desbloquea en el nivel 2.
 * Actividad "Estudiar": sube la inteligencia y da bastante experiencia.
 */
public class Biblioteca extends Mundo {

    /** Nombre del mundo. */
    public static final String NOMBRE = "Biblioteca";

    private static final int INTELIGENCIA = 10;
    private static final int ENERGIA = 15;
    private static final int XP = 25;

    /**
     * Crea la Biblioteca (nivel 2).
     */
    public Biblioteca() {
        super(NOMBRE, "Silencio, libros y mucho aprendizaje", "Estudiar", 2, "fondo_biblioteca.png");
    }

    /**
     * Estudia: inteligencia +10 (+5 más con el Cuaderno UTadeo),
     * energía −15 y 25 XP.
     *
     * @param cabito la mascota que estudia
     * @param random no se usa en esta actividad
     * @return resultado de la actividad
     */
    @Override
    protected ResultadoActividad hacerActividad(Cabito cabito, Random random) {
        cabito.setInteligencia(cabito.getInteligencia() + INTELIGENCIA);
        cabito.setEnergia(cabito.getEnergia() - ENERGIA);
        cabito.getInventario().aplicarJuguetes(Actividad.ESTUDIAR, cabito);
        return ResultadoActividad.exito("Estudiaste en la Biblioteca. ¡Inteligencia arriba!", 0, XP);
    }
}
