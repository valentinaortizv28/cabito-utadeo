package modelo;

/**
 * Cabito, la mascota virtual de la Universidad Jorge Tadeo Lozano.
 * <p>
 * Tiene cinco atributos de estado en el rango 0–100, donde 100 siempre es
 * "bien": saciedad (lo contrario del hambre), energía, felicidad, higiene e
 * inteligencia. También tiene nivel y experiencia.
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
     * </ul>
     * Al final recalcula el estado.
     */
    public void pasarTiempo() {
        if (dormido) {
            setSaciedad(saciedad - BAJA_SACIEDAD_DORMIDO);
            setEnergia(energia + SUBE_ENERGIA_DORMIDO);
        } else {
            setSaciedad(saciedad - BAJA_SACIEDAD);
            setEnergia(energia - BAJA_ENERGIA);
        }
        setHigiene(higiene - BAJA_HIGIENE);
        setFelicidad(felicidad - BAJA_FELICIDAD);

        if (saciedad < UMBRAL_DESCUIDO || higiene < UMBRAL_DESCUIDO) {
            setFelicidad(felicidad - BAJA_FELICIDAD_EXTRA);
        }
        actualizarEstado();
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
     * @return el último estado calculado por {@link #actualizarEstado()}
     */
    public Estado getEstado() {
        return estado;
    }
}
