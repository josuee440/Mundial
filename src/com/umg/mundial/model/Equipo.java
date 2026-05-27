package com.umg.mundial.model;

public class Equipo {
private int idequipo;
    private String pais;
    private String grupo;
    private Integer idGrupo; // Usamos Integer con "I" mayúscula por si es null en la BD

    public Equipo() {}

    public Equipo(int idEquipo, String pais, String grupo, Integer idGrupo) {
        this.idequipo = idEquipo;
        this.pais = pais;
        this.grupo = grupo;
        this.idGrupo = idGrupo;
    }

    // Getters y Setters
    public int getIdequipo() { return idequipo; }
    public void setIdequipo(int idequipo) { this.idequipo = idequipo; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public Integer getIdGrupo() { return idGrupo; }
    public void setIdGrupo(Integer idGrupo) { this.idGrupo = idGrupo; }
}
