package com.example.demo;

public class Paciente {
    private int id;
    private String rut;
    private String nombre;
    private int edad;
    private String genero;
    private int telefono;
    private String correo;
    private String direccion;

    public Paciente(int id, String rut, String nombre, int edad, String genero, int telefono, String correo,
            String direccion) {
        this.id = id;
        this.rut = rut;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;

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
}