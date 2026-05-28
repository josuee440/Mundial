package com.umg.mundial.api.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.umg.mundial.api.Servidor;
import com.umg.mundial.dao.EquipoDAO;
import com.umg.mundial.model.Equipo;
import com.umg.mundial.util.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EquipoController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Servidor.setCors(exchange);
        String method = exchange.getRequestMethod();
        if ("OPTIONS".equals(method)) { exchange.sendResponseHeaders(204, -1); return; }
        EquipoDAO dao = new EquipoDAO();
        if ("GET".equals(method)) { Servidor.responder(exchange, Json.equiposToJson(dao.listar())); } 
        else if ("POST".equals(method) || "PUT".equals(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            try {
                Equipo e = new Equipo();
                int id = Servidor.extraerInt(body, "id_equipo");
                e.setIdequipo(id); e.setPais(Servidor.extraerString(body, "pais")); e.setGrupo(Servidor.extraerString(body, "grupo"));
                int idGrupo = Servidor.extraerInt(body, "id_grupo"); e.setIdGrupo(idGrupo > 0 ? idGrupo : null);
                if ((id > 0 ? dao.actualizar(e) : dao.insertar(e))) Servidor.responder(exchange, Json.ok("Guardado"));
                else Servidor.responder(exchange, Json.error("Error BD"));
            } catch (Exception ex) { Servidor.responder(exchange, Json.error(ex.getMessage())); }
        } else if ("DELETE".equals(method)) {
            if (dao.eliminar(Servidor.extraerInt(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), "id_equipo"))) Servidor.responder(exchange, Json.ok("Eliminado"));
            else Servidor.responder(exchange, Json.error("Error al eliminar"));
        }
    }
}