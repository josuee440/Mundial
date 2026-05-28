package com.umg.mundial.dao;

import com.umg.mundial.model.Jugador;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO implements CRUD<Jugador> {
    
    // ── LECTURA (READ) ──
    @Override
    public List<Jugador> listar() {
        List<Jugador> lista = new ArrayList<>();
        // Hacemos JOIN con equipos para obtener el nombre del país del jugador
        String sql = "SELECT j.id_jugador, j.nombre, j.posicion, j.dorsal, j.id_equipo, e.pais as pais_equipo " +
                     "FROM jugadores j LEFT JOIN equipos e ON j.id_equipo = e.id_equipo"; 
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Jugador j = new Jugador();
                j.setIdJugador(rs.getInt("id_jugador"));
                j.setNombre(rs.getString("nombre"));
                j.setPosicion(rs.getString("posicion"));
                j.setDorsal(rs.getInt("dorsal"));
                j.setIdEquipo(rs.getInt("id_equipo"));
                j.setPaisEquipo(rs.getString("pais_equipo"));
                lista.add(j);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar jugadores: " + e.getMessage());
        }
        return lista;
    }

    // ── CREAR (CREATE) ──
    @Override
    public boolean insertar(Jugador j) {
        String sql = "INSERT INTO jugadores (nombre, posicion, dorsal, id_equipo) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, j.getNombre());
            ps.setString(2, j.getPosicion());
            ps.setInt(3, j.getDorsal());
            ps.setInt(4, j.getIdEquipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al insertar jugador: " + ex.getMessage());
            return false;
        }
    }

    // ── ACTUALIZAR (UPDATE) ──
    @Override
    public boolean actualizar(Jugador j) {
        String sql = "UPDATE jugadores SET nombre=?, posicion=?, dorsal=?, id_equipo=? WHERE id_jugador=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, j.getNombre());
            ps.setString(2, j.getPosicion());
            ps.setInt(3, j.getDorsal());
            ps.setInt(4, j.getIdEquipo());
            ps.setInt(5, j.getIdJugador());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al actualizar jugador: " + ex.getMessage());
            return false;
        }
    }

    // ── ELIMINAR (DELETE) ──
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM jugadores WHERE id_jugador=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar jugador: " + ex.getMessage());
            return false;
        }
    }
}