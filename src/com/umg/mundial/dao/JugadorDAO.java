package com.umg.mundial.dao;

import com.umg.mundial.model.Equipo;
import com.umg.mundial.model.Jugador;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {
    public List<Jugador> listar() {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT j.id_jugador, j.nombre, j.posicion, j.dorsal, e.id_equipo, e.pais " +
                     "FROM jugadores j " +
                     "INNER JOIN equipos e ON j.id_equipo = e.id_equipo";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Jugador j = new Jugador();
                j.setIdJugador(rs.getInt("id_jugador"));
                j.setNombre(rs.getString("nombre"));
                j.setPosicion(rs.getString("posicion"));
                j.setDorsal(rs.getInt("dorsal")); // <--- Leemos el dorsal
                
                Equipo e = new Equipo();
                e.setIdequipo(rs.getInt("id_equipo"));
                e.setPais(rs.getString("pais"));
                
                j.setEquipo(e);
                lista.add(j);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar jugadores: " + e.getMessage());
        }
        return lista;
    }
}
