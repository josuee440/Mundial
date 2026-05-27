package com.umg.mundial.model;

public class Tarjeta {
    private int id_Tarjeta;
    private Partido id_partido;
    private Jugador id_jugador;
    private String tipo; // Amarilla o Roja
    private int minuto;

    public Tarjeta() {}

    public int getId_Tarjeta() { return id_Tarjeta; }
    public void setId_Tarjeta(int id_Tarjeta) { this.id_Tarjeta = id_Tarjeta; }

    public Partido getId_Partido() { return id_partido; }
    public void setId_Partido(Partido id_partido) { this.id_partido = id_partido; }

    public Jugador getId_Jugador() { return id_jugador; }
    public void setId_Jugador(Jugador id_jugador) { this.id_jugador = id_jugador; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getMinuto() { return minuto; }
    public void setMinuto(int minuto) { this.minuto = minuto; }
}
