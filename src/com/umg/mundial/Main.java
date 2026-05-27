package com.umg.mundial;

import com.umg.mundial.api.Servidor;

public class Main {
    public static void main(String[] args) {
        try {
            Servidor.iniciar();
        } catch (Exception e) {
            System.err.println("Error al iniciar servidor:" + e.getMessage());
        }
    }
}
