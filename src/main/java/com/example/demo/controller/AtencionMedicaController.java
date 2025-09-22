package com.example.demo.controller;

import com.example.demo.model.AtencionMedica;
import com.example.demo.service.AtencionMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/atenciones-medicas")
@CrossOrigin(origins = "*")
public class AtencionMedicaController {
    
    @Autowired
    private AtencionMedicaService atencionMedicaService;
    
    // GET - Obtener todas las atenciones médicas
    @GetMapping
    public ResponseEntity<List<AtencionMedica>> getAllAtenciones() {
        try {
            List<AtencionMedica> atenciones = atencionMedicaService.getAllAtenciones();
            return ResponseEntity.ok(atenciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Obtener atención por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAtencionById(@PathVariable Long id) {
        try {
            Optional<AtencionMedica> atencion = atencionMedicaService.getAtencionById(id);
            
            if (atencion.isPresent()) {
                return ResponseEntity.ok(atencion.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró la atención médica con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar la atención médica: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // POST - Crear nueva atención médica
    @PostMapping
    public ResponseEntity<?> createAtencion(@Valid @RequestBody AtencionMedica atencionMedica) {
        try {
            if (atencionMedica.getPacienteId() == null || atencionMedica.getMedicoId() == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "El ID del paciente y médico son obligatorios");
                return ResponseEntity.badRequest().body(response);
            }
            
            AtencionMedica nuevaAtencion = atencionMedicaService.createAtencion(atencionMedica);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAtencion);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al crear la atención médica: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // PUT - Actualizar atención médica existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAtencion(@PathVariable Long id, 
                                           @Valid @RequestBody AtencionMedica atencionDetails) {
        try {
            AtencionMedica atencionActualizada = atencionMedicaService.updateAtencion(id, atencionDetails);
            
            if (atencionActualizada != null) {
                return ResponseEntity.ok(atencionActualizada);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró la atención médica con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al actualizar la atención médica: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // DELETE - Eliminar atención médica
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAtencion(@PathVariable Long id) {
        try {
            boolean eliminado = atencionMedicaService.deleteAtencion(id);
            
            if (eliminado) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Atención médica eliminada correctamente");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró la atención médica con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar la atención médica: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // GET - Obtener atenciones por paciente
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> getAtencionesByPacienteId(@PathVariable Long pacienteId) {
        try {
            List<AtencionMedica> atenciones = atencionMedicaService.getAtencionesByPacienteId(pacienteId);
            return ResponseEntity.ok(atenciones);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar atenciones del paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // GET - Obtener atenciones por médico
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<?> getAtencionesByMedicoId(@PathVariable Long medicoId) {
        try {
            List<AtencionMedica> atenciones = atencionMedicaService.getAtencionesByMedicoId(medicoId);
            return ResponseEntity.ok(atenciones);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar atenciones del médico: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // GET - Obtener atenciones por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getAtencionesByEstado(@PathVariable String estado) {
        try {
            if (!estado.equals("Programada") && !estado.equals("Realizada") && !estado.equals("Cancelada")) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Estado no válido. Debe ser: Programada, Realizada o Cancelada");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<AtencionMedica> atenciones = atencionMedicaService.getAtencionesByEstado(estado);
            return ResponseEntity.ok(atenciones);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar atenciones por estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // GET - Estadísticas de atenciones
    @GetMapping("/estadisticas")
    public ResponseEntity<?> getEstadisticas() {
        try {
            Long total = (long) atencionMedicaService.getAllAtenciones().size();
            Long realizadas = atencionMedicaService.countAtencionesByEstado("Realizada");
            Long programadas = atencionMedicaService.countAtencionesByEstado("Programada");
            Long canceladas = atencionMedicaService.countAtencionesByEstado("Cancelada");
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("total", total);
            estadisticas.put("realizadas", realizadas);
            estadisticas.put("programadas", programadas);
            estadisticas.put("canceladas", canceladas);
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al calcular estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // POST - Cargar datos de ejemplo (para cumplir con los 3 registros mínimos)
    @PostMapping("/cargar-ejemplos")
    public ResponseEntity<?> cargarDatosEjemplo() {
        try {
            AtencionMedica atencion1 = new AtencionMedica();
            atencion1.setPacienteId(1L);
            atencion1.setMedicoId(1L);
            atencion1.setFechaAtencion(LocalDateTime.of(2024, 1, 15, 10, 30));
            atencion1.setMotivoConsulta("Dolor de pecho y falta de aire");
            atencion1.setDiagnostico("Angina de pecho");
            atencion1.setTratamiento("Reposo, nitroglicerina sublingual y control cardiológico");
            atencion1.setObservaciones("Paciente con factores de riesgo cardiovascular");
            atencion1.setEstado("Realizada");
            
            AtencionMedica atencion2 = new AtencionMedica();
            atencion2.setPacienteId(2L);
            atencion2.setMedicoId(2L);
            atencion2.setFechaAtencion(LocalDateTime.of(2024, 1, 16, 11, 0));
            atencion2.setMotivoConsulta("Fiebre alta y dolor de garganta en niño");
            atencion2.setDiagnostico("Faringitis");
            atencion2.setTratamiento("Amoxicilina por 10 días y paracetamol para la fiebre");
            atencion2.setObservaciones("Niño de 5 años, buen estado general");
            atencion2.setEstado("Realizada");
            
            AtencionMedica atencion3 = new AtencionMedica();
            atencion3.setPacienteId(3L);
            atencion3.setMedicoId(3L);
            atencion3.setFechaAtencion(LocalDateTime.of(2024, 1, 17, 9, 15));
            atencion3.setMotivoConsulta("Dolor en rodilla después de accidente deportivo");
            atencion3.setDiagnostico("Esguince");
            atencion3.setTratamiento("Inmovilización, fisioterapia y control en 2 semanas");
            atencion3.setObservaciones("Paciente futbolista amateur");
            atencion3.setEstado("Programada");
            
            atencionMedicaService.createAtencion(atencion1);
            atencionMedicaService.createAtencion(atencion2);
            atencionMedicaService.createAtencion(atencion3);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "3 atenciones médicas de ejemplo cargadas correctamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al cargar datos de ejemplo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}