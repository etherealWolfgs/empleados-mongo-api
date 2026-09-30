package com.academia.empleados.entity;

// Un SUBDOCUMENTO: vive DENTRO del documento del empleado (en MySQL sería otra tabla con su llave foránea)
public record Direccion(String calle, String ciudad, String estado, String codigoPostal) {
}