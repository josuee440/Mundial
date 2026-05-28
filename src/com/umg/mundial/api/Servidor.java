package com.umg.mundial.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.umg.mundial.dao.*;
import com.umg.mundial.util.Json;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Servidor {

    public static void iniciar() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // ── CONTROLADORES ────────────────────────────────────────────────────
        server.createContext("/api/equipos", new com.umg.mundial.api.controllers.EquipoController());
        server.createContext("/api/jugadores", new com.umg.mundial.api.controllers.JugadorController());
        server.createContext("/api/estadios", new com.umg.mundial.api.controllers.EstadioController());
        server.createContext("/api/reportes/porteros", new com.umg.mundial.api.controllers.ReporteController());
        server.createContext("/api/simulador/grupos", new com.umg.mundial.api.controllers.SimuladorController());
        server.createContext("/api/simulador/partido", new com.umg.mundial.api.controllers.SimuladorController());

        // ── RESTO DE ENTIDADES (GET, SIMULADOR, ETC) ─────────────────────────
        server.createContext("/api/grupos", exchange -> { setCors(exchange); if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; } if ("GET".equals(exchange.getRequestMethod())) { responder(exchange, Json.gruposToJson(new GruposDAO().listar())); } });
        server.createContext("/api/entrenadores", exchange -> { setCors(exchange); if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; } if ("GET".equals(exchange.getRequestMethod())) { responder(exchange, Json.entrenadorestoJson(new EntrenadorDAO().listar())); } });
        server.createContext("/api/posiciones", exchange -> { setCors(exchange); if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; } if ("GET".equals(exchange.getRequestMethod())) { responder(exchange, Json.posicionesToJson(new PosicionesDAO().listar())); } });

        // PARTIDOS
        server.createContext("/api/partidos", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) { responder(exchange, Json.partidosToJson(new PartidoDAO().listar())); } 
            else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    com.umg.mundial.model.Partido p = new com.umg.mundial.model.Partido();
                    p.setFase(extraerString(body, "fase")); p.setGrupo("");
                    p.setGoles_Locales(extraerInt(body, "golesL")); p.setGoles_Visitantes(extraerInt(body, "golesV"));
                    
                    com.umg.mundial.model.Equipo local = new com.umg.mundial.model.Equipo(); local.setIdequipo(extraerInt(body, "idLocal")); p.setId_Equipo_Local(local);
                    com.umg.mundial.model.Equipo vis = new com.umg.mundial.model.Equipo(); vis.setIdequipo(extraerInt(body, "idVisitante")); p.setId_Equipo_Visitante(vis);
                    com.umg.mundial.model.Estadio est = new com.umg.mundial.model.Estadio(); int idEst = extraerInt(body, "idEstadio"); est.setIdEstadio(idEst > 0 ? idEst : 1); p.setId_Estadio(est);
                    
                    responder(exchange, String.format("{\"ok\":true,\"idPartido\":%d}", new PartidoDAO().insertar(p)));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // GOLES
        server.createContext("/api/goles", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) { responder(exchange, Json.golesToJson(new GolesDAO().listar())); } 
            else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    String sql = "INSERT INTO goles (id_partido, id_jugador, minuto) VALUES (?,?,?)";
                    try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion(); java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, extraerInt(body, "idPartido")); ps.setInt(2, extraerInt(body, "idJugador")); ps.setInt(3, extraerInt(body, "minuto")); ps.executeUpdate();
                    }
                    responder(exchange, Json.ok("Gol guardado"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // TARJETAS
        server.createContext("/api/tarjetas", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) { responder(exchange, Json.tarjetasToJson(new TarjetaDAO().listar())); } 
            else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    String sql = "INSERT INTO tarjetas (id_partido, id_jugador, tipo, minuto) VALUES (?,?,?,?)";
                    try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion(); java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, extraerInt(body, "idPartido")); ps.setInt(2, extraerInt(body, "idJugador"));
                        ps.setString(3, extraerString(body, "tipo")); ps.setInt(4, extraerInt(body, "minuto")); ps.executeUpdate();
                    }
                    responder(exchange, Json.ok("Tarjeta guardada"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // POSICIONES ACTUALIZAR
        server.createContext("/api/posiciones/actualizar", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    int idEq = extraerInt(body, "idEquipo");
                    try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion(); java.sql.PreparedStatement psc = con.prepareStatement("SELECT COUNT(*) FROM posiciones WHERE id_equipo=?")) {
                        psc.setInt(1, idEq); java.sql.ResultSet rs = psc.executeQuery(); rs.next();
                        String sql = rs.getInt(1) > 0 ? "UPDATE posiciones SET puntos=?,partidos_jugados=?,partidos_ganados=?,partidos_empatados=?,partidos_perdidos=?,goles_favor=?,goles_contra=? WHERE id_equipo=?"
                            : "INSERT INTO posiciones (puntos,partidos_jugados,partidos_ganados,partidos_empatados,partidos_perdidos,goles_favor,goles_contra,id_equipo) VALUES (?,?,?,?,?,?,?,?)";
                        try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                            ps.setInt(1, extraerInt(body, "puntos")); ps.setInt(2, extraerInt(body, "pj")); ps.setInt(3, extraerInt(body, "pg"));
                            ps.setInt(4, extraerInt(body, "pe")); ps.setInt(5, extraerInt(body, "pp")); ps.setInt(6, extraerInt(body, "gf"));
                            ps.setInt(7, extraerInt(body, "gc")); ps.setInt(8, idEq); ps.executeUpdate();
                        }
                    }
                    responder(exchange, Json.ok("Posicion actualizada"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // LIMPIAR BD
        server.createContext("/api/limpiar", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("POST".equals(exchange.getRequestMethod())) {
                try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion()) {
                    for (String tabla : new String[]{"goles","tarjetas","alineaciones","cambios","posiciones","partidos"}) {
                        con.prepareStatement("DELETE FROM " + tabla).executeUpdate();
                    }
                    responder(exchange, Json.ok("BD limpiada"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // FRONTEND
        server.createContext("/", exchange -> {
            setCors(exchange);
            
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            File file = new File("frontend" + path);
            if (!file.exists()) { String msg = "404 No encontrado"; exchange.sendResponseHeaders(404, msg.length()); exchange.getResponseBody().write(msg.getBytes()); exchange.getResponseBody().close(); return; }
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
            
            if (path.endsWith(".css")) {
                exchange.getResponseHeaders().set("Content-Type", "text/css; charset=UTF-8");
            } else if (path.endsWith(".js")) {
                exchange.getResponseHeaders().set("Content-Type", "application/javascript; charset=UTF-8");
            } else {
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            }
            
            exchange.sendResponseHeaders(200, bytes.length); exchange.getResponseBody().write(bytes); exchange.getResponseBody().close();
        });

        server.setExecutor(null);
        server.start();
        System.out.println("══════════════════════════════════════════");
        System.out.println("  Servidor iniciado en http://localhost:8080");
        System.out.println("══════════════════════════════════════════");
    }

    public static void responder(HttpExchange ex, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    public static void setCors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    public static String extraerString(String json, String clave) {
        String buscar = "\"" + clave + "\""; int idx = json.indexOf(buscar); if (idx < 0) return "";
        int ini = json.indexOf("\"", idx + buscar.length() + 1) + 1; int fin = json.indexOf("\"", ini); return json.substring(ini, fin);
    }

    public static int extraerInt(String json, String clave) {
        String buscar = "\"" + clave + "\""; int idx = json.indexOf(buscar); if (idx < 0) return 0;
        int ini = idx + buscar.length(); while (ini < json.length() && !Character.isDigit(json.charAt(ini))) ini++;
        int fin = ini; while (fin < json.length() && Character.isDigit(json.charAt(fin))) fin++;
        try { return Integer.parseInt(json.substring(ini, fin)); } catch (NumberFormatException e) { return 0; }
    }
}