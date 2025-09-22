package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    // GET - Obtener todos los pacientes
    @GetMapping
    public ResponseEntity<List<Paciente>> getAllPacientes() {
        try {
            List<Paciente> pacientes = pacienteService.getAllPacientes();
            return ResponseEntity.ok(pacientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Obtener paciente por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPacienteById(@PathVariable Long id) {
        try {
            Optional<Paciente> paciente = pacienteService.getPacienteById(id);
            
            if (paciente.isPresent()) {
                return ResponseEntity.ok(paciente.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el paciente con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar el paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // POST - Crear nuevo paciente
    @PostMapping
    public ResponseEntity<?> createPaciente(@Valid @RequestBody Paciente paciente) {
        try {
            Paciente nuevoPaciente = pacienteService.createPaciente(paciente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaciente);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al crear el paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // PUT - Actualizar paciente existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaciente(@PathVariable Long id, 
                                           @Valid @RequestBody Paciente pacienteDetails) {
        try {
            Paciente pacienteActualizado = pacienteService.updatePaciente(id, pacienteDetails);
            
            if (pacienteActualizado != null) {
                return ResponseEntity.ok(pacienteActualizado);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el paciente con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al actualizar el paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // DELETE - Eliminar paciente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaciente(@PathVariable Long id) {
        try {
            boolean eliminado = pacienteService.deletePaciente(id);
            
            if (eliminado) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Paciente eliminado correctamente");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el paciente con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar el paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // GET - Buscar paciente por RUT
    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> getPacienteByRut(@PathVariable String rut) {
        try {
            Optional<Paciente> paciente = pacienteService.getPacienteByRut(rut);
            
            if (paciente.isPresent()) {
                return ResponseEntity.ok(paciente.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró paciente con RUT: " + rut);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // POST - Cargar pacientes de ejemplo
    @PostMapping("/cargar-ejemplos")
    public ResponseEntity<?> cargarPacientesEjemplo() {
        try {
            Paciente paciente1 = new Paciente("12345678-9", "Juan Pérez", 35, "M", 
                                             "912345678", "juan@email.com", "Calle 123");
            
            Paciente paciente2 = new Paciente("98765432-1", "María González", 28, "F", 
                                             "987654321", "maria@email.com", "Av. 456");
            
            Paciente paciente3 = new Paciente("45678912-3", "Carlos López", 22, "M", 
                                             "945678912", "carlos@email.com", "Pasaje 789");
            
            pacienteService.createPaciente(paciente1);
            pacienteService.createPaciente(paciente2);
            pacienteService.createPaciente(paciente3);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "3 pacientes de ejemplo cargados correctamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al cargar pacientes de ejemplo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}