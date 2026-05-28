package com.umg.mundial.api.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.umg.mundial.api.Servidor;
import com.umg.mundial.dao.JugadorDAO;
import com.umg.mundial.dao.PartidoDAO;
import com.umg.mundial.model.Jugador;
import com.umg.mundial.model.Partido;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReporteController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Servidor.setCors(exchange);
        if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }

        if ("GET".equals(exchange.getRequestMethod())) {
            List<Jugador> jugadores = new JugadorDAO().listar();
            List<Partido> partidos = new PartidoDAO().listar();
            List<String> statsJson = new ArrayList<>();

            for (Jugador port : jugadores) {
                if ("Portero".equals(port.getPosicion())) {
                    int idEq = port.getIdEquipo();
                    int golesEnContra = 0;
                    int partidosJugados = 0;

                    for (Partido p : partidos) {
                        if (p.getId_Equipo_Local() != null && p.getId_Equipo_Visitante() != null) {
                            boolean esLocal = p.getId_Equipo_Local().getIdequipo() == idEq;
                            boolean esVisitante = p.getId_Equipo_Visitante().getIdequipo() == idEq;
                            
                            if (esLocal || esVisitante) {
                                partidosJugados++;
                                golesEnContra += esLocal ? p.getGoles_Visitantes() : p.getGoles_Locales();
                            }
                        }
                    }
                    if (partidosJugados > 0) {
                        double promedio = (double) golesEnContra / partidosJugados;
                        statsJson.add(String.format(java.util.Locale.US, "{\"nombre\":\"%s\", \"promedio\":%f}", port.getNombre(), promedio));
                    }
                }
            }
            Servidor.responder(exchange, "[" + String.join(",", statsJson) + "]");
        }
    }
}