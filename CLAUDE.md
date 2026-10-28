# Cabito: Aventura Tadeísta — Proyecto Algoritmos y Programación (UTadeo 2026-II)

## Contexto
Aplicación de escritorio en Java de mascota virtual protagonizada por Cabito, la mascota de la Universidad Jorge Tadeo Lozano. Proyecto de curso en grupo; entrega la semana del 9 al 13 de noviembre de 2026 con sustentación presencial. La rúbrica exige que compile y ejecute sin errores, y el código debe estar bien documentado (Javadoc) y seguir buenas prácticas. Los integrantes deben poder explicar cada parte del código, así que prioriza claridad sobre trucos.

## Stack y reglas técnicas
- Java 17+, interfaz con **Swing** (sin JavaFX).
- Proyecto Maven con estructura estándar (`src/main/java`, `src/test/java`). Única dependencia externa: Gson para guardar partidas. JUnit 5 para pruebas.
- Paquetes: `modelo`, `modelo.items`, `modelo.mundos`, `minijuegos`, `persistencia`, `controlador`, `vista`.
- Patrón modelo-vista-controlador: el modelo no importa nada de Swing.
- Javadoc en todas las clases y métodos públicos, en español. Nombres en español.
- Después de cada etapa: compilar (`mvn compile`), correr pruebas (`mvn test`) y hacer commit en git.

## Requisitos del enunciado (todos obligatorios)
1. Interfaz gráfica amigable que muestre a Cabito, sus atributos, su estado y las acciones.
2. Mínimo 5 atributos de estado que cambian con acciones y con el paso del tiempo.
3. Estados automáticos determinados con condicionales a partir de los atributos, que cambian la imagen de Cabito y las acciones disponibles.
4. Mínimo 3 mundos inspirados en la universidad, cada uno con actividades distintas.
5. Tienda Tadeísta: comprar con monedas; los objetos van a un inventario y tienen efectos sobre los atributos.
6. Experiencia y niveles, con desbloqueos.
7. Mínimo un minijuego con lógica algorítmica.
8. Guardar y cargar partida: nombre, atributos, nivel, experiencia, monedas, inventario y progreso en mundos.
9. Usar estructuras de datos: matriz en el minijuego, y una `LinkedList` para el historial de acciones/logros.

## Diseño del modelo

### Cabito (atributos 0–100, donde 100 siempre es "bien")
- 5 atributos de estado: `saciedad` (inverso del hambre; se muestra en la UI como "Hambre" = 100 - saciedad), `energia`, `felicidad`, `higiene`, `inteligencia`. No hay atributo de salud.
- Progreso: `nivel`, `experiencia`. XP para subir de nivel = `nivel * 100`.
- Todos los setters limitan el valor al rango 0–100.

### Paso del tiempo (javax.swing.Timer, cada 10 segundos de juego)
- saciedad −3, energía −2, higiene −2, felicidad −1.
- Si saciedad < 20 o higiene < 20: felicidad −2 adicional.
- Si está DORMIDO: energía +8 y no baja la saciedad tan rápido (−1).

### Estado (enum) — `actualizarEstado()` evalúa en este orden de prioridad
1. DORMIDO si el usuario lo mandó a dormir y energía < 100.
2. ENFERMO si saciedad < 15 o higiene < 15.
3. HAMBRIENTO si saciedad < 30.
4. CANSADO si energía < 25.
5. SUCIO si higiene < 25.
6. TRISTE si felicidad < 30.
7. FELIZ si felicidad > 75 y todos los demás > 50.
8. NORMAL en otro caso.

Efecto en acciones: DORMIDO solo permite "Despertar"; ENFERMO no permite jugar ni minijuegos (solo comer, bañar, dormir o usar botiquín); CANSADO no permite minijuegos.

### Acciones básicas
- Alimentar (requiere comida del inventario), Jugar (felicidad +15, energía −10, higiene −5, +10 XP), Dormir, Bañar (higiene = 100, felicidad −5), Explorar (abre el mundo actual).

### Items (clase abstracta `Item` con `aplicar(Cabito)`; subclases Comida, Juguete, Accesorio, ItemCuidado)
Comida e ItemCuidado se consumen; Juguete y Accesorio son permanentes (Accesorio se equipa y da un bonus continuo).

| Item | Tipo | Precio | Efecto |
|---|---|---|---|
| Tinto de la cafetería | Comida | 10 | energía +20, saciedad +5 |
| Empanada | Comida | 15 | saciedad +25 |
| Almuerzo ejecutivo | Comida | 35 | saciedad +50, felicidad +5 |
| Botella de agua UTadeo | Comida | 8 | energía +10 |
| Balón Tadeísta | Juguete | 40 | jugar da +10 felicidad extra |
| Cuaderno UTadeo | Juguete | 30 | estudiar en Biblioteca da +5 inteligencia extra |
| Kit de aseo | ItemCuidado | 20 | higiene +40 |
| Botiquín de bienestar | ItemCuidado | 45 | saciedad +20, higiene +20, energía +20 (saca a Cabito de ENFERMO) |
| Gorra UTadeo | Accesorio | 60 | felicidad baja la mitad de rápido |
| Saco "UTadeo" | Accesorio | 120 | felicidad +10 al equiparlo; desbloquea en nivel 3 |
| Termo Tadeísta | Accesorio | 80 | energía baja la mitad de rápido |
| Mochila UTadeo | Accesorio | 100 | +20% monedas en actividades; desbloquea en nivel 5 |

### Mundos (clase abstracta `Mundo`)
- **La Brújula** (nivel 1, desbloqueado al inicio): zona social. Actividad "Recorrer el campus": monedas aleatorias 5–25, felicidad +10, energía −10, +15 XP. Eventos aleatorios ("¡Encontraste una moneda!").
- **Biblioteca** (nivel 2): actividad "Estudiar": inteligencia +10, energía −15, +25 XP. Minijuego de preguntas sobre la UTadeo (5 preguntas de un banco de 15; 5 monedas por respuesta correcta).
- **Módulo 20** (nivel 4): actividad "Taller/Laboratorio": inteligencia +5, felicidad +5, +30 XP. Aquí está el minijuego de memoria.

### Minijuego de memoria (implementa interfaz `Minijuego`)
- Tablero `Carta[][]` de 4x4 con 8 parejas de íconos tadeístas, barajado con Fisher–Yates.
- Puntaje según intentos: ≤12 intentos = 50 monedas, ≤18 = 30, más = 15. Siempre +40 XP.

### Persistencia (`GestorPartidas`)
- Guarda cada partida como JSON en la carpeta `partidas/` con el nombre de la partida.
- Al cargar, lista las partidas existentes para elegir una. Manejar errores de archivo con mensajes al usuario, nunca con stack traces.

## Interfaz (Swing)
Basada en la imagen de referencia del enunciado:
- Barra superior: título, nivel, barra de XP, monedas, botones Guardar y Cargar.
- Centro: fondo del mundo actual, Cabito dibujado según su estado (empieza con figuras/emoji dibujados con Graphics2D o imágenes PNG en `src/main/resources/img/`; deja fácil cambiar las imágenes), globo de diálogo, botones de acción.
- Panel derecho: barras de progreso (`JProgressBar`) de cada atributo con color.
- Panel inferior: tarjetas de mundos (bloqueados muestran "Nivel X requerido"), y navegación: Mundos, Tienda Tadeísta, Inventario, Minijuegos, Logros.
- Pantalla inicial: Nueva partida (pide nombre) o Cargar partida.
- Colores institucionales azul oscuro y teal; fuente legible.

## Plan de trabajo (una etapa a la vez; espera mi confirmación entre etapas)
1. Estructura Maven, paquetes, `Cabito`, `Estado`, lógica de tiempo y pruebas JUnit de `actualizarEstado()`.
2. Items, `Inventario`, `Tienda` y sus pruebas.
3. Mundos y sistema de niveles/desbloqueos.
4. Minijuego de memoria y de preguntas (primero lógica con pruebas, luego panel Swing).
5. Persistencia con Gson y pruebas de guardar/cargar ida y vuelta.
6. Interfaz completa conectada vía `JuegoController`.
7. Pulido: mensajes, validaciones, Javadoc completo, README con instrucciones de ejecución.

Al terminar cada etapa, resume qué clases creaste y qué decisión de diseño conviene saber explicar en la sustentación.
