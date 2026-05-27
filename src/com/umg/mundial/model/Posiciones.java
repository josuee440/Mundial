package com.umg.mundial.model;

public class Posiciones {
    private int id_Posicion;
    private Equipo equipo; // Objeto Equipo para mostrar el nombre del país
    private int puntos;
    private int partidos_jugados;
    private int partidos_ganados;
    private int partidos_empatados;
    private int partidos_perdidos;
    private int goles_favor;
    private int goles_contra;

    public Posiciones() {}

    // Getters y Setters
    public int getIdPosicion() { return id_Posicion; }
    public void setIdPosicion(int id_Posicion) { this.id_Posicion = id_Posicion; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public int getPartidos_Jugados() { return partidos_jugados; }
    public void setPartidos_Jugados(int partidos_jugados) { this.partidos_jugados = partidos_jugados; }

    public int getPartidosGanados() { return partidos_ganados; }
    public void setPartidosGanados(int partidos_ganados) { this.partidos_ganados = partidos_ganados; }

    public int getPartidos_Empatados() { return partidos_empatados; }
    public void setPartidos_Empatados(int partidos_empatados) { this.partidos_empatados = partidos_empatados; }

    public int getPartidos_Perdidos() { return partidos_perdidos; }
    public void setPartidos_Perdidos(int partidos_perdidos) { this.partidos_perdidos = partidos_perdidos; }

    public int getGoles_Favor() { return goles_favor; }
    public void setGoles_Favor(int goles_favor) { this.goles_favor = goles_favor; }

    public int getGoles_Contra() { return goles_contra; }
    public void setGoles_Contra(int goles_contra) { this.goles_contra = goles_contra; }
}
