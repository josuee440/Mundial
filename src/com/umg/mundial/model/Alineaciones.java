package com.umg.mundial.model;

public class Alineaciones {
    private int id_Alineacion;
    private Partido id_partido;
    private Jugador id_jugador;
    private boolean titular;

    public Alineaciones() {}

    public int getIdAlineacion() { return id_Alineacion; }
    public void setIdAlineacion(int id_Alineacion) { this.id_Alineacion = id_Alineacion; }

    public Partido getPartido() { return id_partido; }
    public void setPartido(Partido id_partido) { this.id_partido = id_partido; }

    public Jugador getJugador() { return id_jugador; }
    public void setJugador(Jugador id_jugador) { this.id_jugador = id_jugador; }

    public boolean isTitular() { return titular; }
    public void setTitular(boolean titular) { this.titular = titular; }
}
