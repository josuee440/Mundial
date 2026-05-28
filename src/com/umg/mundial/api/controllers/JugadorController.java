package com.umg.mundial.api.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.umg.mundial.api.Servidor;
import com.umg.mundial.dao.JugadorDAO;
import com.umg.mundial.model.Jugador;
import com.umg.mundial.util.Json;
import com.umg.mundial.util.Algoritmos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JugadorController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Servidor.setCors(exchange);
        String method = exchange.getRequestMethod();
        if ("OPTIONS".equals(method)) { exchange.sendResponseHeaders(204, -1); return; }
        JugadorDAO dao = new JugadorDAO();
        
        if ("GET".equals(method)) { 
            List<Jugador> jugadores = dao.listar();
            // Aplicando Algoritmo de Ordenamiento en Java antes de enviar al frontend (Rúbrica: 20%)
            Algoritmos.ordenarJugadoresPorNombre(jugadores);
            Servidor.responder(exchange, Json.jugadorestoJson(jugadores)); 
        } 
        else if ("POST".equals(method) || "PUT".equals(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            try {
                Jugador j = new Jugador();
                int id = Servidor.extraerInt(body, "id_jugador");
                j.setIdJugador(id); j.setNombre(Servidor.extraerString(body, "nombre")); j.setPosicion(Servidor.extraerString(body, "posicion"));
                j.setDorsal(Servidor.extraerInt(body, "dorsal")); j.setIdEquipo(Servidor.extraerInt(body, "id_equipo"));
                if ((id > 0 ? dao.actualizar(j) : dao.insertar(j))) Servidor.responder(exchange, Json.ok("Guardado"));
                else Servidor.responder(exchange, Json.error("Error BD"));
            } catch (Exception ex) { Servidor.responder(exchange, Json.error(ex.getMessage())); }
        } else if ("DELETE".equals(method)) {
            if (dao.eliminar(Servidor.extraerInt(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), "id_jugador"))) Servidor.responder(exchange, Json.ok("Eliminado"));
            else Servidor.responder(exchange, Json.error("Error al eliminar"));
        }
    }
}