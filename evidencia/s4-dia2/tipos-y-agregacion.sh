#!/usr/bin/env bash
# Evidencia del martes, directo en Mongo: de qué TIPO quedó cada campo tras la migración de datos,
# y la misma agregación que hace la API, escrita en mongosh.
# Uso:  bash evidencia/s4-dia2/tipos-y-agregacion.sh > evidencia/s4-dia2/tipos-y-agregacion.txt
M() { docker exec empleados-mongo mongosh --quiet -u academia -p academia123 --authenticationDatabase admin empleados_db --eval "$1"; }

echo "Evidencia tipos y agregación · $(date '+%Y-%m-%d %H:%M') · $(git config user.name)"
echo; echo "### Tipo de salario y fechaIngreso en cada documento (esperado: decimal y date en todos)"
M 'db.empleados.aggregate([{ $group: { _id: { salario: { $type: "$salario" }, fechaIngreso: { $type: "$fechaIngreso" } }, documentos: { $sum: 1 } } }]).forEach(r => print(JSON.stringify(r)))'
echo; echo "### Un documento de la semilla, completo"
M 'printjson(db.empleados.findOne({ email: "oscar.rosales@academia.mx" }))'
echo; echo "### La agregación en mongosh: empleados y salario promedio por departamento"
M 'db.empleados.aggregate([
  { $group: { _id: "$departamento", empleados: { $sum: 1 }, promedio: { $avg: "$salario" } } },
  { $sort: { _id: 1 } }
]).forEach(r => print(r._id, "·", r.empleados, "empleados · promedio", Number(r.promedio).toFixed(2)))'