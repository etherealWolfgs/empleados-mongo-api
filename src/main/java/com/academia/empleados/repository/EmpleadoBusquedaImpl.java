package com.academia.empleados.repository;

import com.academia.empleados.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// El nombre IMPORTA: Spring Data busca la clase "<interfaz>Impl" y la conecta con EmpleadoRepository
class EmpleadoBusquedaImpl implements EmpleadoBusqueda {

    private final MongoTemplate mongo;

    EmpleadoBusquedaImpl(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public Page<Empleado> buscar(String departamento, String texto, Boolean activo,
                                 BigDecimal salarioMinimo, BigDecimal salarioMaximo, Pageable pageable) {
        // Cada filtro que llega (no null) agrega una condición; los que no llegan, no filtran
        List<Criteria> filtros = new ArrayList<>();
        if (departamento != null) {
            // ^...$ = el valor completo; "i" = sin importar mayúsculas
            filtros.add(Criteria.where("departamento").regex("^" + Pattern.quote(departamento) + "$", "i"));
        }
        if (texto != null) {
            // el texto puede estar en el nombre O en los apellidos
            filtros.add(new Criteria().orOperator(
                    Criteria.where("nombre").regex(Pattern.quote(texto), "i"),
                    Criteria.where("apellidos").regex(Pattern.quote(texto), "i")));
        }
        if (activo != null) {
            filtros.add(Criteria.where("activo").is(activo));
        }
        if (salarioMinimo != null || salarioMaximo != null) {
            Criteria salario = Criteria.where("salario");
            if (salarioMinimo != null) salario = salario.gte(salarioMinimo);
            if (salarioMaximo != null) salario = salario.lte(salarioMaximo);
            filtros.add(salario);
        }
        Query consulta = filtros.isEmpty() ? new Query() : new Query(new Criteria().andOperator(filtros));

        long total = mongo.count(consulta, Empleado.class);                    // cuántos cumplen (para la página)
        List<Empleado> pagina = mongo.find(consulta.with(pageable), Empleado.class);  // solo los de esta página
        return new PageImpl<>(pagina, pageable, total);
    }
}
