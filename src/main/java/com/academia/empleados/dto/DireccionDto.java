package com.academia.empleados.dto;

import com.academia.empleados.entity.Direccion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dirección del empleado (se guarda DENTRO de su documento)")
public record DireccionDto(

        @Schema(example = "Calle Fresno 10")
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 100, message = "La calle admite máximo 100 caracteres")
        String calle,

        @Schema(example = "Querétaro")
        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 60, message = "La ciudad admite máximo 60 caracteres")
        String ciudad,

        @Schema(example = "Querétaro")
        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 60, message = "El estado admite máximo 60 caracteres")
        String estado,

        @Schema(example = "76100")
        @Pattern(regexp = "\\d{5}", message = "El código postal son 5 dígitos")
        String codigoPostal
) {

    public Direccion aEntidad() {
        return new Direccion(calle, ciudad, estado, codigoPostal);
    }

    public static DireccionDto desde(Direccion d) {
        return d == null ? null : new DireccionDto(d.calle(), d.ciudad(), d.estado(), d.codigoPostal());
    }
}