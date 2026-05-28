package com.umg.mundial.dao;

import com.umg.mundial.model.Equipo;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO implements CRUD<Equipo> {
    
    // ── LECTURA (READ) ──
    @Override
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

    // ── CREAR (CREATE) ──
    @Override
    public boolean insertar(Equipo e) {
        String sql = "INSERT INTO equipos (pais, grupo, id_grupo) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getPais());
            ps.setString(2, e.getGrupo());
            if (e.getIdGrupo() != null) ps.setInt(3, e.getIdGrupo());
            else ps.setNull(3, java.sql.Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al insertar: " + ex.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR (UPDATE) ──
    @Override
    public boolean actualizar(Equipo e) {
        String sql = "UPDATE equipos SET pais=?, grupo=?, id_grupo=? WHERE id_equipo=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getPais());
            ps.setString(2, e.getGrupo());
            if (e.getIdGrupo() != null) ps.setInt(3, e.getIdGrupo());
            else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setInt(4, e.getIdequipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al actualizar: " + ex.getMessage());
            return false;
        }
    }

    // ── ELIMINAR (DELETE) ──
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM equipos WHERE id_equipo=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar: " + ex.getMessage());
            return false;
        }
    }
}