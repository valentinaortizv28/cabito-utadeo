package controlador;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import minijuegos.Minijuego;
import modelo.Partida;

/**
 * Vista de mentiras para probar {@link JuegoController} sin abrir ventanas.
 * Guarda lo que el controlador le pide mostrar y responde a las preguntas
 * con valores preparados por cada prueba.
 */
class VistaFalsa implements VistaJuego {

    boolean inicioMostrado;
    boolean cerrada;
    Partida partidaMostrada;
    int actualizaciones;
    String ultimoDialogo;
    final List<String> avisos = new ArrayList<>();
    final List<String> errores = new ArrayList<>();
    final List<List<String>> opcionesOfrecidas = new ArrayList<>();
    Minijuego minijuegoAbierto;

    /** Respuestas que dará pedirNombre(), en orden (null = cancelar). */
    final Deque<String> nombres = new ArrayDeque<>();
    /** Índice de la opción que elegirá elegir(); -1 = cancelar. */
    int indiceElegido;
    boolean respuestaConfirmar = true;
    OpcionSalir respuestaSalir = OpcionSalir.NO_GUARDAR;

    @Override
    public void mostrarInicio() {
        inicioMostrado = true;
    }

    @Override
    public void mostrarJuego(Partida partida) {
        partidaMostrada = partida;
    }

    @Override
    public void actualizar(Partida partida) {
        actualizaciones++;
    }

    @Override
    public void mostrarDialogo(String texto) {
        ultimoDialogo = texto;
    }

    @Override
    public void mostrarAviso(String titulo, String texto) {
        avisos.add(titulo + ": " + texto);
    }

    @Override
    public void mostrarError(String texto) {
        errores.add(texto);
    }

    @Override
    public String pedirNombre() {
        return nombres.isEmpty() ? null : nombres.removeFirst();
    }

    @Override
    public String elegir(String titulo, String mensaje, List<String> opciones) {
        opcionesOfrecidas.add(opciones);
        return indiceElegido < 0 ? null : opciones.get(indiceElegido);
    }

    @Override
    public boolean confirmar(String titulo, String mensaje) {
        return respuestaConfirmar;
    }

    @Override
    public OpcionSalir preguntarGuardarAntesDeSalir() {
        return respuestaSalir;
    }

    @Override
    public void abrirMinijuego(Minijuego minijuego) {
        minijuegoAbierto = minijuego;
    }

    @Override
    public void cerrar() {
        cerrada = true;
    }

    /** @return el último aviso mostrado, o "" si no hubo */
    String ultimoAviso() {
        return avisos.isEmpty() ? "" : avisos.get(avisos.size() - 1);
    }
}
