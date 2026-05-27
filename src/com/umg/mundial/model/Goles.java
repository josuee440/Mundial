package com.umg.mundial.model;

public class Goles {
    private int id_goles;
    private Partido id_partido;
    private Jugador id_jugador;
    private int minuto;

    public Goles() {}

    public int getId_Goles() { return id_goles; }
    public void setId_Goles(int id_goles) { this.id_goles = id_goles; }

    public Partido getId_Partido() { return id_partido; }
    public void setId_Partido(Partido id_partido) { this.id_partido = id_partido; }

    public Jugador getId_Jugador() { return id_jugador; }
    public void setId_Jugador(Jugador id_jugador) { this.id_jugador = id_jugador; }

    public int getMinuto() { return minuto; }
    public void setMinuto(int minuto) { this.minuto = minuto; }
}
