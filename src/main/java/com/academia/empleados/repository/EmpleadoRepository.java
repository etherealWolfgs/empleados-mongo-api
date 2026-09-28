package com.academia.empleados.repository;

import com.academia.empleados.entity.Empleado;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.List;

// MongoRepository en lugar de JpaRepository: mismos métodos heredados (findAll, findById, save, delete...)
// El id ahora es String. Además "hereda" buscar(...) de EmpleadoBusqueda (lo implementa EmpleadoBusquedaImpl)
public interface EmpleadoRepository extends MongoRepository<Empleado, String>, EmpleadoBusqueda {

    // Consultas DERIVADAS: iguales que con MySQL; Spring Data arma la consulta de Mongo a partir del nombre
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, String id);

    // { departamento: /^...$/i }, ordenado por apellidos
    List<Empleado> findByDepartamentoIgnoreCaseOrderByApellidosAsc(String departamento);

    // { salario: { $gte: minimo, $lte: maximo } }, del mayor al menor
    List<Empleado> findBySalarioBetweenOrderBySalarioDesc(BigDecimal minimo, BigDecimal maximo);
}