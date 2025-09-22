package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.service.MedicoService;
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
@RequestMapping("/medicos")
@CrossOrigin(origins = "*")
public class MedicoController {
    
    @Autowired
    private MedicoService medicoService;
    
    // GET - Obtener todos los médicos
    @GetMapping
    public ResponseEntity<List<Medico>> getAllMedicos() {
        try {
            List<Medico> medicos = medicoService.getAllMedicos();
            return ResponseEntity.ok(medicos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Obtener médico por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicoById(@PathVariable Long id) {
        try {
            Optional<Medico> medico = medicoService.getMedicoById(id);
            
            if (medico.isPresent()) {
                return ResponseEntity.ok(medico.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el médico con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar el médico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // POST - Crear nuevo médico
    @PostMapping
    public ResponseEntity<?> createMedico(@Valid @RequestBody Medico medico) {
        try {
            Medico nuevoMedico = medicoService.createMedico(medico);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMedico);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al crear el médico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // PUT - Actualizar médico existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedico(@PathVariable Long id, 
                                         @Valid @RequestBody Medico medicoDetails) {
        try {
            Medico medicoActualizado = medicoService.updateMedico(id, medicoDetails);
            
            if (medicoActualizado != null) {
                return ResponseEntity.ok(medicoActualizado);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el médico con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al actualizar el médico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // DELETE - Eliminar médico
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedico(@PathVariable Long id) {
        try {
            boolean eliminado = medicoService.deleteMedico(id);
            
            if (eliminado) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Médico eliminado correctamente");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el médico con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar el médico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // GET - Buscar médico por RUT
    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> getMedicoByRut(@PathVariable String rut) {
        try {
            Optional<Medico> medico = medicoService.getMedicoByRut(rut);
            
            if (medico.isPresent()) {
                return ResponseEntity.ok(medico.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró médico con RUT: " + rut);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar médico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // POST - Cargar médicos de ejemplo
    @PostMapping("/cargar-ejemplos")
    public ResponseEntity<?> cargarMedicosEjemplo() {
        try {
            Medico medico1 = new Medico("12345678-9", "Dr. Carlos Martínez", 45, "M", 
                                       "912345678", "carlos@clinica.com", 
                                       "Av. Principal 123", "Cardiología");
            
            Medico medico2 = new Medico("23456789-0", "Dra. Ana González", 38, "F", 
                                       "923456789", "ana@hospital.com", 
                                       "Calle Secundaria 456", "Pediatría");
            
            Medico medico3 = new Medico("34567890-1", "Dr. Roberto Silva", 52, "M", 
                                       "934567890", "roberto@salud.cl", 
                                       "Pasaje 789", "Traumatología");
            
            medicoService.createMedico(medico1);
            medicoService.createMedico(medico2);
            medicoService.createMedico(medico3);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "3 médicos de ejemplo cargados correctamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al cargar médicos de ejemplo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}