package com.academia.empleados.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Un documento de la colección "empleados" (en MySQL era una fila de la tabla "empleados")
@Document("empleados")
public class Empleado {

    // Mongo genera el _id: un ObjectId de 24 caracteres hexadecimales, no un número consecutivo
    @Id
    private String id;

    private String nombre;

    private String apellidos;

    // Índice único: Mongo rechaza un segundo documento con el mismo email
    @Indexed(unique = true)
    private String email;

    private String puesto;

    private String departamento;

    // Se guarda como Decimal128: decimal exacto, igual que el DECIMAL(10,2) de MySQL
    private BigDecimal salario;

    // Mongo no tiene "solo fecha": se guarda como fecha con hora (la medianoche de tu zona, en UTC)
    private LocalDate fechaIngreso;

    private boolean activo = true;

    // Documento embebido: { calle, ciudad, estado, codigoPostal } dentro del empleado (puede faltar)
    private Direccion direccion;

    // Una lista dentro del documento: ["Java", "Docker"]. En MySQL sería otra tabla y un JOIN
    private List<String> habilidades = new ArrayList<>();

    // Spring Data necesita un constructor sin argumentos para leer los documentos
    protected Empleado() {
    }

    public Empleado(String nombre, String apellidos, String email, String puesto,
                    String departamento, BigDecimal salario, LocalDate fechaIngreso) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.puesto = puesto;
        this.departamento = departamento;
        this.salario = salario;
        this.fechaIngreso = fechaIngreso;
    }

    public String getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public List<String> getHabilidades() { return habilidades; }
    public void setHabilidades(List<String> habilidades) { this.habilidades = habilidades; }
}