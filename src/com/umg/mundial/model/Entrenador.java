package com.umg.mundial.model;

public class Entrenador extends Persona {
    private int id_entrenador;
    private Equipo id_equipo; // Objeto para la relación

    public Entrenador() {}

    // Getters y Setters
    public int getIdEntrenador() { return id_entrenador; }
    public void setIdEntrenador(int idEntrenador) { this.id_entrenador = idEntrenador; }

    public Equipo getEquipo() { return id_equipo; }
    public void setEquipo(Equipo equipo) { this.id_equipo = equipo; }
}
