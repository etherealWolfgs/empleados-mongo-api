package com.academia.empleados.dto;

import java.math.BigDecimal;

// Una fila del resultado de la agregación: lo que en SQL sería un GROUP BY departamento
public record EstadisticaDepartamento(
        String departamento,
        int empleados,
        int activos,
        BigDecimal salarioPromedio,
        BigDecimal salarioMinimo,
        BigDecimal salarioMaximo
) {
}
