import java.util.*;

/**
 * APE4_Grafos.java
 * Caso de estudio: Mapa del campus universitario y búsqueda de rutas.
 *
 * Se implementa un grafo no dirigido y ponderado que representa ubicaciones
 * dentro del campus de la UTA. Se aplican dos algoritmos de búsqueda de rutas:
 *   - BFS  (Breadth-First Search): ruta con MENOS PARADAS (saltos).
 *   - Dijkstra: ruta con MENOR DISTANCIA (suma de pesos).
 *
 * Estructura de datos utilizada:
 *   - Map<String, Nodo>         → almacena los nodos del grafo indexados por ID.
 *   - Map<String, List<Arista>> → lista de adyacencia, relaciona cada nodo
 *                                  con sus vecinos y el peso de la arista.
 */
public class APE4_Grafos {

    // ═══════════════════════════════════════
    // Nodo
    // Representa una ubicación dentro del campus.
    // ═══════════════════════════════════════
    static class Nodo {
        String id;      // Identificador único (ej. "fisei")
        String nombre;  // Nombre legible (ej. "FISEI")

        public Nodo(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
    }

    // ═══════════════════════════════════════
    // Arista
    // Representa una conexión entre dos nodos con un peso (distancia en metros).
    // ═══════════════════════════════════════
    static class Arista {
        String destino; // ID del nodo destino
        int peso;       // Distancia en metros entre los dos nodos

        public Arista(String destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    // ═══════════════════════════════════════
    // Grafo
    // Contiene la lógica completa: agregar nodos/aristas y algoritmos de búsqueda.
    // ═══════════════════════════════════════
    static class Grafo {

        // Almacén de nodos: ID -> Nodo
        Map<String, Nodo> nodos = new HashMap<>();

        // Lista de adyacencia: ID -> lista de aristas salientes
        Map<String, List<Arista>> adyacencia = new HashMap<>();

        // ═══════════════════════════════════
        // TODO 1
        // Agregar nodo al grafo
        // ═══════════════════════════════════
        public void agregarNodo(String id, String nombre) {

            // Se crea el objeto Nodo y se guarda en el mapa de nodos
            nodos.put(id, new Nodo(id, nombre));

            // Se inicializa la lista de adyacencia vacía para este nodo,
            // garantizando que exista una entrada aunque no tenga aristas aún.
            adyacencia.put(id, new ArrayList<>());
        }

        // ═══════════════════════════════════
        // TODO 2
        // Agregar arista no dirigida (bidireccional)
        // ═══════════════════════════════════
        public void agregarArista(String origen, String destino, int peso) {

            // Se agrega la arista en ambas direcciones porque el grafo NO es dirigido.
            // origen -> destino
            adyacencia.get(origen).add(new Arista(destino, peso));
            // destino -> origen (regreso)
            adyacencia.get(destino).add(new Arista(origen, peso));
        }

        // ═══════════════════════════════════
        // TODO 3 — BFS
        // Ruta con menos paradas (menor número de nodos intermedios).
        // BFS explora nivel por nivel, garantizando que el primer camino
        // que llega al destino es el que tiene menos saltos.
        // ═══════════════════════════════════
        public List<String> bfs(String inicio, String fin) {

            // Cola para recorrer niveles; cada elemento es un camino completo
            Queue<List<String>> cola = new LinkedList<>();

            // Nodos visitados para no repetir recorrido
            Set<String> visitados = new HashSet<>();

            // Camino inicial que contiene solo el nodo de inicio
            List<String> caminoInicial = new ArrayList<>();

            // TODO: Agregar nodo inicio al camino inicial
            caminoInicial.add(inicio);
            // El nodo inicio se inserta primero en el camino.

            // TODO: Agregar caminoInicial a la cola
            cola.add(caminoInicial);
            // El camino inicial (de un solo nodo) entra a la cola de procesamiento.

            // TODO: Marcar inicio como visitado
            visitados.add(inicio);
            // Se marca para no volver a procesarlo y evitar ciclos.

            while (!cola.isEmpty()) {

                // TODO: Obtener el primer camino de la cola (FIFO)
                List<String> camino = cola.poll();
                // poll() retorna y elimina la cabeza de la cola.

                // Nodo actual: el último en el camino explorado hasta ahora
                String actual = camino.get(camino.size() - 1);

                // Si llegamos al destino, retornamos el camino completo
                if (actual.equals(fin)) {
                    return camino;
                }

                // Recorrer vecinos del nodo actual
                for (Arista arista : adyacencia.get(actual)) {

                    // TODO: Verificar si el vecino NO fue visitado
                    if (!visitados.contains(arista.destino)) {
                        // Solo procesamos nodos no visitados para evitar ciclos.

                        // TODO: Marcar vecino como visitado
                        visitados.add(arista.destino);
                        // Se marca antes de encolar para no añadirlo varias veces.

                        // Crear nuevo camino extendido con el vecino
                        List<String> nuevoCamino = new ArrayList<>(camino);

                        // TODO: Agregar vecino al nuevo camino
                        nuevoCamino.add(arista.destino);
                        // Se extiende el camino actual con el vecino encontrado.

                        // TODO: Agregar nuevoCamino a la cola
                        cola.add(nuevoCamino);
                        // El nuevo camino se encola para continuar la exploración.
                    }
                }
            }

            // Si la cola se vació y nunca llegamos al destino, no hay ruta
            return null;
        }

        // ═══════════════════════════════════
        // TODO 4 — Dijkstra
        // Ruta con menor distancia (menor suma de pesos de las aristas).
        // Dijkstra usa una cola de prioridad para siempre explorar primero
        // el nodo con menor distancia acumulada desde el inicio.
        // ═══════════════════════════════════
        public List<String> dijkstra(String inicio, String fin) {

            // Mapa de distancias mínimas conocidas desde el inicio a cada nodo
            Map<String, Integer> distancias = new HashMap<>();

            // Mapa de nodo anterior en la ruta óptima (para reconstruir el camino)
            Map<String, String> anteriores = new HashMap<>();

            // Cola de prioridad que ordena nodos por su distancia acumulada
            PriorityQueue<String> cola = new PriorityQueue<>(
                    Comparator.comparingInt(distancias::get)
            );

            // Inicializar distancias a "infinito" para todos los nodos
            for (String nodo : nodos.keySet()) {

                // TODO: Inicializar distancia infinita
                distancias.put(nodo, Integer.MAX_VALUE);
                // Integer.MAX_VALUE representa "aún no alcanzable".
            }

            // TODO: Distancia del inicio = 0
            distancias.put(inicio, 0);
            // El punto de partida tiene distancia 0 a sí mismo.

            // TODO: Agregar inicio a la cola
            cola.add(inicio);
            // Insertamos el nodo de inicio; la cola lo procesará primero (dist=0).

            while (!cola.isEmpty()) {

                // TODO: Obtener nodo con menor distancia
                String actual = cola.poll();
                // poll() extrae el nodo con menor distancia acumulada.

                for (Arista arista : adyacencia.get(actual)) {

                    // TODO: Calcular nueva distancia
                    int nuevaDistancia = distancias.get(actual) + arista.peso;
                    // Suma la distancia ya acumulada al nodo actual más el peso de la arista.

                    // TODO: Verificar si nuevaDistancia es menor que la conocida
                    if (nuevaDistancia < distancias.get(arista.destino)) {
                        // Se encontró un camino más corto hacia arista.destino.

                        // TODO: Actualizar distancia
                        distancias.put(arista.destino, nuevaDistancia);
                        // Se registra la nueva distancia mínima.

                        // TODO: Guardar nodo anterior
                        anteriores.put(arista.destino, actual);
                        // Necesario para reconstruir el camino al final.

                        // TODO: Agregar vecino a la cola
                        cola.add(arista.destino);
                        // Se re-encola el vecino con la distancia actualizada.
                    }
                }
            }

            // Reconstruir camino de fin hacia inicio siguiendo el mapa de anteriores
            List<String> camino = new ArrayList<>();

            String actual = fin;

            while (actual != null) {

                camino.add(0, actual); // Insertamos al frente para orden correcto

                actual = anteriores.get(actual);
            }

            return camino;
        }

        // ═══════════════════════════════════
        // Mostrar resultado
        // Imprime la ruta en formato: NombreNodo (id) -> NombreNodo (id) -> ...
        // ═══════════════════════════════════
        public void mostrarRuta(List<String> ruta) {

            if (ruta == null) {
                System.out.println("No existe ruta");
                return;
            }

            for (int i = 0; i < ruta.size(); i++) {

                String idNodo = ruta.get(i);

                Nodo nodo = nodos.get(idNodo);

                System.out.print(
                    nodo.nombre + " (" + nodo.id + ")"
                );

                if (i < ruta.size() - 1) {
                    System.out.print(" -> ");
                }
            }

            System.out.println();
        }
    }

    // ═══════════════════════════════════════
    // MAIN
    // Construye el grafo del campus y ejecuta las pruebas de búsqueda.
    // ═══════════════════════════════════════
    public static void main(String[] args) {

        Grafo grafo = new Grafo();

        // ── NODOS: Ubicaciones del campus ──────────────────────
        grafo.agregarNodo("uta",        "Universidad");
        grafo.agregarNodo("fisei",      "FISEI");
        grafo.agregarNodo("idiomas",    "Idiomas");
        grafo.agregarNodo("biblioteca", "Biblioteca");
        grafo.agregarNodo("estadio",    "Estadio");
        grafo.agregarNodo("comedor",    "Comedor");

        // ── ARISTAS: Conexiones entre ubicaciones (distancias en metros) ─
        grafo.agregarArista("uta",       "fisei",     50);
        grafo.agregarArista("fisei",     "idiomas",   40);
        grafo.agregarArista("idiomas",   "biblioteca", 30);
        grafo.agregarArista("biblioteca","estadio",   70);

        // Ruta con menos paradas pero mayor distancia total
        grafo.agregarArista("uta",     "comedor", 20);
        grafo.agregarArista("comedor", "estadio", 200);

        // ═══════════════════════════════════
        // PRUEBAS
        // Se busca la ruta de "uta" (inicio) a "estadio" (fin).
        //
        // Rutas posibles:
        //   A) uta -> comedor -> estadio         (2 saltos, 220m)
        //   B) uta -> fisei -> idiomas -> biblioteca -> estadio (4 saltos, 190m)
        //
        // BFS elige A (menos saltos).
        // Dijkstra elige B (menor distancia).
        // ═══════════════════════════════════

        System.out.println("===== BFS =====");
        System.out.println("Criterio: menos paradas (saltos)");

        List<String> rutaBFS = grafo.bfs("uta", "estadio");
        grafo.mostrarRuta(rutaBFS);

        System.out.println("\n===== DIJKSTRA =====");
        System.out.println("Criterio: menor distancia (metros)");

        List<String> rutaDijkstra = grafo.dijkstra("uta", "estadio");
        grafo.mostrarRuta(rutaDijkstra);
    }
}
