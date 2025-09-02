package com.example.demo;

public class Medico {
    private int id;
    private String rut;
    private String nombre;
    private int edad;
    private String genero;
    private int telefono;
    private String correo;
    private String direccion;
    private String especialidad;

    public Medico(int id, String rut, String nombre, int edad, String genero, int telefono, String correo,
            String direccion, String especialidad) {
        this.id = id;
        this.rut = rut;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.especialidad = especialidad;

    }

    public int getId() {
        return id;
    }

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getGenero() {
        return genero;
    }

    public int getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getEspecialidad() {
        return especialidad;
    }
}