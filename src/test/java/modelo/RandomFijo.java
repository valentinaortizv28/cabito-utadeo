package modelo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * {@link Random} de mentiras para las pruebas: {@code nextInt} devuelve los
 * valores que se le indican, en orden. Cuando se acaban, devuelve el mayor
 * valor posible menos uno, lo que en la Brújula significa "sin evento".
 */
public class RandomFijo extends Random {

    private final Deque<Integer> valores = new ArrayDeque<>();

    /**
     * @param valores valores que devolverá {@code nextInt}, en orden
     */
    public RandomFijo(int... valores) {
        for (int v : valores) {
            this.valores.addLast(v);
        }
    }

    @Override
    public int nextInt(int limite) {
        if (valores.isEmpty()) {
            return limite - 1;
        }
        return valores.removeFirst();
    }
}
