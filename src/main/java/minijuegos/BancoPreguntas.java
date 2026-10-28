package minijuegos;

import java.util.List;

/**
 * Banco fijo de 15 preguntas sobre la UTadeo y sobre la vida de Cabito en
 * el campus. Para cambiar o agregar preguntas basta con editar esta lista.
 */
public final class BancoPreguntas {

    private static final List<Pregunta> PREGUNTAS = List.of(
            new Pregunta("¿En qué año fue fundada la Universidad Jorge Tadeo Lozano?",
                    List.of("1936", "1954", "1965", "1972"), 1),
            new Pregunta("¿En qué ciudad está la sede principal de la UTadeo?",
                    List.of("Medellín", "Cali", "Bogotá", "Barranquilla"), 2),
            new Pregunta("¿Qué significa la sigla UTadeo?",
                    List.of("Universidad Jorge Tadeo Lozano", "Universidad Tecnológica de Oriente",
                            "Unidad Tadeísta de Educación", "Universidad Taller de Diseño"), 0),
            new Pregunta("¿Quién fue Jorge Tadeo Lozano, el personaje que da nombre a la universidad?",
                    List.of("Un pintor del siglo XX", "Un científico y prócer de la Independencia",
                            "Un futbolista bogotano", "El primer rector de la universidad"), 1),
            new Pregunta("Jorge Tadeo Lozano participó en una famosa expedición científica. ¿Cuál?",
                    List.of("La Comisión Corográfica", "La Expedición Botánica",
                            "La Expedición Kon-Tiki", "La Misión Geodésica Francesa"), 1),
            new Pregunta("¿Quién dirigió la Real Expedición Botánica del Nuevo Reino de Granada?",
                    List.of("Francisco José de Caldas", "Antonio Nariño",
                            "José Celestino Mutis", "Alexander von Humboldt"), 2),
            new Pregunta("Jorge Tadeo Lozano fue el primer presidente de...",
                    List.of("El Estado de Cundinamarca", "La Gran Colombia",
                            "El Banco de la República", "La Real Audiencia"), 0),
            new Pregunta("¿Qué tipo de universidad es la UTadeo?",
                    List.of("Pública nacional", "Privada", "Militar", "Pública departamental"), 1),
            new Pregunta("¿En qué ciudad del Caribe tiene seccional la UTadeo?",
                    List.of("Santa Marta", "Riohacha", "Montería", "Cartagena"), 3),
            new Pregunta("¿Cómo se llama la mascota de la UTadeo?",
                    List.of("Tadeíto", "Cabito", "Lozanito", "Brujito"), 1),
            new Pregunta("¿En qué lugar del campus estudia Cabito para ganar inteligencia?",
                    List.of("La Brújula", "El Módulo 20", "La Biblioteca", "La cafetería"), 2),
            new Pregunta("¿Qué objeto de la Tienda Tadeísta ayuda a Cabito cuando está enfermo?",
                    List.of("Botiquín de bienestar", "Balón Tadeísta", "Gorra UTadeo", "Cuaderno UTadeo"), 0),
            new Pregunta("¿Qué bebida de la cafetería le da a Cabito más energía?",
                    List.of("Botella de agua", "Jugo de mora", "Tinto", "Gaseosa"), 2),
            new Pregunta("¿Qué accesorio le da a Cabito más monedas en sus actividades?",
                    List.of("Termo Tadeísta", "Mochila UTadeo", "Saco UTadeo", "Gorra UTadeo"), 1),
            new Pregunta("¿Cuántas parejas hay que encontrar en el juego de memoria del Módulo 20?",
                    List.of("4", "6", "8", "16"), 2));

    /** No se crean objetos de esta clase: todo es estático. */
    private BancoPreguntas() {
    }

    /**
     * @return las 15 preguntas (lista de solo lectura)
     */
    public static List<Pregunta> getPreguntas() {
        return PREGUNTAS;
    }
}
