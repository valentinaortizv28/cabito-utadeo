package modelo.mundos;

import java.util.Random;

import modelo.Cabito;

/**
 * La Brújula: zona social del campus, disponible desde el inicio.
 * Actividad "Recorrer el campus": monedas aleatorias, felicidad y
 * posibilidad de un evento sorpresa.
 */
public class Brujula extends Mundo {

    /** Nombre del mundo. */
    public static final String NOMBRE = "La Brújula";

    private static final int MONEDAS_MIN = 5;
    private static final int MONEDAS_MAX = 25;
    private static final int FELICIDAD = 10;
    private static final int ENERGIA = 10;
    private static final int XP = 15;

    /** Probabilidad (en %) de que ocurra un evento aleatorio. */
    private static final int PROBABILIDAD_EVENTO = 30;
    private static final int MONEDAS_EVENTO = 5;
    private static final int FELICIDAD_EVENTO = 5;
    private static final int ENERGIA_EVENTO = 5;

    /**
     * Crea La Brújula (nivel 1).
     */
    public Brujula() {
        super(NOMBRE, "Zona social del campus", "Recorrer el campus", 1, "fondo_brujula.png");
    }

    /**
     * Recorre el campus: gana entre 5 y 25 monedas (+20% con la Mochila),
     * felicidad +10, energía −10 y 15 XP. Con 30% de probabilidad ocurre
     * además uno de tres eventos sorpresa.
     *
     * @param cabito la mascota que recorre el campus
     * @param random generador de números aleatorios
     * @return resultado de la actividad
     */
    @Override
    protected ResultadoActividad hacerActividad(Cabito cabito, Random random) {
        int base = MONEDAS_MIN + random.nextInt(MONEDAS_MAX - MONEDAS_MIN + 1);
        int monedas = cabito.getInventario().ganarMonedasPorActividad(base);
        cabito.setFelicidad(cabito.getFelicidad() + FELICIDAD);
        cabito.setEnergia(cabito.getEnergia() - ENERGIA);

        String mensaje = "Recorriste el campus y ganaste " + monedas + " monedas.";
        if (random.nextInt(100) < PROBABILIDAD_EVENTO) {
            switch (random.nextInt(3)) {
                case 0:
                    cabito.getInventario().agregarMonedas(MONEDAS_EVENTO);
                    monedas += MONEDAS_EVENTO;
                    mensaje += " ¡Encontraste una moneda! (+" + MONEDAS_EVENTO + ")";
                    break;
                case 1:
                    cabito.setFelicidad(cabito.getFelicidad() + FELICIDAD_EVENTO);
                    mensaje += " ¡Te encontraste con unos amigos! (felicidad +" + FELICIDAD_EVENTO + ")";
                    break;
                default:
                    cabito.setEnergia(cabito.getEnergia() + ENERGIA_EVENTO);
                    mensaje += " ¡Te regalaron un tinto! (energía +" + ENERGIA_EVENTO + ")";
                    break;
            }
        }
        return ResultadoActividad.exito(mensaje, monedas, XP);
    }
}
