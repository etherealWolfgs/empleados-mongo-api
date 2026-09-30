package com.academia.empleados.dto;

import com.academia.empleados.entity.Empleado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EmpleadoResponse(
        String id,
        String nombre,
        String apellidos,
        String email,
        String puesto,
        String departamento,
        BigDecimal salario,
        LocalDate fechaIngreso,
        boolean activo,
        DireccionDto direccion,
        List<String> habilidades
) {

    // Convierte la entidad (lo que hay en la BD) en lo que la API devuelve
    public static EmpleadoResponse desde(Empleado e) {
        return new EmpleadoResponse(e.getId(), e.getNombre(), e.getApellidos(), e.getEmail(),
                e.getPuesto(), e.getDepartamento(), e.getSalario(), e.getFechaIngreso(), e.isActivo(),
                DireccionDto.desde(e.getDireccion()), e.getHabilidades());
    }
}