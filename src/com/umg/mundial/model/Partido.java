package com.umg.mundial.model;

import java.util.Date;

public class Partido {
    private int id_partido;
    private String fase;
    private String grupo;
    private Equipo id_equipo_local;     // Objeto completo
    private Equipo id_equipo_visitante; // Objeto completo
    private int goles_locales;
    private int goles_visitantes;
    private Integer penales_locales;   // Usamos Integer por si es null
    private Integer penales_visitantes;
    private Date fecha;
    private Estadio id_estadio;        // Objeto completo

    public Partido() {}

    // --- Getters y Setters ---
    public int getId_Partido() { return id_partido; }
    public void setId_Partido(int id_partido) { this.id_partido = id_partido; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public Equipo getId_Equipo_Local() { return id_equipo_local; }
    public void setId_Equipo_Local(Equipo id_equipo_local) { this.id_equipo_local = id_equipo_local; }

    public Equipo getId_Equipo_Visitante() { return id_equipo_visitante; }
    public void setId_Equipo_Visitante(Equipo id_equipo_visitante) { this.id_equipo_visitante = id_equipo_visitante; }

    public int getGoles_Locales() { return goles_locales; }
    public void setGoles_Locales(int goles_locales) { this.goles_locales = goles_locales; }

    public int getGoles_Visitantes() { return goles_visitantes; }
    public void setGoles_Visitantes(int goles_visitantes) { this.goles_visitantes = goles_visitantes; }

    public Integer getPenales_Locales() { return penales_locales; }
    public void setPenales_Locales(Integer penales_locales) { this.penales_locales = penales_locales; }

    public Integer getPenales_Visitantes() { return penales_visitantes; }
    public void setPenales_Visitantes(Integer penales_visitantes) { this.penales_visitantes = penales_visitantes; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Estadio getId_Estadio() { return id_estadio; }
    public void setId_Estadio(Estadio id_estadio) { this.id_estadio = id_estadio; }
    
}
