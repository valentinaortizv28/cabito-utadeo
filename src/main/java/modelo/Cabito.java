package modelo;

import modelo.items.Accesorio;
import modelo.items.Actividad;
import modelo.items.Bonus;
import modelo.items.Catalogo;
import modelo.items.Comida;
import modelo.items.Inventario;
import modelo.items.Item;

/**
 * Cabito, la mascota virtual de la Universidad Jorge Tadeo Lozano.
 * <p>
 * Tiene cinco atributos de estado en el rango 0–100, donde 100 siempre es
 * "bien": saciedad (lo contrario del hambre), energía, felicidad, higiene e
 * inteligencia. También tiene nivel, experiencia y un {@link Inventario}
 * con sus monedas y objetos.
 * <p>
 * Esta clase no conoce el reloj del juego: el controlador llama a
 * {@link #pasarTiempo()} cada 10 segundos. Así el modelo no depende de Swing
 * y se puede probar con JUnit sin esperar tiempo real.
 */
public class Cabito {

    /** Valor mínimo de cualquier atributo. */
    public static final int MINIMO = 0;
    /** Valor máximo de cualquier atributo. */
    public static final int MAXIMO = 100;

    // Valores con los que empieza una partida nueva
    private static final int SACIEDAD_INICIAL = 80;
    private static final int ENERGIA_INICIAL = 80;
    private static final int FELICIDAD_INICIAL = 70;
    private static final int HIGIENE_INICIAL = 80;
    private static final int INTELIGENCIA_INICIAL = 60;

    // Cambios en cada paso del tiempo (cada 10 segundos)
    private static final int BAJA_SACIEDAD = 3;
    private static final int BAJA_SACIEDAD_DORMIDO = 1;
    private static final int BAJA_ENERGIA = 2;
    private static final int SUBE_ENERGIA_DORMIDO = 8;
    private static final int BAJA_HIGIENE = 2;
    private static final int BAJA_FELICIDAD = 1;
    private static final int BAJA_FELICIDAD_EXTRA = 2;
    private static final int UMBRAL_DESCUIDO = 20;

    // Efectos de las acciones básicas
    private static final int JUGAR_FELICIDAD = 15;
    private static final int JUGAR_ENERGIA = 10;
    private static final int JUGAR_HIGIENE = 5;
    private static final int JUGAR_XP = 10;
    private static final int BANAR_FELICIDAD = 5;

    /** Experiencia necesaria por cada nivel: subir del nivel n pide n * 100 XP. */
    private static final int XP_POR_NIVEL = 100;

    private String nombre;
    private int saciedad;
    private int energia;
    private int felicidad;
    private int higiene;
    private int inteligencia;
    private int nivel;
    private int experiencia;
    private boolean dormido;
    private Estado estado;
    private int contadorTics;
    private final Inventario inventario;

    /**
     * Crea un Cabito nuevo con los valores iniciales de una partida.
     *
     * @param nombre nombre que el jugador le da a Cabito
     */
    public Cabito(String nombre) {
        this.nombre = nombre;
        this.saciedad = SACIEDAD_INICIAL;
        this.energia = ENERGIA_INICIAL;
        this.felicidad = FELICIDAD_INICIAL;
        this.higiene = HIGIENE_INICIAL;
        this.inteligencia = INTELIGENCIA_INICIAL;
        this.nivel = 1;
        this.experiencia = 0;
        this.dormido = false;
        this.contadorTics = 0;
        this.inventario = new Inventario();
        actualizarEstado();
    }

    // ------------------------------------------------------------------
    // Paso del tiempo y estado
    // ------------------------------------------------------------------

    /**
     * Aplica un "tic" del reloj del juego (equivale a 10 segundos).
     * <ul>
     *   <li>Despierto: saciedad −3 y energía −2.</li>
     *   <li>Dormido: saciedad −1 y energía +8.</li>
     *   <li>Siempre: higiene −2 y felicidad −1.</li>
     *   <li>Si después de esto la saciedad o la higiene quedan por debajo
     *       de 20, la felicidad baja 2 más.</li>
     *   <li>Con la Gorra UTadeo equipada la felicidad baja la mitad, y con el
     *       Termo Tadeísta la energía baja la mitad.</li>
     * </ul>
     * Al final recalcula el estado.
     */
    public void pasarTiempo() {
        contadorTics++;
        if (dormido) {
            setSaciedad(saciedad - BAJA_SACIEDAD_DORMIDO);
            setEnergia(energia + SUBE_ENERGIA_DORMIDO);
        } else {
            setSaciedad(saciedad - BAJA_SACIEDAD);
            setEnergia(energia - reducirConBonus(BAJA_ENERGIA, Bonus.ENERGIA_LENTA));
        }
        setHigiene(higiene - BAJA_HIGIENE);

        int bajaFelicidad = BAJA_FELICIDAD;
        if (saciedad < UMBRAL_DESCUIDO || higiene < UMBRAL_DESCUIDO) {
            bajaFelicidad += BAJA_FELICIDAD_EXTRA;
        }
        setFelicidad(felicidad - reducirConBonus(bajaFelicidad, Bonus.FELICIDAD_LENTA));
        actualizarEstado();
    }

    /**
     * Reduce a la mitad una bajada de atributo si el bonus está activo.
     * <p>
     * Como los atributos son enteros, la mitad de un número impar no es
     * exacta: se redondea hacia abajo en los tics pares y hacia arriba en los
     * impares. Así, en promedio, baja exactamente la mitad (una bajada de 1
     * se aplica un tic sí y otro no).
     *
     * @param bajada cuánto bajaría el atributo sin bonus
     * @param bonus  bonus que reduce esta bajada
     * @return la bajada que se aplica en este tic
     */
    private int reducirConBonus(int bajada, Bonus bonus) {
        if (!inventario.tieneBonus(bonus)) {
            return bajada;
        }
        int mitad = bajada / 2;
        if (bajada % 2 != 0 && contadorTics % 2 == 1) {
            mitad++;
        }
        return mitad;
    }

    /**
     * Calcula el estado de Cabito a partir de sus atributos y lo guarda.
     * <p>
     * Las condiciones se revisan en orden de prioridad; gana la primera que
     * se cumpla:
     * <ol>
     *   <li>DORMIDO: se le mandó a dormir y la energía es menor que 100.</li>
     *   <li>ENFERMO: saciedad &lt; 15 o higiene &lt; 15.</li>
     *   <li>HAMBRIENTO: saciedad &lt; 30.</li>
     *   <li>CANSADO: energía &lt; 25.</li>
     *   <li>SUCIO: higiene &lt; 25.</li>
     *   <li>TRISTE: felicidad &lt; 30.</li>
     *   <li>FELIZ: felicidad &gt; 75 y los demás atributos &gt; 50.</li>
     *   <li>NORMAL: en cualquier otro caso.</li>
     * </ol>
     * Si estaba dormido y la energía ya llegó a 100, se despierta solo.
     *
     * @return el estado calculado
     */
    public Estado actualizarEstado() {
        if (dormido && energia >= MAXIMO) {
            dormido = false;
        }

        if (dormido) {
            estado = Estado.DORMIDO;
        } else if (saciedad < 15 || higiene < 15) {
            estado = Estado.ENFERMO;
        } else if (saciedad < 30) {
            estado = Estado.HAMBRIENTO;
        } else if (energia < 25) {
            estado = Estado.CANSADO;
        } else if (higiene < 25) {
            estado = Estado.SUCIO;
        } else if (felicidad < 30) {
            estado = Estado.TRISTE;
        } else if (felicidad > 75 && saciedad > 50 && energia > 50
                && higiene > 50 && inteligencia > 50) {
            estado = Estado.FELIZ;
        } else {
            estado = Estado.NORMAL;
        }
        return estado;
    }

    // ------------------------------------------------------------------
    // Acciones básicas
    // ------------------------------------------------------------------

    /**
     * Manda a Cabito a dormir. Mientras duerma recupera energía con el tiempo.
     */
    public void dormir() {
        dormido = true;
        actualizarEstado();
    }

    /**
     * Despierta a Cabito aunque no haya recuperado toda la energía.
     */
    public void despertar() {
        dormido = false;
        actualizarEstado();
    }

    /**
     * Juega con Cabito: felicidad +15, energía −10, higiene −5 y +10 XP.
     * Si tiene el Balón Tadeísta, gana felicidad extra.
     * No se puede jugar si está dormido o enfermo.
     *
     * @return {@code true} si pudo jugar; {@code false} si el estado no lo permite
     */
    public boolean jugar() {
        if (!estado.permiteJugar()) {
            return false;
        }
        setFelicidad(felicidad + JUGAR_FELICIDAD);
        setEnergia(energia - JUGAR_ENERGIA);
        setHigiene(higiene - JUGAR_HIGIENE);
        inventario.aplicarJuguetes(Actividad.JUGAR, this);
        ganarExperiencia(JUGAR_XP);
        actualizarEstado();
        return true;
    }

    /**
     * Baña a Cabito: la higiene queda en 100 y la felicidad baja 5
     * (no le gusta mucho el agua). No se puede bañar si está dormido.
     *
     * @return {@code true} si se pudo bañar; {@code false} si está dormido
     */
    public boolean banar() {
        if (estado.soloPermiteDespertar()) {
            return false;
        }
        setHigiene(MAXIMO);
        setFelicidad(felicidad - BANAR_FELICIDAD);
        actualizarEstado();
        return true;
    }

    /**
     * Le da de comer a Cabito una comida del inventario.
     *
     * @param nombreComida nombre de la comida, por ejemplo {@link Catalogo#EMPANADA}
     * @return {@code false} si no es comida, no la tiene o el estado no lo permite
     */
    public boolean alimentar(String nombreComida) {
        if (!(Catalogo.buscar(nombreComida) instanceof Comida)) {
            return false;
        }
        return usarItem(nombreComida);
    }

    /**
     * Usa un objeto del inventario: los consumibles se gastan y los
     * accesorios se equipan. Reglas según el estado:
     * <ul>
     *   <li>DORMIDO: no se puede usar nada.</li>
     *   <li>ENFERMO: se pueden usar comida y artículos de cuidado (como el
     *       botiquín), pero no equipar accesorios.</li>
     * </ul>
     *
     * @param nombre nombre del objeto
     * @return {@code true} si el objeto se usó
     */
    public boolean usarItem(String nombre) {
        if (estado.soloPermiteDespertar()) {
            return false;
        }
        Item item = Catalogo.buscar(nombre);
        if (item instanceof Accesorio && estado == Estado.ENFERMO) {
            return false;
        }
        boolean usado = inventario.usar(nombre, this);
        actualizarEstado();
        return usado;
    }

    // ------------------------------------------------------------------
    // Experiencia y niveles
    // ------------------------------------------------------------------

    /**
     * Suma experiencia y sube de nivel cada vez que se alcanza
     * {@code nivel * 100} XP. La experiencia sobrante se conserva, así que
     * con mucha XP de una vez se pueden subir varios niveles.
     *
     * @param cantidad experiencia ganada; si no es positiva no hace nada
     * @return {@code true} si subió al menos un nivel
     */
    public boolean ganarExperiencia(int cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        experiencia += cantidad;
        boolean subio = false;
        while (experiencia >= getExperienciaParaSubir()) {
            experiencia -= getExperienciaParaSubir();
            nivel++;
            subio = true;
        }
        return subio;
    }

    /**
     * Devuelve la experiencia que se necesita para pasar del nivel actual
     * al siguiente.
     *
     * @return {@code nivel * 100}
     */
    public int getExperienciaParaSubir() {
        return nivel * XP_POR_NIVEL;
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    /**
     * Ajusta un valor al rango 0–100.
     *
     * @param valor valor a ajustar
     * @return el valor dentro del rango permitido
     */
    private int limitar(int valor) {
        return Math.max(MINIMO, Math.min(MAXIMO, valor));
    }

    // ------------------------------------------------------------------
    // Getters y setters
    // Los setters de atributos limitan el valor a 0–100 pero no recalculan
    // el estado: quien los use debe llamar a actualizarEstado() al terminar.
    // ------------------------------------------------------------------

    /**
     * @return nombre de Cabito
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre nuevo nombre de Cabito
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return saciedad (0 = muerto de hambre, 100 = lleno)
     */
    public int getSaciedad() {
        return saciedad;
    }

    /**
     * @param saciedad nueva saciedad; se limita a 0–100
     */
    public void setSaciedad(int saciedad) {
        this.saciedad = limitar(saciedad);
    }

    /**
     * Devuelve el hambre tal como se muestra en la interfaz.
     *
     * @return {@code 100 - saciedad}
     */
    public int getHambre() {
        return MAXIMO - saciedad;
    }

    /**
     * @return energía (0–100)
     */
    public int getEnergia() {
        return energia;
    }

    /**
     * @param energia nueva energía; se limita a 0–100
     */
    public void setEnergia(int energia) {
        this.energia = limitar(energia);
    }

    /**
     * @return felicidad (0–100)
     */
    public int getFelicidad() {
        return felicidad;
    }

    /**
     * @param felicidad nueva felicidad; se limita a 0–100
     */
    public void setFelicidad(int felicidad) {
        this.felicidad = limitar(felicidad);
    }

    /**
     * @return higiene (0–100)
     */
    public int getHigiene() {
        return higiene;
    }

    /**
     * @param higiene nueva higiene; se limita a 0–100
     */
    public void setHigiene(int higiene) {
        this.higiene = limitar(higiene);
    }

    /**
     * @return inteligencia (0–100)
     */
    public int getInteligencia() {
        return inteligencia;
    }

    /**
     * @param inteligencia nueva inteligencia; se limita a 0–100
     */
    public void setInteligencia(int inteligencia) {
        this.inteligencia = limitar(inteligencia);
    }

    /**
     * @return nivel actual (empieza en 1)
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * Cambia el nivel directamente (por ejemplo, al cargar una partida).
     *
     * @param nivel nuevo nivel; si es menor que 1 queda en 1
     */
    public void setNivel(int nivel) {
        this.nivel = Math.max(1, nivel);
    }

    /**
     * @return experiencia acumulada dentro del nivel actual
     */
    public int getExperiencia() {
        return experiencia;
    }

    /**
     * Cambia la experiencia directamente (por ejemplo, al cargar una partida).
     *
     * @param experiencia nueva experiencia; si es negativa queda en 0
     */
    public void setExperiencia(int experiencia) {
        this.experiencia = Math.max(0, experiencia);
    }

    /**
     * @return {@code true} si Cabito fue mandado a dormir y no ha despertado
     */
    public boolean isDormido() {
        return dormido;
    }

    /**
     * @return inventario con las monedas y los objetos de Cabito
     */
    public Inventario getInventario() {
        return inventario;
    }

    /**
     * @return el último estado calculado por {@link #actualizarEstado()}
     */
    public Estado getEstado() {
        return estado;
    }
}
