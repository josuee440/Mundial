package com.umg.mundial.dao;

import com.umg.mundial.model.Estadio;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadioDAO implements CRUD<Estadio> {
    
    // ── LECTURA (READ) ──
    @Override
    public List<Estadio> listar() {
        List<Estadio> lista = new ArrayList<>();
        // Columna corregida a nombre_estadios
        String sql = "SELECT id_estadio, nombre_estadios, ciudad, pais, capacidad FROM estadios"; 
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Estadio e = new Estadio();
                e.setIdEstadio(rs.getInt("id_estadio"));
                e.setNombre_Estadios(rs.getString("nombre_estadios")); // <-- Se lee de la columna correcta
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

    // ── CREAR (CREATE) ──
    @Override
    public boolean insertar(Estadio e) {
        // Columna corregida a nombre_estadios
        String sql = "INSERT INTO estadios (nombre_estadios, ciudad, pais, capacidad) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre_Estadios());
            ps.setString(2, e.getCiudad());
            ps.setString(3, e.getPais());
            ps.setInt(4, e.getCapacidad());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al insertar estadio: " + ex.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR (UPDATE) ──
    @Override
    public boolean actualizar(Estadio e) {
        // Columna corregida a nombre_estadios
        String sql = "UPDATE estadios SET nombre_estadios=?, ciudad=?, pais=?, capacidad=? WHERE id_estadio=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre_Estadios());
            ps.setString(2, e.getCiudad());
            ps.setString(3, e.getPais());
            ps.setInt(4, e.getCapacidad());
            ps.setInt(5, e.getIdEstadio());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al actualizar estadio: " + ex.getMessage());
            return false;
        }
    }

    // ── ELIMINAR (DELETE) ──
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM estadios WHERE id_estadio=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar estadio: " + ex.getMessage());
            return false;
        }
    }
}