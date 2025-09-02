package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PacienteController {

    private List<Paciente> Pacientes = new ArrayList<>();

    public PacienteController() {
        Pacientes.add(new Paciente(1, "12345678-9", "Juan Pérez", 35, "M", 912345678, "juan@email.com", "Calle 123"));
        Pacientes
                .add(new Paciente(2, "98765432-1", "María González", 28, "F", 987654321, "maria@email.com", "Av. 456"));
        Pacientes.add(
                new Paciente(3, "45678912-3", "Carlos López", 22, "M", 945678912, "carlos@email.com", "Pasaje 789"));
        Pacientes.add(new Paciente(4, "32165498-7", "Ana Martínez", 65, "F", 932165498, "ana@email.com", "Plaza 101"));
        Pacientes.add(
                new Paciente(5, "65412398-7", "Pedro Rodríguez", 42, "M", 965412398, "pedro@email.com", "Calle 202"));
        Pacientes.add(new Paciente(6, "78945612-3", "Alex Sánchez", 19, "O", 978945612, "alex@email.com", "Av. 303"));
        Pacientes
                .add(new Paciente(7, "15975346-2", "Laura Torres", 31, "F", 915975346, "laura@email.com", "Calle 404"));
    }

    @GetMapping("/pacientes")
    public List<Paciente> getPacientes() {
        return Pacientes;
    }

    @GetMapping("/pacientes/{id}")
    public Paciente getPacienteById(@PathVariable int id) {
        for (Paciente paciente : Pacientes) {
            if (paciente.getId() == id) {
                return paciente;
            }
        }
        return null;
    }

}