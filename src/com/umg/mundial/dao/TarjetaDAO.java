package com.umg.mundial.dao;

import com.umg.mundial.model.*;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarjetaDAO {

    // ── Listar TODAS las tarjetas (para la API general) ───────────────────────
    public List<Tarjeta> listar() {
        List<Tarjeta> lista = new ArrayList<>();
        String sql = "SELECT t.id_tarjeta, t.tipo, t.minuto, " +
                     "t.id_partido, t.id_jugador, j.nombre " +
                     "FROM tarjetas t " +
                     "JOIN jugadores j ON t.id_jugador = j.id_jugador " +
                     "ORDER BY t.id_partido, t.minuto";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tarjeta tar = new Tarjeta();
                tar.setId_Tarjeta(rs.getInt("id_tarjeta"));
                tar.setTipo(rs.getString("tipo"));
                tar.setMinuto(rs.getInt("minuto"));

                Partido p = new Partido();
                p.setId_Partido(rs.getInt("id_partido"));
                tar.setId_Partido(p);

                Jugador j = new Jugador();
                j.setIdJugador(rs.getInt("id_jugador"));
                j.setNombre(rs.getString("nombre"));
                tar.setId_Jugador(j);

                lista.add(tar);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tarjetas: " + e.getMessage());
        }
        return lista;
    }

    // ── Listar tarjetas de un partido específico ──────────────────────────────
    public List<Tarjeta> listarPorPartido(int idPartido) {
        List<Tarjeta> lista = new ArrayList<>();
        String sql = "SELECT t.id_tarjeta, t.tipo, t.minuto, t.id_jugador, j.nombre " +
                     "FROM tarjetas t " +
                     "JOIN jugadores j ON t.id_jugador = j.id_jugador " +
                     "WHERE t.id_partido = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tarjeta tar = new Tarjeta();
                    tar.setId_Tarjeta(rs.getInt("id_tarjeta"));
                    tar.setTipo(rs.getString("tipo"));
                    tar.setMinuto(rs.getInt("minuto"));

                    Jugador j = new Jugador();
                    j.setIdJugador(rs.getInt("id_jugador"));
                    j.setNombre(rs.getString("nombre"));
                    tar.setId_Jugador(j);

                    lista.add(tar);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tarjetas por partido: " + e.getMessage());
        }
        return lista;
    }
}
