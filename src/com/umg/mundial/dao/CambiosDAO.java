package com.umg.mundial.dao;

import com.umg.mundial.model.*;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CambiosDAO {

    // ── Listar TODOS los cambios (para la API general) ────────────────────────
    public List<Cambios> listar() {
        List<Cambios> lista = new ArrayList<>();
        String sql = "SELECT c.id_cambio, c.minuto, c.id_partido, " +
                     "c.jugador_sale, js.nombre AS sale_nombre, " +
                     "c.jugador_entra, je.nombre AS entra_nombre " +
                     "FROM cambios c " +
                     "JOIN jugadores js ON c.jugador_sale  = js.id_jugador " +
                     "JOIN jugadores je ON c.jugador_entra = je.id_jugador " +
                     "ORDER BY c.id_partido, c.minuto";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cambios cambio = new Cambios();
                cambio.setIdCambios(rs.getInt("id_cambio"));
                cambio.setMinuto(rs.getInt("minuto"));

                Partido p = new Partido();
                p.setId_Partido(rs.getInt("id_partido"));
                cambio.setPartido(p);

                Jugador sale = new Jugador();
                sale.setIdJugador(rs.getInt("jugador_sale"));
                sale.setNombre(rs.getString("sale_nombre"));
                cambio.setJugadorSale(sale);

                Jugador entra = new Jugador();
                entra.setIdJugador(rs.getInt("jugador_entra"));
                entra.setNombre(rs.getString("entra_nombre"));
                cambio.setJugadorEntra(entra);

                lista.add(cambio);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar cambios: " + e.getMessage());
        }
        return lista;
    }

    // ── Listar cambios de un partido específico ───────────────────────────────
    public List<Cambios> listarPorPartido(int idPartido) {
        List<Cambios> lista = new ArrayList<>();
        String sql = "SELECT c.id_cambio, c.minuto, " +
                     "c.jugador_sale, js.nombre AS sale_nombre, " +
                     "c.jugador_entra, je.nombre AS entra_nombre " +
                     "FROM cambios c " +
                     "JOIN jugadores js ON c.jugador_sale  = js.id_jugador " +
                     "JOIN jugadores je ON c.jugador_entra = je.id_jugador " +
                     "WHERE c.id_partido = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cambios cambio = new Cambios();
                    cambio.setIdCambios(rs.getInt("id_cambio"));
                    cambio.setMinuto(rs.getInt("minuto"));

                    Jugador sale = new Jugador();
                    sale.setIdJugador(rs.getInt("jugador_sale"));
                    sale.setNombre(rs.getString("sale_nombre"));
                    cambio.setJugadorSale(sale);

                    Jugador entra = new Jugador();
                    entra.setIdJugador(rs.getInt("jugador_entra"));
                    entra.setNombre(rs.getString("entra_nombre"));
                    cambio.setJugadorEntra(entra);

                    lista.add(cambio);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar cambios por partido: " + e.getMessage());
        }
        return lista;
    }
}
