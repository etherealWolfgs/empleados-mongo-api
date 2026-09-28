#!/usr/bin/env bash
# Evidencia del Día 2: ejercita el CRUD y sus errores contra TU API (debe estar corriendo en :8080).
# Crea dos empleados de prueba con emails únicos, los usa y los borra: tus datos no se tocan.
# Uso:  bash evidencia/dia2/probar-crud.sh > evidencia/dia2/crud.txt
API=http://localhost:8080/api/empleados
H='Content-Type: application/json'
SUFIJO=$(date +%s)
A="prueba.a.$SUFIJO@empresa.com"
B="prueba.b.$SUFIJO@empresa.com"
cuerpo() { echo "{\"nombre\":\"$1\",\"apellidos\":\"Evidencia\",\"email\":\"$2\",\"puesto\":\"$3\",\"departamento\":\"Pruebas\",\"salario\":$4,\"fechaIngreso\":\"2024-01-15\"}"; }
paso() { echo; echo "### $1"; shift; curl -s -i "$@" | tr -d '\r' | tee /tmp/ultima-respuesta.txt | grep -viE '^(date|keep-alive|connection|transfer-encoding|vary|content-length):'; echo; }
id_creado() { grep -i '^location:' /tmp/ultima-respuesta.txt | grep -o '[^/]*$'; }   # el id sale de la cabecera Location

echo "Evidencia CRUD · $(date '+%Y-%m-%d %H:%M') · $(git config user.name)"
paso "1. POST crea A → 201 + Location" -X POST $API -H "$H" -d "$(cuerpo PruebaA $A Tester 20000.00)"
ID_A=$(id_creado)
paso "2. POST crea B → 201" -X POST $API -H "$H" -d "$(cuerpo PruebaB $B Tester 21000.00)"
ID_B=$(id_creado)
paso "3. GET por id (A=$ID_A) → 200" $API/$ID_A
paso "4. PUT B=$ID_B cambia puesto y salario → 200" -X PUT $API/$ID_B -H "$H" -d "$(cuerpo PruebaB $B Lider 25000.00)"
paso "5. POST con el email de A → 409" -X POST $API -H "$H" -d "$(cuerpo Otra $A Tester 1)"
paso "6. POST con datos inválidos → 400" -X POST $API -H "$H" -d '{"nombre":"","apellidos":"X","email":"no-es-email","puesto":"X","departamento":"X","salario":-5,"fechaIngreso":"2030-01-01"}'
paso "7. POST con fecha 15/03/2024 → 400 JSON ilegible" -X POST $API -H "$H" -d '{"nombre":"X","apellidos":"X","email":"x@x.com","puesto":"X","departamento":"X","salario":1,"fechaIngreso":"15/03/2024"}'
paso "8. GET id inexistente 999999 → 404" $API/999999
paso "9. DELETE A → 204" -X DELETE $API/$ID_A
paso "10. DELETE A otra vez → 404" -X DELETE $API/$ID_A
paso "11. DELETE B → 204" -X DELETE $API/$ID_B
paso "12. GET lista → 200" $API