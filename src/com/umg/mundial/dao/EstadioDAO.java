package com.umg.mundial.dao;

import com.umg.mundial.model.Estadio;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadioDAO {
    public List<Estadio> listar() {
        List<Estadio> lista = new ArrayList<>();
        String sql = "SELECT id_estadio, nombre_estadios, ciudad, pais, capacidad FROM estadios";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Estadio e = new Estadio();
                e.setIdEstadio(rs.getInt("id_estadio"));
                e.setNombre_Estadios(rs.getString("nombre_estadios"));
                e.setCiudad(rs.getString("ciudad"));
                e.setPais(rs.getString("pais"));
                e.setCapacidad(rs.getInt("capacidad"));
                lista.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar estadios: " + e.getMessage());
        }
        return lista;
    }
}
