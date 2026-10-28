package minijuegos;

import java.util.List;

/**
 * Pregunta de selección múltiple con una sola respuesta correcta.
 */
public class Pregunta {

    private final String enunciado;
    private final List<String> opciones;
    private final int indiceCorrecta;

    /**
     * Crea una pregunta.
     *
     * @param enunciado      texto de la pregunta
     * @param opciones       opciones de respuesta
     * @param indiceCorrecta posición (desde 0) de la opción correcta
     */
    public Pregunta(String enunciado, List<String> opciones, int indiceCorrecta) {
        if (indiceCorrecta < 0 || indiceCorrecta >= opciones.size()) {
            throw new IllegalArgumentException("Índice de respuesta correcta fuera de rango");
        }
        this.enunciado = enunciado;
        this.opciones = List.copyOf(opciones);
        this.indiceCorrecta = indiceCorrecta;
    }

    /**
     * @param indice opción elegida (desde 0)
     * @return {@code true} si es la respuesta correcta
     */
    public boolean esCorrecta(int indice) {
        return indice == indiceCorrecta;
    }

    /**
     * @return texto de la pregunta
     */
    public String getEnunciado() {
        return enunciado;
    }

    /**
     * @return opciones de respuesta (solo lectura)
     */
    public List<String> getOpciones() {
        return opciones;
    }

    /**
     * @return posición de la opción correcta
     */
    public int getIndiceCorrecta() {
        return indiceCorrecta;
    }

    /**
     * @return texto de la opción correcta
     */
    public String getRespuestaCorrecta() {
        return opciones.get(indiceCorrecta);
    }
}
