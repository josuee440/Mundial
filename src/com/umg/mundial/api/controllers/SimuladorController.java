package com.umg.mundial.api.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.umg.mundial.api.Servidor;
import com.umg.mundial.dao.EquipoDAO;
import com.umg.mundial.model.Equipo;
import com.umg.mundial.util.Conexion;
import com.umg.mundial.util.Json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.Collections;

public class SimuladorController implements HttpHandler {
    // Helper record para ordenar equipos en la fase de grupos
    private record EquipoStats(Equipo equipo, int[] stats) implements Comparable<EquipoStats> {
        // stats: [pj, pg, pe, pp, gf, gc, pts]
        public int getPuntos() { return stats[6]; }
        public int getDifGoles() { return stats[4] - stats[5]; }
        public int getGolesFavor() { return stats[4]; }

        @Override
        public int compareTo(EquipoStats other) {
            int ptsCompare = Integer.compare(other.getPuntos(), this.getPuntos());
            if (ptsCompare != 0) return ptsCompare;
            int gdCompare = Integer.compare(other.getDifGoles(), this.getDifGoles());
            if (gdCompare != 0) return gdCompare;
            return Integer.compare(other.getGolesFavor(), this.getGolesFavor());
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Servidor.setCors(exchange);
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if ("/api/simulador/grupos".equals(path) && "POST".equals(exchange.getRequestMethod())) {
            simularFaseGrupos(exchange);
        } else if ("/api/simulador/partido".equals(path) && "POST".equals(exchange.getRequestMethod())) {
            simularPartidoIndividual(exchange);
        } else {
            Servidor.responder(exchange, Json.error("Ruta no válida"));
        }
    }

    // Simula las llaves completas de la Fase de Grupos
    private void simularFaseGrupos(HttpExchange exchange) throws IOException {
        try (Connection con = Conexion.getConexion()) {
            // Limpiar todo el historial de torneos anteriores para empezar de cero
            try (Statement st = con.createStatement()) { 
                st.execute("DELETE FROM goles");
                st.execute("DELETE FROM tarjetas");
                st.execute("DELETE FROM cambios");
                st.execute("DELETE FROM alineaciones");
                st.execute("DELETE FROM posiciones");
                st.execute("DELETE FROM partidos");
            }

            List<Equipo> equipos = new EquipoDAO().listar();
            if (equipos.size() < 48) {
                Servidor.responder(exchange, Json.error("Se necesitan 48 equipos para la simulación. Actualmente hay " + equipos.size()));
                return;
            }
            Map<String, List<Equipo>> grupos = new HashMap<>();
            for (Equipo e : equipos) {
                String g = e.getGrupo() != null ? e.getGrupo() : "?";
                grupos.putIfAbsent(g, new ArrayList<>());
                grupos.get(g).add(e);
            }

            // id_equipo -> [pj, pg, pe, pp, gf, gc, pts]
            Map<Integer, int[]> stats = new HashMap<>();
            for (Equipo e : equipos) stats.put(e.getIdequipo(), new int[7]);

            for (List<Equipo> grupo : grupos.values()) {
                for (int i = 0; i < grupo.size(); i++) {
                    for (int j = i + 1; j < grupo.size(); j++) {
                        Equipo local = grupo.get(i);
                        Equipo vis = grupo.get(j);
                        jugarYRegistrar(con, "FASE DE GRUPOS", local.getIdequipo(), vis.getIdequipo(), stats, false);
                    }
                }
            }

            // Lógica de clasificación para 48 equipos
            Map<String, List<EquipoStats>> tablasDeGrupos = new HashMap<>();
            for (Equipo e : equipos) {
                String g = e.getGrupo() != null ? e.getGrupo() : "?";
                tablasDeGrupos.putIfAbsent(g, new ArrayList<>());
                tablasDeGrupos.get(g).add(new EquipoStats(e, stats.get(e.getIdequipo())));
            }

            List<Equipo> clasificados = new ArrayList<>();
            List<EquipoStats> terceros = new ArrayList<>();

            for (List<EquipoStats> grupo : tablasDeGrupos.values()) {
                Collections.sort(grupo); // Ordena usando el compareTo del record
                if (grupo.size() > 0) clasificados.add(grupo.get(0).equipo());
                if (grupo.size() > 1) clasificados.add(grupo.get(1).equipo());
                if (grupo.size() > 2) terceros.add(grupo.get(2));
            }

            Collections.sort(terceros); // Ordena la lista de terceros
            for (int i = 0; i < 8 && i < terceros.size(); i++) {
                clasificados.add(terceros.get(i).equipo());
            }

            // Guardar nuevas posiciones
            String sqlPos = "INSERT INTO posiciones (id_equipo, puntos, partidos_jugados, partidos_ganados, partidos_empatados, partidos_perdidos, goles_favor, goles_contra) VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sqlPos)) {
                for (Map.Entry<Integer, int[]> entry : stats.entrySet()) {
                    int[] s = entry.getValue();
                    ps.setInt(1, entry.getKey()); ps.setInt(2, s[6]); ps.setInt(3, s[0]);
                    ps.setInt(4, s[1]); ps.setInt(5, s[2]); ps.setInt(6, s[3]);
                    ps.setInt(7, s[4]); ps.setInt(8, s[5]); ps.executeUpdate();
                }
            }
            // Responder con los 32 equipos clasificados
            Servidor.responder(exchange, Json.equiposToJson(clasificados));
        } catch (Exception e) {
            Servidor.responder(exchange, Json.error(e.getMessage()));
        }
    }

    // Simula un partido individual (para fase de llaves/eliminatoria)
    private void simularPartidoIndividual(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String fase = Servidor.extraerString(body, "fase");
        int idLocal = Servidor.extraerInt(body, "idLocal");
        int idVisitante = Servidor.extraerInt(body, "idVisitante");
        boolean eliminatoria = body.contains("\"eliminatoria\":true");

        try (Connection con = Conexion.getConexion()) {
            int[] res = jugarYRegistrar(con, fase, idLocal, idVisitante, null, eliminatoria);
            String jsonResp = String.format("{\"golesL\":%d, \"golesV\":%d, \"idGanador\":%d}", res[0], res[1], res[2]);
            Servidor.responder(exchange, jsonResp);
        } catch (Exception e) { Servidor.responder(exchange, Json.error(e.getMessage())); }
    }

    // Motor principal matemático y aleatorio (Goles y Tarjetas para la rúbrica)
    private int[] jugarYRegistrar(Connection con, String fase, int idLocal, int idVis, Map<Integer, int[]> stats, boolean eliminatoria) throws SQLException {
        Random rand = new Random();
        int golesL = rand.nextInt(4); int golesV = rand.nextInt(4);
        if (eliminatoria && golesL == golesV) { if (rand.nextBoolean()) golesL++; else golesV++; }

        // 1. Guardar partido
        String sqlPart = "INSERT INTO partidos (fase, id_equipo_local, id_equipo_visitante, goles_locales, goles_visitantes, fecha, id_estadio) VALUES (?, ?, ?, ?, ?, NOW(), 1) RETURNING id_partido";
        int idPartido = 0;
        try (PreparedStatement ps = con.prepareStatement(sqlPart)) {
            ps.setString(1, fase); ps.setInt(2, idLocal); ps.setInt(3, idVis); ps.setInt(4, golesL); ps.setInt(5, golesV);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) idPartido = rs.getInt(1); }
        }

        // 2. Traer jugadores y asignar eventos (Goles a nombre de un jugador y tarjetas)
        List<Integer> jLocal = getJugadores(con, idLocal);
        List<Integer> jVis = getJugadores(con, idVis);
        insertarGoles(con, idPartido, jLocal, golesL, rand);
        insertarGoles(con, idPartido, jVis, golesV, rand);
        insertarTarjetas(con, idPartido, jLocal, jVis, rand);
        insertarCambios(con, idPartido, jLocal, jVis, rand);

        // 3. Actualizar estadística de la tabla del torneo
        if (stats != null) {
            int[] sl = stats.get(idLocal); int[] sv = stats.get(idVis);
            sl[0]++; sv[0]++; sl[4] += golesL; sl[5] += golesV; sv[4] += golesV; sv[5] += golesL;
            if (golesL > golesV) { sl[1]++; sl[6] += 3; sv[3]++; }
            else if (golesV > golesL) { sv[1]++; sv[6] += 3; sl[3]++; }
            else { sl[2]++; sv[2]++; sl[6]++; sv[6]++; }
        }
        int idGanador = (golesL > golesV) ? idLocal : (golesV > golesL ? idVis : 0);
        return new int[]{golesL, golesV, idGanador};
    }

    private List<Integer> getJugadores(Connection con, int idEq) throws SQLException {
        List<Integer> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT id_jugador FROM jugadores WHERE id_equipo = ?")) {
            ps.setInt(1, idEq); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) lista.add(rs.getInt(1)); }
        } return lista;
    }

    private void insertarGoles(Connection con, int idPartido, List<Integer> jugadores, int goles, Random rand) throws SQLException {
        if (jugadores.isEmpty() || goles == 0) return;
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO goles (id_partido, id_jugador, minuto) VALUES (?, ?, ?)")) {
            for (int i = 0; i < goles; i++) {
                ps.setInt(1, idPartido); ps.setInt(2, jugadores.get(rand.nextInt(jugadores.size())));
                ps.setInt(3, rand.nextInt(90) + 1); ps.executeUpdate();
            }
        }
    }

    private void insertarTarjetas(Connection con, int idPartido, List<Integer> jL, List<Integer> jV, Random rand) throws SQLException {
        int numTarjetas = rand.nextInt(4); if (numTarjetas == 0) return;
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO tarjetas (id_partido, id_jugador, tipo, minuto) VALUES (?, ?, ?, ?)")) {
            for (int i = 0; i < numTarjetas; i++) {
                List<Integer> equipo = rand.nextBoolean() ? jL : jV; if (equipo.isEmpty()) continue;
                ps.setInt(1, idPartido); ps.setInt(2, equipo.get(rand.nextInt(equipo.size())));
                ps.setString(3, rand.nextInt(10) > 8 ? "Roja" : "Amarilla"); ps.setInt(4, rand.nextInt(90) + 1); ps.executeUpdate();
            }
        }
    }

    // Simula lesiones (cambios por lesión) para cumplir al 100% con la rúbrica
    private void insertarCambios(Connection con, int idPartido, List<Integer> jL, List<Integer> jV, Random rand) throws SQLException {
        int numCambios = rand.nextInt(3); // 0 a 2 lesiones/cambios por partido
        if (numCambios == 0) return;
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO cambios (id_partido, jugador_sale, jugador_entra, minuto) VALUES (?, ?, ?, ?)")) {
            for (int i = 0; i < numCambios; i++) {
                List<Integer> equipo = rand.nextBoolean() ? jL : jV; if (equipo.size() < 2) continue;
                int idxSale = rand.nextInt(equipo.size());
                int idxEntra = (idxSale + 1) % equipo.size();
                ps.setInt(1, idPartido); ps.setInt(2, equipo.get(idxSale));
                ps.setInt(3, equipo.get(idxEntra)); ps.setInt(4, rand.nextInt(90) + 1); ps.executeUpdate();
            }
        }
    }
}