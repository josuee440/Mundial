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

        // ── EQUIPOS (CRUD COMPLETO) ──────────────────────────────────────────
        server.createContext("/api/equipos", exchange -> {
            setCors(exchange);
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equals(method)) { exchange.sendResponseHeaders(204, -1); return; }
            
            EquipoDAO dao = new EquipoDAO();

            // LECTURA (READ)
            if ("GET".equals(method)) {
                responder(exchange, Json.equiposToJson(dao.listar()));
            } 
            // CREAR Y ACTUALIZAR (CREATE / UPDATE)
            else if ("POST".equals(method) || "PUT".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    int id = extraerInt(body, "id_equipo"); // Vendrá en 0 si es nuevo
                    String pais = extraerString(body, "pais");
                    String grupo = extraerString(body, "grupo");
                    int idGrupo = extraerInt(body, "id_grupo");

                    com.umg.mundial.model.Equipo e = new com.umg.mundial.model.Equipo();
                    e.setIdequipo(id);
                    e.setPais(pais);
                    e.setGrupo(grupo);
                    e.setIdGrupo(idGrupo > 0 ? idGrupo : null);

                    boolean exito = (id > 0) ? dao.actualizar(e) : dao.insertar(e);

                    if (exito) responder(exchange, Json.ok("Equipo guardado correctamente"));
                    else responder(exchange, Json.error("Error al guardar equipo en BD"));
                } catch (Exception ex) {
                    responder(exchange, Json.error(ex.getMessage()));
                }
            } 
            // ELIMINAR (DELETE)
            else if ("DELETE".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                int id = extraerInt(body, "id_equipo");
                if (dao.eliminar(id)) responder(exchange, Json.ok("Equipo eliminado"));
                else responder(exchange, Json.error("Error al eliminar equipo"));
            }
        });

        // ── ESTADIOS ──────────────────────────────────────────────────────────
        server.createContext("/api/estadios", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.estadiosToJson(new EstadioDAO().listar()));
            }
        });

        // ── GRUPOS ────────────────────────────────────────────────────────────
        server.createContext("/api/grupos", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.gruposToJson(new GruposDAO().listar()));
            }
        });

        // ── JUGADORES ─────────────────────────────────────────────────────────
        server.createContext("/api/jugadores", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.jugadorestoJson(new JugadorDAO().listar()));
            }
        });

        // ── ENTRENADORES ──────────────────────────────────────────────────────
        server.createContext("/api/entrenadores", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.entrenadorestoJson(new EntrenadorDAO().listar()));
            }
        });

        // ── PARTIDOS ──────────────────────────────────────────────────────────
        server.createContext("/api/partidos", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }

            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.partidosToJson(new PartidoDAO().listar()));

            } else if ("POST".equals(exchange.getRequestMethod())) {
                // Leer el cuerpo JSON enviado desde el simulador
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    String fase      = extraerString(body, "fase");
                    int idLocal      = extraerInt(body, "idLocal");
                    int idVisitante  = extraerInt(body, "idVisitante");
                    int golesL       = extraerInt(body, "golesL");
                    int golesV       = extraerInt(body, "golesV");

                    com.umg.mundial.model.Partido p = new com.umg.mundial.model.Partido();
                    p.setFase(fase);
                    p.setGrupo("");
                    p.setGoles_Locales(golesL);
                    p.setGoles_Visitantes(golesV);

                    com.umg.mundial.model.Equipo local = new com.umg.mundial.model.Equipo();
                    local.setIdequipo(idLocal);
                    p.setId_Equipo_Local(local);

                    com.umg.mundial.model.Equipo visitante = new com.umg.mundial.model.Equipo();
                    visitante.setIdequipo(idVisitante);
                    p.setId_Equipo_Visitante(visitante);

                    // Estadio 1 por defecto (puedes cambiarlo)
                    com.umg.mundial.model.Estadio est = new com.umg.mundial.model.Estadio();
                    int idEst = extraerInt(body, "idEstadio");
                    est.setIdEstadio(idEst > 0 ? idEst : 1);
                    p.setId_Estadio(est);

                    int idGenerado = new PartidoDAO().insertar(p);
                    responder(exchange, String.format("{\"ok\":true,\"idPartido\":%d}", idGenerado));

                } catch (Exception e) {
                    System.err.println("Error al guardar partido: " + e.getMessage());
                    responder(exchange, Json.error("Error al guardar: " + e.getMessage()));
                }
            }
        });

        // ── GOLES ─────────────────────────────────────────────────────────────
        server.createContext("/api/goles", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.golesToJson(new GolesDAO().listar()));
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    int idPartido = extraerInt(body, "idPartido");
                    int idJugador = extraerInt(body, "idJugador");
                    int minuto    = extraerInt(body, "minuto");
                    String sql = "INSERT INTO goles (id_partido, id_jugador, minuto) VALUES (?,?,?)";
                    try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion();
                         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, idPartido); ps.setInt(2, idJugador); ps.setInt(3, minuto);
                        ps.executeUpdate();
                    }
                    responder(exchange, Json.ok("Gol guardado"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // ── TARJETAS POST ─────────────────────────────────────────────────────
        server.createContext("/api/tarjetas", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.tarjetasToJson(new TarjetaDAO().listar()));
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    int idPartido = extraerInt(body, "idPartido");
                    int idJugador = extraerInt(body, "idJugador");
                    int minuto    = extraerInt(body, "minuto");
                    String tipo   = extraerString(body, "tipo");
                    String sql = "INSERT INTO tarjetas (id_partido, id_jugador, tipo, minuto) VALUES (?,?,?,?)";
                    try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion();
                         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, idPartido); ps.setInt(2, idJugador);
                        ps.setString(3, tipo);   ps.setInt(4, minuto);
                        ps.executeUpdate();
                    }
                    responder(exchange, Json.ok("Tarjeta guardada"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // ── POSICIONES POST ───────────────────────────────────────────────────
        server.createContext("/api/posiciones/actualizar", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    int idEquipo = extraerInt(body, "idEquipo");
                    int puntos   = extraerInt(body, "puntos");
                    int pj       = extraerInt(body, "pj");
                    int pg       = extraerInt(body, "pg");
                    int pe       = extraerInt(body, "pe");
                    int pp       = extraerInt(body, "pp");
                    int gf       = extraerInt(body, "gf");
                    int gc       = extraerInt(body, "gc");
                    
                    String check = "SELECT COUNT(*) FROM posiciones WHERE id_equipo=?";
                    try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion();
                         java.sql.PreparedStatement ps = con.prepareStatement(check)) {
                        ps.setInt(1, idEquipo);
                        java.sql.ResultSet rs = ps.executeQuery();
                        rs.next();
                        int count = rs.getInt(1);
                        String sql = count > 0
                            ? "UPDATE posiciones SET puntos=?,partidos_jugados=?,partidos_ganados=?,partidos_empatados=?,partidos_perdidos=?,goles_favor=?,goles_contra=? WHERE id_equipo=?"
                            : "INSERT INTO posiciones (puntos,partidos_jugados,partidos_ganados,partidos_empatados,partidos_perdidos,goles_favor,goles_contra,id_equipo) VALUES (?,?,?,?,?,?,?,?)";
                        try (java.sql.PreparedStatement ps2 = con.prepareStatement(sql)) {
                            ps2.setInt(1,puntos); ps2.setInt(2,pj); ps2.setInt(3,pg);
                            ps2.setInt(4,pe);     ps2.setInt(5,pp); ps2.setInt(6,gf);
                            ps2.setInt(7,gc);     ps2.setInt(8,idEquipo);
                            ps2.executeUpdate();
                        }
                    }
                    responder(exchange, Json.ok("Posicion actualizada"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // ── ALINEACIONES ──────────────────────────────────────────────────────
        server.createContext("/api/alineaciones", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.alineacionesToJson(new AlineacionesDAO().listar()));
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    int idPartido = extraerInt(body, "idPartido");
                    int idJugador = extraerInt(body, "idJugador");
                    boolean titular = body.contains("\"titular\":true");
                    String sql = "INSERT INTO alineaciones (id_partido, id_jugador, titular) VALUES (?,?,?)";
                    try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion();
                         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, idPartido);
                        ps.setInt(2, idJugador);
                        ps.setBoolean(3, titular);
                        ps.executeUpdate();
                    }
                    responder(exchange, Json.ok("Alineacion guardada"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // ── CAMBIOS ───────────────────────────────────────────────────────────
        server.createContext("/api/cambios", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.cambiosToJson(new CambiosDAO().listar()));
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    int idPartido    = extraerInt(body, "idPartido");
                    int jugadorSale  = extraerInt(body, "jugadorSale");
                    int jugadorEntra = extraerInt(body, "jugadorEntra");
                    int minuto       = extraerInt(body, "minuto");
                    String sql = "INSERT INTO cambios (id_partido, jugador_sale, jugador_entra, minuto) VALUES (?,?,?,?)";
                    try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion();
                         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, idPartido);
                        ps.setInt(2, jugadorSale);
                        ps.setInt(3, jugadorEntra);
                        ps.setInt(4, minuto);
                        ps.executeUpdate();
                    }
                    responder(exchange, Json.ok("Cambio guardado"));
                } catch (Exception e) { responder(exchange, Json.error(e.getMessage())); }
            }
        });

        // ── POSICIONES ────────────────────────────────────────────────────────
        server.createContext("/api/posiciones", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("GET".equals(exchange.getRequestMethod())) {
                responder(exchange, Json.posicionesToJson(new PosicionesDAO().listar()));
            }
        });

        // ── LIMPIAR BD ────────────────────────────────────────────────────────
        server.createContext("/api/limpiar", exchange -> {
            setCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("POST".equals(exchange.getRequestMethod())) {
                try (java.sql.Connection con = com.umg.mundial.util.Conexion.getConexion()) {
                    String[] tablas = {"goles","tarjetas","alineaciones","cambios","posiciones","partidos"};
                    for (String tabla : tablas) {
                        con.prepareStatement("DELETE FROM " + tabla).executeUpdate();
                    }
                    System.out.println("BD limpiada correctamente");
                    responder(exchange, Json.ok("BD limpiada"));
                } catch (Exception e) {
                    System.err.println("Error al limpiar: " + e.getMessage());
                    responder(exchange, Json.error(e.getMessage()));
                }
            }
        });

        // ── FRONTEND (sirve index.html) ────────────────────────────────────────
        server.createContext("/", exchange -> {
            setCors(exchange);
            File file = new File("frontend/index.html");
            if (!file.exists()) {
                String msg = "Archivo frontend/index.html no encontrado";
                exchange.sendResponseHeaders(404, msg.length());
                exchange.getResponseBody().write(msg.getBytes());
                exchange.getResponseBody().close();
                return;
            }
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        });

        server.setExecutor(null);
        server.start();

        System.out.println("══════════════════════════════════════════");
        System.out.println("  Servidor iniciado en http://localhost:8080");
        System.out.println("  Abre tu navegador en esa dirección.");
        System.out.println("  Presiona Ctrl+C para detener.");
        System.out.println("══════════════════════════════════════════");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void responder(HttpExchange ex, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void setCors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static String extraerString(String json, String clave) {
        String buscar = "\"" + clave + "\"";
        int idx = json.indexOf(buscar);
        if (idx < 0) return "";
        int ini = json.indexOf("\"", idx + buscar.length() + 1) + 1;
        int fin = json.indexOf("\"", ini);
        return json.substring(ini, fin);
    }

    private static int extraerInt(String json, String clave) {
        String buscar = "\"" + clave + "\"";
        int idx = json.indexOf(buscar);
        if (idx < 0) return 0;
        int ini = idx + buscar.length();
        while (ini < json.length() && !Character.isDigit(json.charAt(ini))) ini++;
        int fin = ini;
        while (fin < json.length() && Character.isDigit(json.charAt(fin))) fin++;
        try { return Integer.parseInt(json.substring(ini, fin)); }
        catch (NumberFormatException e) { return 0; }
    }
}