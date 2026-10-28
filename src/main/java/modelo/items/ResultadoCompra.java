package modelo.items;

/**
 * Resultado de intentar comprar un objeto en la tienda, con el mensaje que
 * se le muestra al usuario.
 */
public enum ResultadoCompra {

    /** La compra se hizo. */
    EXITO("¡Compra realizada!"),
    /** El objeto no está en el catálogo. */
    NO_EXISTE("Ese objeto no existe en la tienda."),
    /** Cabito aún no tiene el nivel para comprarlo. */
    NIVEL_INSUFICIENTE("Necesitas más nivel para comprar este objeto."),
    /** Es un objeto permanente que ya se compró. */
    YA_LO_TIENE("Ya tienes este objeto."),
    /** No alcanzan las monedas. */
    MONEDAS_INSUFICIENTES("No tienes suficientes monedas.");

    private final String mensaje;

    /**
     * @param mensaje texto para el usuario
     */
    ResultadoCompra(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return texto para mostrar al usuario
     */
    public String getMensaje() {
        return mensaje;
    }
}
