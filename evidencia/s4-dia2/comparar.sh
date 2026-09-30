#!/usr/bin/env bash
# Compara, paso por paso, dos salidas de probar-busquedas.sh: la de MySQL (Semana 3) y la de Mongo.
# Solo cuenta a los 30 empleados de la semilla: los que creaste a mano en MySQL no se migraron.
# Uso:  bash evidencia/s4-dia2/comparar.sh evidencia/dia3/busquedas.txt evidencia/s4-dia2/busquedas.txt
python3 - "$1" "$2" <<'PY'
import json, re, sys

semilla = set()
for linea in open("datos/semilla-empleados.json", encoding="utf-8"):
    d = json.loads(linea)
    semilla.add(d["nombre"] + " " + d["apellidos"])

def leer(ruta):
    pasos, paso = {}, None
    for l in open(ruta, encoding="utf-8"):
        m = re.match(r"### (\d+)\. (.*)", l)
        if m:
            paso = int(m.group(1)); pasos[paso] = {"titulo": m.group(2).strip(), "http": "?", "nombres": []}
        elif paso and l.startswith("HTTP "):
            pasos[paso]["http"] = l.split()[1]
        elif paso:
            m = re.match(r"\s+\S+ (.+?) \|", l)            # "   <id> Nombre Apellidos | Depto | ..."
            if m and m.group(1) in semilla:
                pasos[paso]["nombres"].append(m.group(1))
    return pasos

def comparar(x, y):
    if x["http"] != y["http"]:
        return "DIFERENTE"
    if x["nombres"] == y["nombres"]:
        return "igual"
    n = min(len(x["nombres"]), len(y["nombres"]))
    if n > 0 and x["nombres"][:n] == y["nombres"][:n]:
        return "igual (*)"            # misma lista; la página traía además empleados creados a mano
    return "DIFERENTE"

a, b = leer(sys.argv[1]), leer(sys.argv[2])
print("paso  MySQL      Mongo      resultado")
distintos = 0
for p in sorted(a):
    x, y = a[p], b.get(p, {"http": "-", "nombres": []})
    r = comparar(x, y)
    distintos += r == "DIFERENTE"
    print("%3d   %s · %2d   %s · %2d   %-10s %s" % (p, x["http"], len(x["nombres"]), y["http"], len(y["nombres"]), r, x["titulo"]))
print("\n%d de %d pasos iguales, contando solo a los 30 de la semilla." % (len(a) - distintos, len(a)))
print("(*) Igual: la diferencia son empleados que creaste a mano (en MySQL o en Mongo) y que no están en la semilla.")
PY