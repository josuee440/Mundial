package com.umg.mundial.dao;

import com.umg.mundial.model.*;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlineacionesDAO {

    // ── Listar TODAS las alineaciones (para la API general) ───────────────────
    public List<Alineaciones> listar() {
        List<Alineaciones> lista = new ArrayList<>();
        String sql = "SELECT a.id_alineacion, a.titular, " +
                     "a.id_partido, a.id_jugador, j.nombre " +
                     "FROM alineaciones a " +
                     "JOIN jugadores j ON a.id_jugador = j.id_jugador " +
                     "ORDER BY a.id_partido";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Alineaciones al = new Alineaciones();
                al.setIdAlineacion(rs.getInt("id_alineacion"));
                al.setTitular(rs.getBoolean("titular"));

                Partido p = new Partido();
                p.setId_Partido(rs.getInt("id_partido"));
                al.setPartido(p);

                Jugador j = new Jugador();
                j.setIdJugador(rs.getInt("id_jugador"));
                j.setNombre(rs.getString("nombre"));
                al.setJugador(j);

                lista.add(al);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar alineaciones: " + e.getMessage());
        }
        return lista;
    }

    // ── Listar alineación de un partido específico ────────────────────────────
    public List<Alineaciones> listarPorPartido(int idPartido) {
        List<Alineaciones> lista = new ArrayList<>();
        String sql = "SELECT a.id_alineacion, a.titular, a.id_jugador, j.nombre " +
                     "FROM alineaciones a " +
                     "JOIN jugadores j ON a.id_jugador = j.id_jugador " +
                     "WHERE a.id_partido = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Alineaciones al = new Alineaciones();
                    al.setIdAlineacion(rs.getInt("id_alineacion"));
                    al.setTitular(rs.getBoolean("titular"));

                    Jugador j = new Jugador();
                    j.setIdJugador(rs.getInt("id_jugador"));
                    j.setNombre(rs.getString("nombre"));
                    al.setJugador(j);

                    lista.add(al);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar alineaciones por partido: " + e.getMessage());
        }
        return lista;
    }
}
