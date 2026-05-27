package com.umg.mundial.dao;

import com.umg.mundial.model.Partido;
import com.umg.mundial.model.Equipo;
import com.umg.mundial.model.Estadio;
import com.umg.mundial.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartidoDAO {

    public List<Partido> listar() {
        List<Partido> lista = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "el.pais AS local_nombre, ev.pais AS visitante_nombre, " +
                     "es.nombre_estadios AS estadio_nombre " +
                     "FROM partidos p " +
                     "JOIN equipos el ON p.id_equipo_local    = el.id_equipo " +
                     "JOIN equipos ev ON p.id_equipo_visitante = ev.id_equipo " +
                     "JOIN estadios es ON p.id_estadio         = es.id_estadio";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Partido p = new Partido();
                p.setId_Partido(rs.getInt("id_partido"));
                p.setFase(rs.getString("fase"));
                p.setGrupo(rs.getString("grupo"));
                p.setGoles_Locales(rs.getInt("goles_locales"));
                p.setGoles_Visitantes(rs.getInt("goles_visitantes"));
                p.setFecha(rs.getTimestamp("fecha"));

                // Penales (pueden ser null)
                Object penL = rs.getObject("penales_locales");
                Object penV = rs.getObject("penales_visitantes");
                p.setPenales_Locales(penL != null ? (Integer) penL : null);
                p.setPenales_Visitantes(penV != null ? (Integer) penV : null);

                Equipo local = new Equipo();
                local.setIdequipo(rs.getInt("id_equipo_local"));
                local.setPais(rs.getString("local_nombre"));
                p.setId_Equipo_Local(local);

                Equipo visitante = new Equipo();
                visitante.setIdequipo(rs.getInt("id_equipo_visitante"));
                visitante.setPais(rs.getString("visitante_nombre"));
                p.setId_Equipo_Visitante(visitante);

                Estadio est = new Estadio();
                est.setIdEstadio(rs.getInt("id_estadio"));
                est.setNombre_Estadios(rs.getString("estadio_nombre")); // ← CORREGIDO
                p.setId_Estadio(est);

                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar partidos: " + e.getMessage());
        }
        return lista;
    }

    // ── Insertar partido simulado en la BD — devuelve el id generado ──────────
    public int insertar(Partido p) {
        String sql = "INSERT INTO partidos (fase, grupo, id_equipo_local, id_equipo_visitante, " +
                     "goles_locales, goles_visitantes, penales_locales, penales_visitantes, " +
                     "fecha, id_estadio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getFase());
            ps.setString(2, p.getGrupo() != null ? p.getGrupo() : "");
            ps.setInt(3, p.getId_Equipo_Local().getIdequipo());
            ps.setInt(4, p.getId_Equipo_Visitante().getIdequipo());
            ps.setInt(5, p.getGoles_Locales());
            ps.setInt(6, p.getGoles_Visitantes());

            if (p.getPenales_Locales() != null)
                ps.setInt(7, p.getPenales_Locales());
            else
                ps.setNull(7, Types.INTEGER);

            if (p.getPenales_Visitantes() != null)
                ps.setInt(8, p.getPenales_Visitantes());
            else
                ps.setNull(8, Types.INTEGER);

            ps.setInt(9, p.getId_Estadio() != null ? p.getId_Estadio().getIdEstadio() : 1);

            ps.executeUpdate();

            // Retornar el id_partido generado por PostgreSQL
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    System.out.println("Partido guardado ID=" + idGenerado + " fase=" + p.getFase());
                    return idGenerado;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar partido: " + e.getMessage());
        }
        return 0;
    }
}
