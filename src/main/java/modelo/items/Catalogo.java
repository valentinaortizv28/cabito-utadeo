package modelo.items;

import java.util.List;

/**
 * Lista fija de todos los objetos que existen en el juego.
 * <p>
 * Cada objeto se crea una sola vez aquí y se comparte en todo el juego.
 * El inventario solo guarda nombres, y con {@link #buscar(String)} se
 * obtiene el objeto completo. Las constantes de nombre evitan errores de
 * escritura en el resto del código.
 */
public final class Catalogo {

    /** Nombre del tinto. */
    public static final String TINTO = "Tinto de la cafetería";
    /** Nombre de la empanada. */
    public static final String EMPANADA = "Empanada";
    /** Nombre del almuerzo ejecutivo. */
    public static final String ALMUERZO = "Almuerzo ejecutivo";
    /** Nombre de la botella de agua. */
    public static final String AGUA = "Botella de agua UTadeo";
    /** Nombre del balón. */
    public static final String BALON = "Balón Tadeísta";
    /** Nombre del cuaderno. */
    public static final String CUADERNO = "Cuaderno UTadeo";
    /** Nombre del kit de aseo. */
    public static final String KIT_ASEO = "Kit de aseo";
    /** Nombre del botiquín. */
    public static final String BOTIQUIN = "Botiquín de bienestar";
    /** Nombre de la gorra. */
    public static final String GORRA = "Gorra UTadeo";
    /** Nombre del saco. */
    public static final String SACO = "Saco \"UTadeo\"";
    /** Nombre del termo. */
    public static final String TERMO = "Termo Tadeísta";
    /** Nombre de la mochila. */
    public static final String MOCHILA = "Mochila UTadeo";

    private static final List<Item> ITEMS = List.of(
            // Comida(nombre, descripción, precio, saciedad, energía, felicidad)
            new Comida(TINTO, "Energía +20, saciedad +5", 10, 5, 20, 0),
            new Comida(EMPANADA, "Saciedad +25", 15, 25, 0, 0),
            new Comida(ALMUERZO, "Saciedad +50, felicidad +5", 35, 50, 0, 5),
            new Comida(AGUA, "Energía +10", 8, 0, 10, 0),
            // Juguete(nombre, descripción, precio, actividad, felicidad extra, inteligencia extra)
            new Juguete(BALON, "Jugar da +10 de felicidad extra", 40, Actividad.JUGAR, 10, 0),
            new Juguete(CUADERNO, "Estudiar en la Biblioteca da +5 de inteligencia extra", 30,
                    Actividad.ESTUDIAR, 0, 5),
            // ItemCuidado(nombre, descripción, precio, saciedad, energía, higiene)
            new ItemCuidado(KIT_ASEO, "Higiene +40", 20, 0, 0, 40),
            new ItemCuidado(BOTIQUIN, "Saciedad, higiene y energía +20; cura la enfermedad", 45,
                    20, 20, 20),
            // Accesorio(nombre, descripción, precio, nivel, bonus, felicidad al equipar)
            new Accesorio(GORRA, "La felicidad baja la mitad de rápido", 60, 1,
                    Bonus.FELICIDAD_LENTA, 0),
            new Accesorio(SACO, "Felicidad +10 al equiparlo", 120, 3, Bonus.NINGUNO, 10),
            new Accesorio(TERMO, "La energía baja la mitad de rápido", 80, 1,
                    Bonus.ENERGIA_LENTA, 0),
            new Accesorio(MOCHILA, "+20% de monedas en las actividades", 100, 5,
                    Bonus.MONEDAS_EXTRA, 0));

    /** No se crean objetos de esta clase: todo es estático. */
    private Catalogo() {
    }

    /**
     * Devuelve todos los objetos del juego en el orden de la tienda.
     *
     * @return lista que no se puede modificar
     */
    public static List<Item> getItems() {
        return ITEMS;
    }

    /**
     * Busca un objeto por su nombre.
     *
     * @param nombre nombre exacto del objeto
     * @return el objeto, o {@code null} si no existe
     */
    public static Item buscar(String nombre) {
        for (Item item : ITEMS) {
            if (item.getNombre().equals(nombre)) {
                return item;
            }
        }
        return null;
    }
}
