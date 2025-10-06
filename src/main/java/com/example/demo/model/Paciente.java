package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paciente_seq")
    @SequenceGenerator(name = "paciente_seq", sequenceName = "paciente_seq", allocationSize = 1)
    private Long id;
    
    @NotBlank(message = "El RUT es obligatorio")
    @Column(name = "rut", unique = true, nullable = false, length = 12)
    private String rut;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 130, message = "La edad máxima es 130 años")
    @Column(name = "edad")
    private Integer edad;
    
    @Pattern(regexp = "M|F|O", message = "El género debe ser M, F u O")
    @Column(name = "genero", length = 1)
    private String genero;  
    
    @Column(name = "telefono")
    private String telefono;
    
    @Email(message = "El email debe ser válido")
    @Column(name = "correo")
    private String correo;
    
    @Size(max = 200, message = "La dirección no puede exceder los 200 caracteres")
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    public Paciente() {}
    
    public Paciente(String rut, String nombre, Integer edad, String genero, String telefono, 
                   String correo, String direccion) {
        this.rut = rut;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}