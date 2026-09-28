package com.academia.empleados.exception;

public class EmpleadoNoEncontradoException extends RuntimeException {

    public EmpleadoNoEncontradoException(String id) {
        super("No existe un empleado con id " + id);
    }
}