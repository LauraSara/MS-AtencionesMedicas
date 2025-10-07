package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.service.MedicoService;
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
@RequestMapping("/medicos")
@CrossOrigin(origins = "*")
public class MedicoController {
    
    @Autowired
    private MedicoService medicoService;
    
    // GET - Obtener todos los médicos 
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Medico>>> getAllMedicos() {
        try {
            List<Medico> medicos = medicoService.getAllMedicos();
            
            List<EntityModel<Medico>> medicosConLinks = medicos.stream()
                .map(medico -> {
                    EntityModel<Medico> resource = EntityModel.of(medico);

                    resource.add(linkTo(methodOn(MedicoController.class)
                        .getMedicoById(medico.getId())).withSelfRel());

                    resource.add(linkTo(methodOn(MedicoController.class)
                        .getMedicoByRut(medico.getRut())).withRel("medico-rut"));
                    return resource;
                })
                .collect(Collectors.toList());
            
            CollectionModel<EntityModel<Medico>> collection = CollectionModel.of(medicosConLinks);

            collection.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withSelfRel());
            collection.add(linkTo(methodOn(MedicoController.class).createMedico(null)).withRel("crear-medico"));
            collection.add(linkTo(methodOn(MedicoController.class).cargarMedicosEjemplo()).withRel("cargar-ejemplos"));
            
            return ResponseEntity.ok(collection);
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
                EntityModel<Medico> resource = EntityModel.of(medico.get());
                
                resource.add(linkTo(methodOn(MedicoController.class).getMedicoById(id)).withSelfRel());
                
                resource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
                resource.add(linkTo(methodOn(MedicoController.class).updateMedico(id, null)).withRel("actualizar-medico"));
                resource.add(linkTo(methodOn(MedicoController.class).deleteMedico(id)).withRel("eliminar-medico"));
                resource.add(linkTo(methodOn(MedicoController.class).getMedicoByRut(medico.get().getRut())).withRel("medico-rut"));
                
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el médico con ID: " + id);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
                errorResource.add(linkTo(methodOn(MedicoController.class).createMedico(null)).withRel("crear-medico"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar el médico: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
    
    // POST - Crear nuevo médico 
    @PostMapping
    public ResponseEntity<?> createMedico(@Valid @RequestBody Medico medico) {
        try {
            Medico nuevoMedico = medicoService.createMedico(medico);
            
            EntityModel<Medico> resource = EntityModel.of(nuevoMedico);
            resource.add(linkTo(methodOn(MedicoController.class).getMedicoById(nuevoMedico.getId())).withSelfRel());
            resource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
            resource.add(linkTo(methodOn(MedicoController.class).updateMedico(nuevoMedico.getId(), null)).withRel("actualizar-medico"));
            resource.add(linkTo(methodOn(MedicoController.class).getMedicoByRut(nuevoMedico.getRut())).withRel("medico-rut"));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(resource);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
            return ResponseEntity.badRequest().body(errorResource);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al crear el médico: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
    
    // PUT - Actualizar médico existente 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedico(@PathVariable Long id, 
                                         @Valid @RequestBody Medico medicoDetails) {
        try {
            Medico medicoActualizado = medicoService.updateMedico(id, medicoDetails);
            
            if (medicoActualizado != null) {
                EntityModel<Medico> resource = EntityModel.of(medicoActualizado);
                resource.add(linkTo(methodOn(MedicoController.class).getMedicoById(id)).withSelfRel());
                resource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
                resource.add(linkTo(methodOn(MedicoController.class).deleteMedico(id)).withRel("eliminar-medico"));
                resource.add(linkTo(methodOn(MedicoController.class).getMedicoByRut(medicoActualizado.getRut())).withRel("medico-rut"));
                
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el médico con ID: " + id);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
                errorResource.add(linkTo(methodOn(MedicoController.class).createMedico(null)).withRel("crear-medico"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al actualizar el médico: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
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
                EntityModel<Map<String, String>> resource = EntityModel.of(response);
                resource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
                resource.add(linkTo(methodOn(MedicoController.class).createMedico(null)).withRel("crear-medico"));
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró el médico con ID: " + id);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
                errorResource.add(linkTo(methodOn(MedicoController.class).createMedico(null)).withRel("crear-medico"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar el médico: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
    
    // GET - Buscar médico por RUT 
    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> getMedicoByRut(@PathVariable String rut) {
        try {
            Optional<Medico> medico = medicoService.getMedicoByRut(rut);
            
            if (medico.isPresent()) {
                EntityModel<Medico> resource = EntityModel.of(medico.get());
                resource.add(linkTo(methodOn(MedicoController.class).getMedicoByRut(rut)).withSelfRel());
                resource.add(linkTo(methodOn(MedicoController.class).getMedicoById(medico.get().getId())).withRel("medico-id"));
                resource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
                resource.add(linkTo(methodOn(MedicoController.class).updateMedico(medico.get().getId(), null)).withRel("actualizar-medico"));
                
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró médico con RUT: " + rut);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
                errorResource.add(linkTo(methodOn(MedicoController.class).createMedico(null)).withRel("crear-medico"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar médico: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
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
            response.put("total_cargados", "3");
            
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            resource.add(linkTo(methodOn(MedicoController.class).cargarMedicosEjemplo()).withSelfRel());
            resource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("ver-medicos"));
            resource.add(linkTo(methodOn(MedicoController.class).createMedico(null)).withRel("crear-medico"));
            
            return ResponseEntity.ok(resource);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al cargar médicos de ejemplo: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(linkTo(methodOn(MedicoController.class).getAllMedicos()).withRel("todos-medicos"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
}