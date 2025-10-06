package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    // GET - Obtener todos los pacientes con HATEOAS
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Paciente>>> getAllPacientes() {
        try {
            List<Paciente> pacientes = pacienteService.getAllPacientes();
            
            List<EntityModel<Paciente>> pacientesConLinks = pacientes.stream()
                .map(paciente -> {
                    EntityModel<Paciente> resource = EntityModel.of(paciente);
                    // Self link
                    resource.add(linkTo(methodOn(PacienteController.class)
                        .getPacienteById(paciente.getId())).withSelfRel());
                    // Related links
                    resource.add(linkTo(methodOn(PacienteController.class)
                        .getPacienteByRut(paciente.getRut())).withRel("paciente-rut"));
                    return resource;
                })
                .collect(Collectors.toList());
            
            CollectionModel<EntityModel<Paciente>> collection = CollectionModel.of(pacientesConLinks);
            // Collection links
            collection.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withSelfRel());
            collection.add(linkTo(methodOn(PacienteController.class).createPaciente(null)).withRel("crear-paciente"));
            collection.add(linkTo(methodOn(PacienteController.class).cargarPacientesEjemplo()).withRel("cargar-ejemplos"));
            
            return ResponseEntity.ok(collection);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Obtener paciente por ID con HATEOAS
    @GetMapping("/{id}")
    public ResponseEntity<?> getPacienteById(@PathVariable Long id) {
        try {
            Optional<Paciente> paciente = pacienteService.getPacienteById(id);
            
            if (paciente.isPresent()) {
                EntityModel<Paciente> resource = EntityModel.of(paciente.get());
                
                // Self link
                resource.add(linkTo(methodOn(PacienteController.class).getPacienteById(id)).withSelfRel());
                
                // Navigation links
                resource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
                resource.add(linkTo(methodOn(PacienteController.class).updatePaciente(id, null)).withRel("actualizar-paciente"));
                resource.add(linkTo(methodOn(PacienteController.class).deletePaciente(id)).withRel("eliminar-paciente"));
                resource.add(linkTo(methodOn(PacienteController.class).getPacienteByRut(paciente.get().getRut())).withRel("paciente-rut"));
                
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el paciente con ID: " + id);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
                errorResource.add(linkTo(methodOn(PacienteController.class).createPaciente(null)).withRel("crear-paciente"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar el paciente: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
    
    // POST - Crear nuevo paciente con HATEOAS
    @PostMapping
    public ResponseEntity<?> createPaciente(@Valid @RequestBody Paciente paciente) {
        try {
            Paciente nuevoPaciente = pacienteService.createPaciente(paciente);
            
            // Crear recurso con enlaces HATEOAS
            EntityModel<Paciente> resource = EntityModel.of(nuevoPaciente);
            resource.add(linkTo(methodOn(PacienteController.class).getPacienteById(nuevoPaciente.getId())).withSelfRel());
            resource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
            resource.add(linkTo(methodOn(PacienteController.class).updatePaciente(nuevoPaciente.getId(), null)).withRel("actualizar-paciente"));
            resource.add(linkTo(methodOn(PacienteController.class).getPacienteByRut(nuevoPaciente.getRut())).withRel("paciente-rut"));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(resource);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
            return ResponseEntity.badRequest().body(errorResource);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al crear el paciente: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
    
    // PUT - Actualizar paciente existente con HATEOAS
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaciente(@PathVariable Long id, 
                                           @Valid @RequestBody Paciente pacienteDetails) {
        try {
            Paciente pacienteActualizado = pacienteService.updatePaciente(id, pacienteDetails);
            
            if (pacienteActualizado != null) {
                EntityModel<Paciente> resource = EntityModel.of(pacienteActualizado);
                resource.add(linkTo(methodOn(PacienteController.class).getPacienteById(id)).withSelfRel());
                resource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
                resource.add(linkTo(methodOn(PacienteController.class).deletePaciente(id)).withRel("eliminar-paciente"));
                resource.add(linkTo(methodOn(PacienteController.class).getPacienteByRut(pacienteActualizado.getRut())).withRel("paciente-rut"));
                
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el paciente con ID: " + id);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
                errorResource.add(linkTo(methodOn(PacienteController.class).createPaciente(null)).withRel("crear-paciente"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al actualizar el paciente: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
    
    // DELETE - Eliminar paciente con HATEOAS
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaciente(@PathVariable Long id) {
        try {
            boolean eliminado = pacienteService.deletePaciente(id);
            
            if (eliminado) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Paciente eliminado correctamente");
                EntityModel<Map<String, String>> resource = EntityModel.of(response);
                resource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
                resource.add(linkTo(methodOn(PacienteController.class).createPaciente(null)).withRel("crear-paciente"));
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el paciente con ID: " + id);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
                errorResource.add(linkTo(methodOn(PacienteController.class).createPaciente(null)).withRel("crear-paciente"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar el paciente: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
    
    // GET - Buscar paciente por RUT con HATEOAS
    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> getPacienteByRut(@PathVariable String rut) {
        try {
            Optional<Paciente> paciente = pacienteService.getPacienteByRut(rut);
            
            if (paciente.isPresent()) {
                EntityModel<Paciente> resource = EntityModel.of(paciente.get());
                resource.add(linkTo(methodOn(PacienteController.class).getPacienteByRut(rut)).withSelfRel());
                resource.add(linkTo(methodOn(PacienteController.class).getPacienteById(paciente.get().getId())).withRel("paciente-id"));
                resource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
                resource.add(linkTo(methodOn(PacienteController.class).updatePaciente(paciente.get().getId(), null)).withRel("actualizar-paciente"));
                
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró paciente con RUT: " + rut);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
                errorResource.add(linkTo(methodOn(PacienteController.class).createPaciente(null)).withRel("crear-paciente"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar paciente: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
    
    // POST - Cargar pacientes de ejemplo con HATEOAS
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
            response.put("total_cargados", "3");
            
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            resource.add(linkTo(methodOn(PacienteController.class).cargarPacientesEjemplo()).withSelfRel());
            resource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("ver-pacientes"));
            resource.add(linkTo(methodOn(PacienteController.class).createPaciente(null)).withRel("crear-paciente"));
            
            return ResponseEntity.ok(resource);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al cargar pacientes de ejemplo: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("todos-pacientes"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
}