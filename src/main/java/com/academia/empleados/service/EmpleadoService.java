package com.academia.empleados.service;

import com.academia.empleados.dto.EmpleadoRequest;
import com.academia.empleados.dto.EmpleadoResponse;
import com.academia.empleados.dto.EstadisticaDepartamento;
import com.academia.empleados.dto.PaginaResponse;
import com.academia.empleados.entity.Empleado;
import com.academia.empleados.exception.EmailDuplicadoException;
import com.academia.empleados.exception.EmpleadoNoEncontradoException;
import com.academia.empleados.repository.EmpleadoRepository;
import org.springframework.data.core.PropertyPath;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

// Sin @Transactional: MongoDB en un solo servidor no tiene transacciones de varios documentos,
// y sin un gestor de transacciones Spring la IGNORARÍA sin avisar. Cada save o delete de UN documento es atómico.
@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    // Inyección por constructor: Spring pasa el repositorio al crear el servicio
    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    // UNA página: tamaño y orden los decide el cliente
    public PaginaResponse<EmpleadoResponse> listar(Pageable pageable) {
        validarOrden(pageable);   // con MySQL, findAll ya lo rechazaba; Mongo ordena por un campo inexistente sin quejarse
        return PaginaResponse.desde(repository.findAll(pageable), EmpleadoResponse::desde);
    }

    public PaginaResponse<EmpleadoResponse> buscar(String departamento, String texto, Boolean activo,
                                                   BigDecimal salarioMinimo, BigDecimal salarioMaximo,
                                                   String ciudad, String habilidad, Pageable pageable) {
        validarOrden(pageable);
        return PaginaResponse.desde(
                repository.buscar(departamento, texto, activo, salarioMinimo, salarioMaximo, ciudad, habilidad, pageable),
                EmpleadoResponse::desde);
    }

    public List<EmpleadoResponse> porDepartamento(String departamento) {
        return repository.findByDepartamentoIgnoreCaseOrderByApellidosAsc(departamento).stream()
                .map(EmpleadoResponse::desde)
                .toList();
    }

    public List<EmpleadoResponse> porRangoDeSalario(BigDecimal minimo, BigDecimal maximo) {
        return repository.findBySalarioBetweenOrderBySalarioDesc(minimo, maximo).stream()
                .map(EmpleadoResponse::desde)
                .toList();
    }

    // El promedio sale con muchos decimales (25333.333333...): se redondea a centavos
    public List<EstadisticaDepartamento> estadisticasPorDepartamento() {
        return repository.estadisticasPorDepartamento().stream()
                .map(e -> new EstadisticaDepartamento(e.departamento(), e.empleados(), e.activos(),
                        e.salarioPromedio().setScale(2, RoundingMode.HALF_UP), e.salarioMinimo(), e.salarioMaximo()))
                .toList();
    }

    public EmpleadoResponse buscarPorId(String id) {
        return EmpleadoResponse.desde(obtener(id));
    }

    public EmpleadoResponse crear(EmpleadoRequest datos) {
        if (repository.existsByEmail(datos.email())) {
            throw new EmailDuplicadoException(datos.email());
        }
        Empleado empleado = new Empleado(datos.nombre(), datos.apellidos(), datos.email(), datos.puesto(),
                datos.departamento(), datos.salario(), datos.fechaIngreso());
        empleado.setActivo(datos.activo() == null || datos.activo());
        copiarDireccionYHabilidades(datos, empleado);
        return EmpleadoResponse.desde(repository.save(empleado));
    }

    public EmpleadoResponse actualizar(String id, EmpleadoRequest datos) {
        Empleado empleado = obtener(id);
        if (repository.existsByEmailAndIdNot(datos.email(), id)) {
            throw new EmailDuplicadoException(datos.email());
        }
        empleado.setNombre(datos.nombre());
        empleado.setApellidos(datos.apellidos());
        empleado.setEmail(datos.email());
        empleado.setPuesto(datos.puesto());
        empleado.setDepartamento(datos.departamento());
        empleado.setSalario(datos.salario());
        empleado.setFechaIngreso(datos.fechaIngreso());
        empleado.setActivo(datos.activo() == null || datos.activo());
        copiarDireccionYHabilidades(datos, empleado);
        return EmpleadoResponse.desde(repository.save(empleado));
    }

    public void eliminar(String id) {
        repository.delete(obtener(id));
    }

    // sort=campoQueNoExiste: PropertyPath.from lo detecta y lanza PropertyReferenceException → el manejador responde 400.
    private void validarOrden(Pageable pageable) {
        pageable.getSort().forEach(orden -> PropertyPath.from(orden.getProperty(), Empleado.class));
    }

    // PUT reemplaza el empleado completo: si no llega dirección o habilidades, se quedan vacías
    private void copiarDireccionYHabilidades(EmpleadoRequest datos, Empleado empleado) {
        empleado.setDireccion(datos.direccion() == null ? null : datos.direccion().aEntidad());
        empleado.setHabilidades(datos.habilidades() == null ? new ArrayList<>() : new ArrayList<>(datos.habilidades()));
    }

    // Busca el empleado o lanza la excepción que el manejador convierte en 404
    private Empleado obtener(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmpleadoNoEncontradoException(id));
    }
}