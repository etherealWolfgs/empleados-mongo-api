#!/usr/bin/env bash
# Evidencia del lunes (Semana 4): Mongo arriba, el índice único, cómo quedó guardado un documento
# y qué archivos cambiaron respecto a la versión MySQL (la etiqueta v1-mysql).
# Uso:  bash evidencia/s4-dia1/evidencia.sh > evidencia/s4-dia1/mongo.txt
M() { docker exec empleados-mongo mongosh --quiet -u academia -p academia123 --authenticationDatabase admin empleados_db --eval "$1"; }

echo "Evidencia Semana 4 · lunes · $(date '+%Y-%m-%d %H:%M') · $(git config user.name)"
echo; echo "### docker compose ps"
docker compose ps --format '{{.Name}}  {{.Image}}  {{.Status}}'
echo; echo "### Índices de la colección empleados"
M 'db.empleados.getIndexes().forEach(i => print(i.name, JSON.stringify(i.key), i.unique ? "UNICO" : ""))'
echo; echo "### Un documento tal como quedó en Mongo"
M 'printjson(db.empleados.findOne({}, {_id: 1, nombre: 1, email: 1, salario: 1, fechaIngreso: 1, _class: 1}))'
echo; echo "### Documentos en la colección"
M 'print(db.empleados.countDocuments())'
echo; echo "### Qué cambió respecto a la versión MySQL (git diff v1-mysql HEAD --stat)"
git diff v1-mysql HEAD --stat