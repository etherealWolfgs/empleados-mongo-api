package com.academia.empleados.repository;

import com.academia.empleados.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

// La búsqueda con filtros opcionales. En MySQL era una @Query JPQL; en Mongo no hay JPQL,
// así que se escribe a mano (EmpleadoBusquedaImpl) y EmpleadoRepository la hereda de esta interfaz.
public interface EmpleadoBusqueda {

    Page<Empleado> buscar(String departamento, String texto, Boolean activo,
                          BigDecimal salarioMinimo, BigDecimal salarioMaximo, Pageable pageable);
}