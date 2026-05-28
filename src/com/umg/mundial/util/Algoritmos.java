package com.umg.mundial.util;

import com.umg.mundial.model.Jugador;
import java.util.ArrayList;
import java.util.List;

public class Algoritmos {
    
    // Algoritmo de Ordenamiento: Método de la Burbuja (Bubble Sort)
    public static void ordenarJugadoresPorNombre(List<Jugador> lista) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // Si el nombre actual es mayor alfabéticamente que el siguiente, se intercambian
                if (lista.get(j).getNombre().compareToIgnoreCase(lista.get(j + 1).getNombre()) > 0) {
                    Jugador temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
    }

    // Algoritmo de Búsqueda: Búsqueda Lineal / Secuencial
    public static List<Jugador> buscarJugador(List<Jugador> lista, String texto) {
        List<Jugador> resultados = new ArrayList<>();
        String query = texto.toLowerCase();
        for (Jugador j : lista) {
            if (j.getNombre().toLowerCase().contains(query)) resultados.add(j);
        }
        return resultados;
    }
}