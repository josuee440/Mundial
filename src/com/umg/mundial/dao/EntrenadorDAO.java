package com.umg.mundial.dao;

import com.umg.mundial.model.Entrenador;
import com.umg.mundial.model.Equipo;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorDAO {
    public List<Entrenador> listar() {
        List<Entrenador> lista = new ArrayList<>();
        String sql = "SELECT t.id_entrenador, t.nombre_entrenador, t.id_equipo, e.pais " +
                     "FROM entrenadores t " +
                     "JOIN equipos e ON t.id_equipo = e.id_equipo";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Entrenador t = new Entrenador();
                t.setIdEntrenador(rs.getInt("id_entrenador"));
                t.setNombre(rs.getString("nombre_entrenador")); // Usando el método heredado de Persona

                // Llenamos el objeto Equipo relacionado
                Equipo eq = new Equipo();
                eq.setIdequipo(rs.getInt("id_equipo"));
                eq.setPais(rs.getString("pais"));
                t.setEquipo(eq);

                lista.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar entrenadores: " + e.getMessage());
        }
        return lista;
    }
}
