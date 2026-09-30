package com.academia.empleados.controller;

import com.academia.empleados.dto.EmpleadoRequest;
import com.academia.empleados.dto.EmpleadoResponse;
import com.academia.empleados.dto.EstadisticaDepartamento;
import com.academia.empleados.dto.PaginaResponse;
import com.academia.empleados.service.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@Tag(name = "Empleados", description = "Alta, consulta, modificación y baja de empleados")
public class EmpleadoController {

    private final EmpleadoService service;

    public EmpleadoController(EmpleadoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar empleados, por páginas",
            description = "page empieza en 0. sort: campo,dirección — ej. salario,desc o apellidos,asc")
    public PaginaResponse<EmpleadoResponse> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar con filtros opcionales, por páginas",
            description = "Todos los filtros son opcionales y se combinan (Y). texto busca en nombre y apellidos; "
                    + "ciudad busca en la dirección; habilidad, en la lista de habilidades. Sin importar acentos ni mayúsculas")
    @ApiResponse(responseCode = "200", description = "Página de resultados (puede venir vacía)")
    @ApiResponse(responseCode = "400", description = "Parámetro inválido (ej. ordenar por un campo que no existe)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public PaginaResponse<EmpleadoResponse> buscar(
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) BigDecimal salarioMinimo,
            @RequestParam(required = false) BigDecimal salarioMaximo,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String habilidad,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.buscar(departamento, texto, activo, salarioMinimo, salarioMaximo, ciudad, habilidad, pageable);
    }

    @GetMapping("/estadisticas/departamentos")
    @Operation(summary = "Por departamento: empleados, activos y salario promedio, mínimo y máximo",
            description = "Una agregación de MongoDB ($group), el equivalente a un GROUP BY de SQL")
    public List<EstadisticaDepartamento> estadisticasPorDepartamento() {
        return service.estadisticasPorDepartamento();
    }

    @GetMapping("/departamento/{departamento}")
    @Operation(summary = "Empleados de un departamento, ordenados por apellidos")
    public List<EmpleadoResponse> porDepartamento(@PathVariable String departamento) {
        return service.porDepartamento(departamento);
    }

    @GetMapping("/salarios")
    @Operation(summary = "Empleados con salario entre minimo y maximo, del mayor al menor")
    public List<EmpleadoResponse> porRangoDeSalario(@RequestParam BigDecimal minimo,
                                                    @RequestParam BigDecimal maximo) {
        return service.porRangoDeSalario(minimo, maximo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un empleado por su id")
    @ApiResponse(responseCode = "200", description = "Empleado encontrado")
    @ApiResponse(responseCode = "404", description = "No existe un empleado con ese id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public EmpleadoResponse buscar(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear un empleado")
    @ApiResponse(responseCode = "201", description = "Empleado creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "El email ya existe",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<EmpleadoResponse> crear(@Valid @RequestBody EmpleadoRequest datos) {
        EmpleadoResponse creado = service.crear(datos);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un empleado")
    @ApiResponse(responseCode = "200", description = "Empleado modificado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No existe un empleado con ese id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "El email ya lo tiene otro empleado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public EmpleadoResponse actualizar(@PathVariable String id, @Valid @RequestBody EmpleadoRequest datos) {
        return service.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un empleado")
    @ApiResponse(responseCode = "204", description = "Empleado eliminado")
    @ApiResponse(responseCode = "404", description = "No existe un empleado con ese id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public void eliminar(@PathVariable String id) {
        service.eliminar(id);
    }
}