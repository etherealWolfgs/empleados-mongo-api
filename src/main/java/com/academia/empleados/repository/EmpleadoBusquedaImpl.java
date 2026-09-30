package com.academia.empleados.repository;

import com.academia.empleados.dto.EstadisticaDepartamento;
import com.academia.empleados.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.text.Normalizer;
import java.util.regex.Pattern;

// El nombre IMPORTA: Spring Data busca la clase "<interfaz>Impl" y la conecta con EmpleadoRepository
class EmpleadoBusquedaImpl implements EmpleadoBusqueda {

    private final MongoTemplate mongo;

    EmpleadoBusquedaImpl(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public Page<Empleado> buscar(String departamento, String texto, Boolean activo,
                                 BigDecimal salarioMinimo, BigDecimal salarioMaximo,
                                 String ciudad, String habilidad, Pageable pageable) {
        // Cada filtro que llega (no null) agrega una condición; los que no llegan, no filtran
        List<Criteria> filtros = new ArrayList<>();
        if (departamento != null) {
            // ^...$ = el valor completo; "i" = sin importar mayúsculas; sinAcentos = con o sin acento
            filtros.add(Criteria.where("departamento").regex("^" + sinAcentos(departamento) + "$", "i"));
        }
        if (texto != null) {
            // el texto puede estar en el nombre O en los apellidos
            filtros.add(new Criteria().orOperator(
                    Criteria.where("nombre").regex(sinAcentos(texto), "i"),
                    Criteria.where("apellidos").regex(sinAcentos(texto), "i")));
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
        if (ciudad != null) {
            // "direccion.ciudad": con el punto se entra al subdocumento
            filtros.add(Criteria.where("direccion.ciudad").regex("^" + sinAcentos(ciudad) + "$", "i"));
        }
        if (habilidad != null) {
            // En una lista, la condición se cumple si CUALQUIER elemento coincide
            filtros.add(Criteria.where("habilidades").regex("^" + sinAcentos(habilidad) + "$", "i"));
        }
        Query consulta = filtros.isEmpty() ? new Query() : new Query(new Criteria().andOperator(filtros));

        long total = mongo.count(consulta, Empleado.class);                    // cuántos cumplen (para la página)
        List<Empleado> pagina = mongo.find(consulta.with(pageable), Empleado.class);  // solo los de esta página
        return new PageImpl<>(pagina, pageable, total);
    }

    // Agregación: una "tubería" de etapas por las que pasan los documentos. Aquí, en SQL sería:
    //   SELECT departamento, COUNT(*), SUM(activo), AVG(salario), MIN(salario), MAX(salario)
    //   FROM empleados GROUP BY departamento ORDER BY departamento
    @Override
    public List<EstadisticaDepartamento> estadisticasPorDepartamento() {
        Aggregation agregacion = Aggregation.newAggregation(
                Aggregation.group("departamento")                                    // $group: un grupo por departamento
                        .count().as("empleados")
                        .sum(ConditionalOperators.when(Criteria.where("activo").is(true)).then(1).otherwise(0)).as("activos")
                        .avg("salario").as("salarioPromedio")
                        .min("salario").as("salarioMinimo")
                        .max("salario").as("salarioMaximo"),
                Aggregation.project("empleados", "activos", "salarioPromedio", "salarioMinimo", "salarioMaximo")
                        .and("departamento").previousOperation(),                    // el _id del grupo pasa a llamarse departamento
                Aggregation.sort(Sort.by("departamento")));                          // $sort
        return mongo.aggregate(agregacion, Empleado.class, EstadisticaDepartamento.class).getMappedResults();
    }

    // "oscar" → "[oóòöô]sc[aáàäâ]r": la regex encuentra la palabra con o sin acento, como la collation de MySQL.
    // (La collation de Mongo NO sirve aquí: no se aplica a las expresiones regulares.)
    static String sinAcentos(String texto) {
        String base = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");                  // "Óscar" → "oscar": quita los acentos de lo que llega
        StringBuilder patron = new StringBuilder();
        for (char c : base.toCharArray()) {
            switch (c) {
                case 'a' -> patron.append("[aáàäâ]");
                case 'e' -> patron.append("[eéèëê]");
                case 'i' -> patron.append("[iíìïî]");
                case 'o' -> patron.append("[oóòöô]");
                case 'u' -> patron.append("[uúùüû]");
                case 'n' -> patron.append("[nñ]");
                default -> patron.append(Pattern.quote(String.valueOf(c)));   // cualquier otro, literal
            }
        }
        return patron.toString();
    }
}