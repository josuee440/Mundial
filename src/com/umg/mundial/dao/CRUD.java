package com.umg.mundial.dao;

import java.util.List;

// Aplicando Abstracción y Polimorfismo con una interfaz genérica
public interface CRUD<T> {
    List<T> listar();
    boolean insertar(T objeto);
    boolean actualizar(T objeto);
    boolean eliminar(int id);
}