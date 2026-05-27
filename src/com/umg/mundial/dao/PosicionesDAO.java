package com.umg.mundial.dao;

import com.umg.mundial.model.Posiciones;
import com.umg.mundial.model.Equipo;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PosicionesDAO {

    public List<Posiciones> listar() {
        List<Posiciones> lista = new ArrayList<>();
        String sql = "SELECT p.id_posicion, p.id_equipo, p.puntos, " +
                     "p.partidos_jugados, p.partidos_ganados, " +
                     "p.partidos_empatados, p.partidos_perdidos, " +
                     "p.goles_favor, p.goles_contra, e.pais " +
                     "FROM posiciones p " +
                     "JOIN equipos e ON p.id_equipo = e.id_equipo " +
                     "ORDER BY p.puntos DESC, (p.goles_favor - p.goles_contra) DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Posiciones pos = new Posiciones();
                pos.setIdPosicion(rs.getInt("id_posicion"));
                pos.setPuntos(rs.getInt("puntos"));
                pos.setPartidos_Jugados(rs.getInt("partidos_jugados"));
                pos.setPartidosGanados(rs.getInt("partidos_ganados"));
                pos.setPartidos_Empatados(rs.getInt("partidos_empatados"));
                pos.setPartidos_Perdidos(rs.getInt("partidos_perdidos"));
                pos.setGoles_Favor(rs.getInt("goles_favor"));
                pos.setGoles_Contra(rs.getInt("goles_contra"));

                Equipo eq = new Equipo();
                eq.setIdequipo(rs.getInt("id_equipo"));
                eq.setPais(rs.getString("pais"));
                pos.setEquipo(eq);

                lista.add(pos);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar posiciones: " + e.getMessage());
        }
        return lista;
    }
}
