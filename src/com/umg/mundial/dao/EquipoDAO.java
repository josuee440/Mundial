package com.umg.mundial.dao;

import com.umg.mundial.model.Equipo;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {
    
    public List<Equipo> listar() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT id_equipo, pais, grupo, id_grupo FROM equipos"; 
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Equipo e = new Equipo();
                e.setIdequipo(rs.getInt("id_equipo"));
                e.setPais(rs.getString("pais"));
                e.setGrupo(rs.getString("grupo"));
                e.setIdGrupo(rs.getObject("id_grupo") != null ? rs.getInt("id_grupo") : null);
                lista.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos: " + e.getMessage());
        }
        return lista;
    }
}