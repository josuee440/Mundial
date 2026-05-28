package com.umg.mundial.model;

public class Jugador {
    private int idJugador;
    private String nombre;
    private String posicion;
    private int dorsal;
    private int idEquipo;
    
    // Este atributo extra nos sirve para guardar el nombre del país
    // al hacer el JOIN con la tabla equipos y mostrarlo en el frontend
    private String paisEquipo; 

    public Jugador() {
    }

    // ── GETTERS Y SETTERS ──

    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getPaisEquipo() {
        return paisEquipo;
    }

    public void setPaisEquipo(String paisEquipo) {
        this.paisEquipo = paisEquipo;
    }
}