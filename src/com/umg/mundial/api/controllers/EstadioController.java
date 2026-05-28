package com.umg.mundial.api.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.umg.mundial.api.Servidor;
import com.umg.mundial.dao.EstadioDAO;
import com.umg.mundial.model.Estadio;
import com.umg.mundial.util.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EstadioController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Servidor.setCors(exchange);
        String method = exchange.getRequestMethod();
        if ("OPTIONS".equals(method)) { exchange.sendResponseHeaders(204, -1); return; }
        EstadioDAO dao = new EstadioDAO();
        if ("GET".equals(method)) { Servidor.responder(exchange, Json.estadiosToJson(dao.listar())); } 
        else if ("POST".equals(method) || "PUT".equals(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            try {
                Estadio e = new Estadio();
                int id = Servidor.extraerInt(body, "id_estadio");
                e.setIdEstadio(id); e.setNombre_Estadios(Servidor.extraerString(body, "nombre"));
                e.setCiudad(Servidor.extraerString(body, "ciudad")); e.setPais(Servidor.extraerString(body, "pais"));
                e.setCapacidad(Servidor.extraerInt(body, "capacidad"));
                if ((id > 0 ? dao.actualizar(e) : dao.insertar(e))) Servidor.responder(exchange, Json.ok("Guardado"));
                else Servidor.responder(exchange, Json.error("Error BD"));
            } catch (Exception ex) { Servidor.responder(exchange, Json.error(ex.getMessage())); }
        } else if ("DELETE".equals(method)) {
            if (dao.eliminar(Servidor.extraerInt(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), "id_estadio"))) Servidor.responder(exchange, Json.ok("Eliminado"));
            else Servidor.responder(exchange, Json.error("Error al eliminar"));
        }
    }
}