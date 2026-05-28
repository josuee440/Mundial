package com.umg.mundial.model;

// Aplicando Herencia: Clase Padre (Superclase abstracta)
public abstract class Persona {
    protected String nombre;

    public Persona() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}