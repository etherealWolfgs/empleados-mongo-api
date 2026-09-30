package com.academia.empleados.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Datos para crear o modificar un empleado")
public record EmpleadoRequest(

        @Schema(example = "Ana")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 60, message = "El nombre admite máximo 60 caracteres")
        String nombre,

        @Schema(example = "López Hernández")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 80, message = "Los apellidos admiten máximo 80 caracteres")
        String apellidos,

        @Schema(example = "ana.lopez@empresa.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 120, message = "El email admite máximo 120 caracteres")
        String email,

        @Schema(example = "Desarrolladora Backend")
        @NotBlank(message = "El puesto es obligatorio")
        @Size(max = 60, message = "El puesto admite máximo 60 caracteres")
        String puesto,

        @Schema(example = "Tecnología")
        @NotBlank(message = "El departamento es obligatorio")
        @Size(max = 60, message = "El departamento admite máximo 60 caracteres")
        String departamento,

        @Schema(example = "35000.00")
        @NotNull(message = "El salario es obligatorio")
        @Positive(message = "El salario debe ser mayor que cero")
        @Digits(integer = 8, fraction = 2, message = "El salario admite 8 enteros y 2 decimales")
        BigDecimal salario,

        @Schema(example = "2024-03-15")
        @NotNull(message = "La fecha de ingreso es obligatoria")
        @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
        LocalDate fechaIngreso,

        @Schema(example = "true", description = "Si no se envía, se toma como true")
        Boolean activo,

        // Opcional. @Valid: también se validan los campos de la dirección
        @Valid
        DireccionDto direccion,

        // Opcional. Cada habilidad: no vacía y de máximo 40 caracteres
        @Schema(example = "[\"Java\", \"MongoDB\"]")
        @Size(max = 10, message = "Máximo 10 habilidades")
        List<@NotBlank(message = "Una habilidad no puede estar vacía") @Size(max = 40, message = "Cada habilidad admite máximo 40 caracteres") String> habilidades
) {
}