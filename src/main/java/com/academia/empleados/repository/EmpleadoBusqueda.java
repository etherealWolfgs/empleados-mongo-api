package com.academia.empleados.repository;

import com.academia.empleados.dto.EstadisticaDepartamento;
import com.academia.empleados.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

// Lo que se escribe a mano (EmpleadoBusquedaImpl) y EmpleadoRepository hereda de esta interfaz
public interface EmpleadoBusqueda {

    Page<Empleado> buscar(String departamento, String texto, Boolean activo,
                          BigDecimal salarioMinimo, BigDecimal salarioMaximo,
                          String ciudad, String habilidad, Pageable pageable);

    List<EstadisticaDepartamento> estadisticasPorDepartamento();
}