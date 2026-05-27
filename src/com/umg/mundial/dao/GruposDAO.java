package com.umg.mundial.dao;

import com.umg.mundial.model.Grupos;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GruposDAO {
    public List<Grupos> listar() {
        List<Grupos> lista = new ArrayList<>();
        // Columnas: id_grupo, nombre
        String sql = "SELECT id_grupo, nombre FROM grupos";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Grupos g = new Grupos();
                g.setIdGrupo(rs.getInt("id_grupo"));
                g.setNombre(rs.getString("nombre"));
                lista.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar grupos: " + e.getMessage());
        }
        return lista;
    }
}
