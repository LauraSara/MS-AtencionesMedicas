package com.example.demo.controller;

import com.example.demo.model.AtencionMedica;
import com.example.demo.service.AtencionMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/atenciones-medicas")
@CrossOrigin(origins = "*")
public class AtencionMedicaController {

    @Autowired
    private AtencionMedicaService atencionMedicaService;

    // GET - Obtener todas las atenciones médicas 
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<AtencionMedica>>> getAllAtenciones() {
        try {
            List<AtencionMedica> atenciones = atencionMedicaService.getAllAtenciones();

            List<EntityModel<AtencionMedica>> atencionesConLinks = atenciones.stream()
                    .map(atencion -> {
                        EntityModel<AtencionMedica> resource = EntityModel.of(atencion);

                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionById(atencion.getId())).withSelfRel());
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionesByPacienteId(atencion.getPacienteId())).withRel("atenciones-paciente"));
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionesByMedicoId(atencion.getMedicoId())).withRel("atenciones-medico"));
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionesByEstado(atencion.getEstado())).withRel("atenciones-estado"));
                        return resource;
                    })
                    .collect(Collectors.toList());

            CollectionModel<EntityModel<AtencionMedica>> collection = CollectionModel.of(atencionesConLinks);

            collection.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withSelfRel());
            collection.add(
                    linkTo(methodOn(AtencionMedicaController.class).createAtencion(null)).withRel("crear-atencion"));
            collection.add(linkTo(methodOn(AtencionMedicaController.class).getEstadisticas()).withRel("estadisticas"));
            collection.add(
                    linkTo(methodOn(AtencionMedicaController.class).cargarDatosEjemplo()).withRel("cargar-ejemplos"));

            return ResponseEntity.ok(collection);
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
                EntityModel<AtencionMedica> resource = EntityModel.of(atencion.get());

                
                resource.add(linkTo(methodOn(AtencionMedicaController.class).getAtencionById(id)).withSelfRel());

                
                resource.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones())
                        .withRel("todas-atenciones"));
                resource.add(linkTo(methodOn(AtencionMedicaController.class).updateAtencion(id, null))
                        .withRel("actualizar-atencion"));
                resource.add(linkTo(methodOn(AtencionMedicaController.class).deleteAtencion(id))
                        .withRel("eliminar-atencion"));
                resource.add(linkTo(methodOn(AtencionMedicaController.class)
                        .getAtencionesByPacienteId(atencion.get().getPacienteId())).withRel("atenciones-paciente"));
                resource.add(linkTo(
                        methodOn(AtencionMedicaController.class).getAtencionesByMedicoId(atencion.get().getMedicoId()))
                        .withRel("atenciones-medico"));
                resource.add(linkTo(
                        methodOn(AtencionMedicaController.class).getAtencionesByEstado(atencion.get().getEstado()))
                        .withRel("atenciones-estado"));
                resource.add(
                        linkTo(methodOn(AtencionMedicaController.class).getEstadisticas()).withRel("estadisticas"));

                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró la atención médica con ID: " + id);
                
                
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones())
                        .withRel("todas-atenciones"));
                errorResource.add(linkTo(methodOn(AtencionMedicaController.class).createAtencion(null))
                        .withRel("crear-atencion"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar la atención médica: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    // POST - Crear nueva atención médica 
    @PostMapping
    public ResponseEntity<?> createAtencion(@Valid @RequestBody AtencionMedica atencionMedica) {
        try {
            if (atencionMedica.getPacienteId() == null || atencionMedica.getMedicoId() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "El paciente y médico son obligatorios");

                Map<String, String> todasAtencionesLink = new HashMap<>();
                todasAtencionesLink.put("href", "/atenciones-medicas");

                Map<String, Object> links = new HashMap<>();
                links.put("todas-atenciones", todasAtencionesLink);

                response.put("_links", links);

                return ResponseEntity.badRequest().body(response);
            }

            AtencionMedica nuevaAtencion = atencionMedicaService.createAtencion(atencionMedica);

            // Crear recurso con enlaces HATEOAS
            EntityModel<AtencionMedica> resource = EntityModel.of(nuevaAtencion);
            resource.add(linkTo(methodOn(AtencionMedicaController.class).getAtencionById(nuevaAtencion.getId()))
                    .withSelfRel());
            resource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            resource.add(linkTo(methodOn(AtencionMedicaController.class).updateAtencion(nuevaAtencion.getId(), null))
                    .withRel("actualizar-atencion"));
            resource.add(linkTo(
                    methodOn(AtencionMedicaController.class).getAtencionesByPacienteId(nuevaAtencion.getPacienteId()))
                    .withRel("atenciones-paciente"));
            resource.add(linkTo(
                    methodOn(AtencionMedicaController.class).getAtencionesByMedicoId(nuevaAtencion.getMedicoId()))
                    .withRel("atenciones-medico"));

            return ResponseEntity.status(HttpStatus.CREATED).body(resource);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al crear la atención médica: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    // PUT - Actualizar atención médica existente 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAtencion(@PathVariable Long id,
            @Valid @RequestBody AtencionMedica atencionDetails) {
        try {
            AtencionMedica atencionActualizada = atencionMedicaService.updateAtencion(id, atencionDetails);

            if (atencionActualizada != null) {
                EntityModel<AtencionMedica> resource = EntityModel.of(atencionActualizada);
                resource.add(linkTo(methodOn(AtencionMedicaController.class).getAtencionById(id)).withSelfRel());
                resource.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones())
                        .withRel("todas-atenciones"));
                resource.add(linkTo(methodOn(AtencionMedicaController.class).deleteAtencion(id))
                        .withRel("eliminar-atencion"));
                resource.add(linkTo(methodOn(AtencionMedicaController.class)
                        .getAtencionesByPacienteId(atencionActualizada.getPacienteId()))
                        .withRel("atenciones-paciente"));
                resource.add(linkTo(methodOn(AtencionMedicaController.class)
                        .getAtencionesByMedicoId(atencionActualizada.getMedicoId())).withRel("atenciones-medico"));

                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró la atención médica con ID: " + id);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones())
                        .withRel("todas-atenciones"));
                errorResource.add(linkTo(methodOn(AtencionMedicaController.class).createAtencion(null))
                        .withRel("crear-atencion"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al actualizar la atención médica: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
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
                EntityModel<Map<String, String>> resource = EntityModel.of(response);
                resource.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones())
                        .withRel("todas-atenciones"));
                resource.add(linkTo(methodOn(AtencionMedicaController.class).createAtencion(null))
                        .withRel("crear-atencion"));
                resource.add(
                        linkTo(methodOn(AtencionMedicaController.class).getEstadisticas()).withRel("estadisticas"));
                return ResponseEntity.ok(resource);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No se encontró la atención médica con ID: " + id);
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones())
                        .withRel("todas-atenciones"));
                errorResource.add(linkTo(methodOn(AtencionMedicaController.class).createAtencion(null))
                        .withRel("crear-atencion"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar la atención médica: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    // GET - Obtener atenciones por paciente 
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> getAtencionesByPacienteId(@PathVariable Long pacienteId) {
        try {
            List<AtencionMedica> atenciones = atencionMedicaService.getAtencionesByPacienteId(pacienteId);

            List<EntityModel<AtencionMedica>> atencionesConLinks = atenciones.stream()
                    .map(atencion -> {
                        EntityModel<AtencionMedica> resource = EntityModel.of(atencion);
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionById(atencion.getId())).withSelfRel());
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionesByMedicoId(atencion.getMedicoId())).withRel("atenciones-medico"));
                        return resource;
                    })
                    .collect(Collectors.toList());

            CollectionModel<EntityModel<AtencionMedica>> collection = CollectionModel.of(atencionesConLinks);
            collection.add(linkTo(methodOn(AtencionMedicaController.class).getAtencionesByPacienteId(pacienteId))
                    .withSelfRel());
            collection.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            collection.add(linkTo(methodOn(AtencionMedicaController.class).getEstadisticas()).withRel("estadisticas"));

            return ResponseEntity.ok(collection);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar atenciones del paciente: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    // GET - Obtener atenciones por médico 
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<?> getAtencionesByMedicoId(@PathVariable Long medicoId) {
        try {
            List<AtencionMedica> atenciones = atencionMedicaService.getAtencionesByMedicoId(medicoId);

            List<EntityModel<AtencionMedica>> atencionesConLinks = atenciones.stream()
                    .map(atencion -> {
                        EntityModel<AtencionMedica> resource = EntityModel.of(atencion);
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionById(atencion.getId())).withSelfRel());
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionesByPacienteId(atencion.getPacienteId())).withRel("atenciones-paciente"));
                        return resource;
                    })
                    .collect(Collectors.toList());

            CollectionModel<EntityModel<AtencionMedica>> collection = CollectionModel.of(atencionesConLinks);
            collection.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAtencionesByMedicoId(medicoId)).withSelfRel());
            collection.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            collection.add(linkTo(methodOn(AtencionMedicaController.class).getEstadisticas()).withRel("estadisticas"));

            return ResponseEntity.ok(collection);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar atenciones del médico: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    // GET - Obtener atenciones por estado 
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getAtencionesByEstado(@PathVariable String estado) {
        try {
            if (!estado.equals("Programada") && !estado.equals("Realizada") && !estado.equals("Cancelada")) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Estado no válido. Debe ser: Programada, Realizada o Cancelada");
                EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
                errorResource.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones())
                        .withRel("todas-atenciones"));
                return ResponseEntity.badRequest().body(errorResource);
            }

            List<AtencionMedica> atenciones = atencionMedicaService.getAtencionesByEstado(estado);

            List<EntityModel<AtencionMedica>> atencionesConLinks = atenciones.stream()
                    .map(atencion -> {
                        EntityModel<AtencionMedica> resource = EntityModel.of(atencion);
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionById(atencion.getId())).withSelfRel());
                        resource.add(linkTo(methodOn(AtencionMedicaController.class)
                                .getAtencionesByPacienteId(atencion.getPacienteId())).withRel("atenciones-paciente"));
                        return resource;
                    })
                    .collect(Collectors.toList());

            CollectionModel<EntityModel<AtencionMedica>> collection = CollectionModel.of(atencionesConLinks);
            collection
                    .add(linkTo(methodOn(AtencionMedicaController.class).getAtencionesByEstado(estado)).withSelfRel());
            collection.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            collection.add(linkTo(methodOn(AtencionMedicaController.class).getEstadisticas()).withRel("estadisticas"));

            return ResponseEntity.ok(collection);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al buscar atenciones por estado: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
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

            // Calcular porcentajes
            if (total > 0) {
                estadisticas.put("porcentajeRealizadas", (realizadas * 100.0) / total);
                estadisticas.put("porcentajeProgramadas", (programadas * 100.0) / total);
                estadisticas.put("porcentajeCanceladas", (canceladas * 100.0) / total);
            }

            EntityModel<Map<String, Object>> resource = EntityModel.of(estadisticas);
            resource.add(linkTo(methodOn(AtencionMedicaController.class).getEstadisticas()).withSelfRel());
            resource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            resource.add(linkTo(methodOn(AtencionMedicaController.class).getAtencionesByEstado("Realizada"))
                    .withRel("atenciones-realizadas"));
            resource.add(linkTo(methodOn(AtencionMedicaController.class).getAtencionesByEstado("Programada"))
                    .withRel("atenciones-programadas"));
            resource.add(linkTo(methodOn(AtencionMedicaController.class).getAtencionesByEstado("Cancelada"))
                    .withRel("atenciones-canceladas"));

            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al calcular estadísticas: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }

    // POST - Cargar datos de ejemplo 
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
            response.put("total_cargadas", "3");

            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            resource.add(linkTo(methodOn(AtencionMedicaController.class).cargarDatosEjemplo()).withSelfRel());
            resource.add(linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("ver-atenciones"));
            resource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getEstadisticas()).withRel("ver-estadisticas"));
            resource.add(
                    linkTo(methodOn(AtencionMedicaController.class).createAtencion(null)).withRel("crear-atencion"));

            return ResponseEntity.ok(resource);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al cargar datos de ejemplo: " + e.getMessage());
            EntityModel<Map<String, String>> errorResource = EntityModel.of(response);
            errorResource.add(
                    linkTo(methodOn(AtencionMedicaController.class).getAllAtenciones()).withRel("todas-atenciones"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource);
        }
    }
}