package com.umg.mundial.model;

public class Entrenador {
    private int id_entrenador;
    private String nombre_entrenador;
    private Equipo id_equipo; // Objeto para la relación

    public Entrenador() {}

    // Getters y Setters
    public int getIdEntrenador() { return id_entrenador; }
    public void setIdEntrenador(int idEntrenador) { this.id_entrenador = idEntrenador; }

    public String getNombreEntrenador() { return nombre_entrenador; }
    public void setNombreEntrenador(String nombreEntrenador) { this.nombre_entrenador = nombreEntrenador; }

    public Equipo getEquipo() { return id_equipo; }
    public void setEquipo(Equipo equipo) { this.id_equipo = equipo; }
}
