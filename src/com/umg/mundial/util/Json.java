package com.umg.mundial.util;

import com.umg.mundial.model.*;
import java.util.List;

public class Json {

    // ─── Helper para escapar caracteres especiales en Strings ───────────────
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    // ─── EQUIPOS ─────────────────────────────────────────────────────────────
    public static String equiposToJson(List<Equipo> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Equipo e = lista.get(i);
            sb.append(String.format(
                "{\"idEquipo\":%d,\"pais\":\"%s\",\"grupo\":\"%s\",\"idGrupo\":%s}",
                e.getIdequipo(),
                esc(e.getPais()),
                esc(e.getGrupo()),
                e.getIdGrupo() != null ? e.getIdGrupo() : "null"
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── ESTADIOS ────────────────────────────────────────────────────────────
    public static String estadiosToJson(List<Estadio> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Estadio e = lista.get(i);
            sb.append(String.format(
                "{\"idEstadio\":%d,\"nombre\":\"%s\",\"ciudad\":\"%s\",\"pais\":\"%s\",\"capacidad\":%d}",
                e.getIdEstadio(),
                esc(e.getNombre_Estadios()),
                esc(e.getCiudad()),
                esc(e.getPais()),
                e.getCapacidad()
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── GRUPOS ──────────────────────────────────────────────────────────────
    public static String gruposToJson(List<Grupos> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Grupos g = lista.get(i);
            sb.append(String.format(
                "{\"idGrupo\":%d,\"nombre\":\"%s\"}",
                g.getIdGrupo(),
                esc(g.getNombre())
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── JUGADORES (CORREGIDO) ───────────────────────────────────────────────
    public static String jugadorestoJson(List<Jugador> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Jugador j = lista.get(i);
            sb.append(String.format(
                "{\"idJugador\":%d,\"nombre\":\"%s\",\"posicion\":\"%s\",\"dorsal\":%d," +
                "\"idEquipo\":%d,\"paisEquipo\":\"%s\"}",
                j.getIdJugador(),
                esc(j.getNombre()),
                esc(j.getPosicion()),
                j.getDorsal(),
                j.getIdEquipo(),
                esc(j.getPaisEquipo() != null ? j.getPaisEquipo() : "")
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── ENTRENADORES ────────────────────────────────────────────────────────
    public static String entrenadorestoJson(List<Entrenador> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Entrenador en = lista.get(i);
            String paisEquipo = (en.getEquipo() != null) ? esc(en.getEquipo().getPais()) : "";
            int idEquipo      = (en.getEquipo() != null) ? en.getEquipo().getIdequipo() : 0;
            sb.append(String.format(
                "{\"idEntrenador\":%d,\"nombre\":\"%s\",\"idEquipo\":%d,\"paisEquipo\":\"%s\"}",
                en.getIdEntrenador(),
                esc(en.getNombre()), // Usando el método heredado de Persona
                idEquipo,
                paisEquipo
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── PARTIDOS ────────────────────────────────────────────────────────────
    public static String partidosToJson(List<Partido> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Partido p = lista.get(i);
            String local     = (p.getId_Equipo_Local()     != null) ? esc(p.getId_Equipo_Local().getPais())     : "";
            String visitante = (p.getId_Equipo_Visitante() != null) ? esc(p.getId_Equipo_Visitante().getPais()) : "";
            int idLocal      = (p.getId_Equipo_Local()     != null) ? p.getId_Equipo_Local().getIdequipo()      : 0;
            int idVisitante  = (p.getId_Equipo_Visitante() != null) ? p.getId_Equipo_Visitante().getIdequipo()  : 0;
            String estadio   = (p.getId_Estadio()          != null) ? esc(p.getId_Estadio().getNombre_Estadios()): "";
            int idEstadio    = (p.getId_Estadio()          != null) ? p.getId_Estadio().getIdEstadio()           : 0;
            String fecha     = (p.getFecha()               != null) ? p.getFecha().toString()                    : "";

            sb.append(String.format(
                "{\"idPartido\":%d,\"fase\":\"%s\",\"grupo\":\"%s\"," +
                "\"idLocal\":%d,\"local\":\"%s\"," +
                "\"idVisitante\":%d,\"visitante\":\"%s\"," +
                "\"golesLocales\":%d,\"golesVisitantes\":%d," +
                "\"penalesLocales\":%s,\"penalesVisitantes\":%s," +
                "\"fecha\":\"%s\",\"idEstadio\":%d,\"estadio\":\"%s\"}",
                p.getId_Partido(),
                esc(p.getFase()),
                esc(p.getGrupo()),
                idLocal, local,
                idVisitante, visitante,
                p.getGoles_Locales(), p.getGoles_Visitantes(),
                p.getPenales_Locales()    != null ? p.getPenales_Locales()    : "null",
                p.getPenales_Visitantes() != null ? p.getPenales_Visitantes() : "null",
                fecha, idEstadio, estadio
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── GOLES ───────────────────────────────────────────────────────────────
    public static String golesToJson(List<Goles> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Goles g = lista.get(i);
            int idPartido  = (g.getId_Partido()  != null) ? g.getId_Partido().getId_Partido()   : 0;
            int idJugador  = (g.getId_Jugador()  != null) ? g.getId_Jugador().getIdJugador()    : 0;
            String jugador = (g.getId_Jugador()  != null) ? esc(g.getId_Jugador().getNombre())  : "";
            sb.append(String.format(
                "{\"idGol\":%d,\"idPartido\":%d,\"idJugador\":%d,\"jugador\":\"%s\",\"minuto\":%d}",
                g.getId_Goles(), idPartido, idJugador, jugador, g.getMinuto()
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── TARJETAS ────────────────────────────────────────────────────────────
    public static String tarjetasToJson(List<Tarjeta> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Tarjeta t = lista.get(i);
            int idPartido  = (t.getId_Partido()  != null) ? t.getId_Partido().getId_Partido()  : 0;
            int idJugador  = (t.getId_Jugador()  != null) ? t.getId_Jugador().getIdJugador()   : 0;
            String jugador = (t.getId_Jugador()  != null) ? esc(t.getId_Jugador().getNombre()) : "";
            sb.append(String.format(
                "{\"idTarjeta\":%d,\"idPartido\":%d,\"idJugador\":%d,\"jugador\":\"%s\",\"tipo\":\"%s\",\"minuto\":%d}",
                t.getId_Tarjeta(), idPartido, idJugador, jugador, esc(t.getTipo()), t.getMinuto()
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── ALINEACIONES ────────────────────────────────────────────────────────
    public static String alineacionesToJson(List<Alineaciones> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Alineaciones a = lista.get(i);
            int idPartido  = (a.getPartido()  != null) ? a.getPartido().getId_Partido()  : 0;
            int idJugador  = (a.getJugador()  != null) ? a.getJugador().getIdJugador()   : 0;
            String jugador = (a.getJugador()  != null) ? esc(a.getJugador().getNombre()) : "";
            sb.append(String.format(
                "{\"idAlineacion\":%d,\"idPartido\":%d,\"idJugador\":%d,\"jugador\":\"%s\",\"titular\":%b}",
                a.getIdAlineacion(), idPartido, idJugador, jugador, a.isTitular()
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── CAMBIOS ─────────────────────────────────────────────────────────────
    public static String cambiosToJson(List<Cambios> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Cambios c = lista.get(i);
            int idPartido     = (c.getPartido()      != null) ? c.getPartido().getId_Partido()        : 0;
            int idSale        = (c.getJugadorSale()  != null) ? c.getJugadorSale().getIdJugador()     : 0;
            String nombreSale = (c.getJugadorSale()  != null) ? esc(c.getJugadorSale().getNombre())   : "";
            int idEntra       = (c.getJugadorEntra() != null) ? c.getJugadorEntra().getIdJugador()    : 0;
            String nombreEntra= (c.getJugadorEntra() != null) ? esc(c.getJugadorEntra().getNombre())  : "";
            sb.append(String.format(
                "{\"idCambio\":%d,\"idPartido\":%d," +
                "\"idJugadorSale\":%d,\"jugadorSale\":\"%s\"," +
                "\"idJugadorEntra\":%d,\"jugadorEntra\":\"%s\",\"minuto\":%d}",
                c.getIdCambios(), idPartido,
                idSale, nombreSale,
                idEntra, nombreEntra,
                c.getMinuto()
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── POSICIONES ──────────────────────────────────────────────────────────
    public static String posicionesToJson(List<Posiciones> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Posiciones pos = lista.get(i);
            String pais  = (pos.getEquipo() != null) ? esc(pos.getEquipo().getPais()) : "";
            int idEquipo = (pos.getEquipo() != null) ? pos.getEquipo().getIdequipo()  : 0;
            sb.append(String.format(
                "{\"idPosicion\":%d,\"idEquipo\":%d,\"pais\":\"%s\"," +
                "\"puntos\":%d,\"pj\":%d,\"pg\":%d,\"pe\":%d,\"pp\":%d,\"gf\":%d,\"gc\":%d}",
                pos.getIdPosicion(), idEquipo, pais,
                pos.getPuntos(),
                pos.getPartidos_Jugados(),
                pos.getPartidosGanados(),
                pos.getPartidos_Empatados(),
                pos.getPartidos_Perdidos(),
                pos.getGoles_Favor(),
                pos.getGoles_Contra()
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    // ─── Respuesta simple de éxito/error ─────────────────────────────────────
    public static String ok(String mensaje) {
        return String.format("{\"ok\":true,\"mensaje\":\"%s\"}", esc(mensaje));
    }

    public static String error(String mensaje) {
        return String.format("{\"ok\":false,\"mensaje\":\"%s\"}", esc(mensaje));
    }
}