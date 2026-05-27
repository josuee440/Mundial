package com.umg.mundial.model;

public class Estadio {
    private int idEstadio;
    private String nombre_estadios;
    private String ciudad;
    private String pais;
    private int capacidad;

    public Estadio() {}

    // Getters y Setters
    public int getIdEstadio() { return idEstadio; }
    public void setIdEstadio(int idEstadio) { this.idEstadio = idEstadio; }

    public String getNombre_Estadios() { return nombre_estadios; }
    public void setNombre_Estadios(String nombre_estadios) { this.nombre_estadios = nombre_estadios; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
}
