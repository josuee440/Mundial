package com.umg.mundial.dao;

import com.umg.mundial.model.*;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GolesDAO {

    // ── Listar TODOS los goles (para la API general) ──────────────────────────
    public List<Goles> listar() {
        List<Goles> lista = new ArrayList<>();
        String sql = "SELECT g.id_gol, g.minuto, " +
                     "g.id_partido, g.id_jugador, j.nombre " +
                     "FROM goles g " +
                     "JOIN jugadores j ON g.id_jugador = j.id_jugador " +
                     "ORDER BY g.id_partido, g.minuto";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Goles gol = new Goles();
                gol.setId_Goles(rs.getInt("id_gol"));
                gol.setMinuto(rs.getInt("minuto"));

                Partido p = new Partido();
                p.setId_Partido(rs.getInt("id_partido"));
                gol.setId_Partido(p);

                Jugador j = new Jugador();
                j.setIdJugador(rs.getInt("id_jugador"));
                j.setNombre(rs.getString("nombre"));
                gol.setId_Jugador(j);

                lista.add(gol);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar goles: " + e.getMessage());
        }
        return lista;
    }

    // ── Listar goles de un partido específico ─────────────────────────────────
    public List<Goles> listarPorPartido(int idPartido) {
        List<Goles> lista = new ArrayList<>();
        String sql = "SELECT g.id_gol, g.minuto, g.id_jugador, j.nombre " +
                     "FROM goles g " +
                     "JOIN jugadores j ON g.id_jugador = j.id_jugador " +
                     "WHERE g.id_partido = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Goles gol = new Goles();
                    gol.setId_Goles(rs.getInt("id_gol"));
                    gol.setMinuto(rs.getInt("minuto"));

                    Jugador j = new Jugador();
                    j.setIdJugador(rs.getInt("id_jugador"));
                    j.setNombre(rs.getString("nombre"));
                    gol.setId_Jugador(j);

                    lista.add(gol);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar goles por partido: " + e.getMessage());
        }
        return lista;
    }
}
