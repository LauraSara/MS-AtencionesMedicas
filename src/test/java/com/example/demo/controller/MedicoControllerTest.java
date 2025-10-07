package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.service.MedicoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicoController.class)
public class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicoService medicoService;

    private Medico medico;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        medico = new Medico();
        medico.setId(1L);
        medico.setRut("12345678-9");
        medico.setNombre("Dr. Carlos Martínez");
        medico.setEdad(45);
        medico.setGenero("M");
        medico.setTelefono("912345678");
        medico.setCorreo("carlos@clinica.com");
        medico.setDireccion("Av. Principal 123");
        medico.setEspecialidad("Cardiología");
    }

    @Test
    void getTodosLosMedicos_entoncesRetornarListaConEnlacesHATEOAS() throws Exception {
        // Given
        List<Medico> medicos = Arrays.asList(medico);
        when(medicoService.getAllMedicos()).thenReturn(medicos);

        // When & Then
        mockMvc.perform(get("/medicos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.medicoList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.medicoList[0].nombre", is("Dr. Carlos Martínez")))
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.crear-medico.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.cargar-ejemplos.href", containsString("/medicos/cargar-ejemplos")));
        
        verify(medicoService, times(1)).getAllMedicos();
    }

    @Test
    void getMedicoPorIdExistente_entoncesRetornarMedicoConEnlacesHATEOAS() throws Exception {
        // Given
        when(medicoService.getMedicoById(1L)).thenReturn(Optional.of(medico));

        // When & Then
        mockMvc.perform(get("/medicos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Dr. Carlos Martínez")))
                .andExpect(jsonPath("$.especialidad", is("Cardiología")))
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos/1")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.actualizar-medico.href", containsString("/medicos/1")))
                .andExpect(jsonPath("$._links.eliminar-medico.href", containsString("/medicos/1")));
        
        verify(medicoService, times(1)).getMedicoById(1L);
    }

    @Test
    void getMedicoPorIdNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {
        // Given
        when(medicoService.getMedicoById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/medicos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.crear-medico.href", containsString("/medicos")));
        
        verify(medicoService, times(1)).getMedicoById(999L);
    }

    @Test
    void cuandoCrearMedicoValido_entoncesRetornarMedicoCreadoConEnlacesHATEOAS() throws Exception {
        // Given
        when(medicoService.createMedico(any(Medico.class))).thenReturn(medico);

        // When & Then
        mockMvc.perform(post("/medicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Dr. Carlos Martínez")))
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos/1")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.actualizar-medico.href", containsString("/medicos/1")));
        
        verify(medicoService, times(1)).createMedico(any(Medico.class));
    }

    @Test
    void cuandoCrearMedicoConRutDuplicado_entoncesRetornarBadRequestConEnlaces() throws Exception {
        // Given
        when(medicoService.createMedico(any(Medico.class)))
            .thenThrow(new RuntimeException("Ya existe un médico con el RUT: 12345678-9"));

        // When & Then
        mockMvc.perform(post("/medicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Ya existe un médico")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")));
        
        verify(medicoService, times(1)).createMedico(any(Medico.class));
    }

    @Test
    void cuandoActualizarMedicoExistente_entoncesRetornarMedicoActualizadoConEnlaces() throws Exception {
        // Given
        Medico medicoActualizado = new Medico();
        medicoActualizado.setId(1L);
        medicoActualizado.setNombre("Dr. Carlos Martínez Actualizado");
        medicoActualizado.setRut("12345678-9");
        medicoActualizado.setEspecialidad("Cardiología Avanzada");

        when(medicoService.updateMedico(eq(1L), any(Medico.class))).thenReturn(medicoActualizado);

        // When & Then
        mockMvc.perform(put("/medicos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Dr. Carlos Martínez Actualizado")))
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos/1")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.eliminar-medico.href", containsString("/medicos/1")));
        
        verify(medicoService, times(1)).updateMedico(eq(1L), any(Medico.class));
    }

    @Test
    void cuandoActualizarMedicoNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {
        // Given
        when(medicoService.updateMedico(eq(999L), any(Medico.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/medicos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.crear-medico.href", containsString("/medicos")));
        
        verify(medicoService, times(1)).updateMedico(eq(999L), any(Medico.class));
    }

    @Test
    void cuandoEliminarMedicoExistente_entoncesRetornarOkConEnlaces() throws Exception {
        // Given
        when(medicoService.deleteMedico(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/medicos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", containsString("eliminado correctamente")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.crear-medico.href", containsString("/medicos")));
        
        verify(medicoService, times(1)).deleteMedico(1L);
    }

    @Test
    void cuandoEliminarMedicoNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {
        // Given
        when(medicoService.deleteMedico(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/medicos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.crear-medico.href", containsString("/medicos")));
        
        verify(medicoService, times(1)).deleteMedico(999L);
    }

    @Test
    void getMedicoPorRutExistente_entoncesRetornarMedicoConEnlaces() throws Exception {
        // Given
        when(medicoService.getMedicoByRut("12345678-9")).thenReturn(Optional.of(medico));

        // When & Then
        mockMvc.perform(get("/medicos/rut/12345678-9")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut", is("12345678-9")))
                .andExpect(jsonPath("$.nombre", is("Dr. Carlos Martínez")))
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos/rut/12345678-9")))
                .andExpect(jsonPath("$._links.medico-id.href", containsString("/medicos/1")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")));
        
        verify(medicoService, times(1)).getMedicoByRut("12345678-9");
    }

    @Test
    void getMedicoPorRutNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {
        // Given
        when(medicoService.getMedicoByRut("00000000-0")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/medicos/rut/00000000-0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todos-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.crear-medico.href", containsString("/medicos")));
        
        verify(medicoService, times(1)).getMedicoByRut("00000000-0");
    }

    @Test
    void cuandoCargarMedicosEjemplo_entoncesRetornarMensajeConEnlaces() throws Exception {
        // Given
        when(medicoService.createMedico(any(Medico.class))).thenReturn(medico);

        // When & Then
        mockMvc.perform(post("/medicos/cargar-ejemplos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", containsString("cargados correctamente")))
                .andExpect(jsonPath("$.total_cargados", is("3")))
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos/cargar-ejemplos")))
                .andExpect(jsonPath("$._links.ver-medicos.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.crear-medico.href", containsString("/medicos")));
        
        verify(medicoService, times(3)).createMedico(any(Medico.class));
    }

    @Test
    void getMedicosVacia_entoncesRetornarListaVaciaConEnlaces() throws Exception {
        // Given
        when(medicoService.getAllMedicos()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/medicos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.crear-medico.href", containsString("/medicos")))
                .andExpect(jsonPath("$._links.cargar-ejemplos.href", containsString("/medicos/cargar-ejemplos")));
        
        verify(medicoService, times(1)).getAllMedicos();
    }

    @Test
    void cuandoCrearMedicoConDatosInvalidos_entoncesRetornarBadRequest() throws Exception {
        // Given
        Medico medicoInvalido = new Medico();
        medicoInvalido.setRut(""); // RUT vacío - inválido
        medicoInvalido.setNombre(""); // Nombre vacío - inválido

        // When & Then
        mockMvc.perform(post("/medicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicoInvalido)))
                .andExpect(status().isBadRequest());
        
        verify(medicoService, never()).createMedico(any(Medico.class));
    }
}