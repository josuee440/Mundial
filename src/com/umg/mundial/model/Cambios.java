package com.umg.mundial.model;

public class Cambios {
    private int id_Cambios;
    private Partido id_partido;
    private Jugador jugador_sale;
    private Jugador jugador_entra;
    private int minuto;

    public Cambios() {}

    public int getIdCambios() { return id_Cambios; }
    public void setIdCambios(int id_Cambios) { this.id_Cambios = id_Cambios; }

    public Partido getPartido() { return id_partido; }
    public void setPartido(Partido id_partido) { this.id_partido = id_partido; }

    public Jugador getJugadorSale() { return jugador_sale; }
    public void setJugadorSale(Jugador jugador_sale) { this.jugador_sale = jugador_sale; }

    public Jugador getJugadorEntra() { return jugador_entra; }
    public void setJugadorEntra(Jugador jugador_entra) { this.jugador_entra = jugador_entra; }

    public int getMinuto() { return minuto; }
    public void setMinuto(int minuto) { this.minuto = minuto; }
}
