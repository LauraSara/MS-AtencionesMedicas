package com.example.demo.controller;

import com.example.demo.model.AtencionMedica;
import com.example.demo.service.AtencionMedicaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

@WebMvcTest(AtencionMedicaController.class)
public class AtencionMedicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtencionMedicaService atencionMedicaService;

    private AtencionMedica atencionMedica;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        atencionMedica = new AtencionMedica();
        atencionMedica.setId(1L);
        atencionMedica.setPacienteId(1L);
        atencionMedica.setMedicoId(1L);
        atencionMedica.setFechaAtencion(LocalDateTime.of(2024, 1, 15, 10, 30));
        atencionMedica.setMotivoConsulta("Dolor de cabeza persistente");
        atencionMedica.setDiagnostico("Migraña crónica");
        atencionMedica.setTratamiento("Analgésicos y reposo");
        atencionMedica.setObservaciones("Paciente con historial de migrañas");
        atencionMedica.setEstado("Realizada");
    }

    @Test
    void getTodasLasAtenciones_entoncesRetornarListaConEnlacesHATEOAS() throws Exception {
        // Given
        List<AtencionMedica> atenciones = Arrays.asList(atencionMedica);
        when(atencionMedicaService.getAllAtenciones()).thenReturn(atenciones);

        // When & Then
        mockMvc.perform(get("/atenciones-medicas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.atencionMedicaList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.atencionMedicaList[0].motivoConsulta", is("Dolor de cabeza persistente")))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.crear-atencion.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.estadisticas.href", containsString("/atenciones-medicas/estadisticas")));
        
        verify(atencionMedicaService, times(1)).getAllAtenciones();
    }

    @Test
    void getAtencionPorIdExistente_entoncesRetornarAtencionConEnlacesHATEOAS() throws Exception {
        // Given
        when(atencionMedicaService.getAtencionById(1L)).thenReturn(Optional.of(atencionMedica));

        // When & Then
        mockMvc.perform(get("/atenciones-medicas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.motivoConsulta", is("Dolor de cabeza persistente")))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas/1")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.actualizar-atencion.href", containsString("/atenciones-medicas/1")))
                .andExpect(jsonPath("$._links.eliminar-atencion.href", containsString("/atenciones-medicas/1")));
        
        verify(atencionMedicaService, times(1)).getAtencionById(1L);
    }

    @Test
    void getAtencionPorIdNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {
        // Given
        when(atencionMedicaService.getAtencionById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/atenciones-medicas/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.crear-atencion.href", containsString("/atenciones-medicas")));
        
        verify(atencionMedicaService, times(1)).getAtencionById(999L);
    }

    @Test
    void cuandoCrearAtencionValida_entoncesRetornarAtencionCreadaConEnlacesHATEOAS() throws Exception {
        // Given
        when(atencionMedicaService.createAtencion(any(AtencionMedica.class))).thenReturn(atencionMedica);

        // When & Then
        mockMvc.perform(post("/atenciones-medicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atencionMedica)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.motivoConsulta", is("Dolor de cabeza persistente")))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas/1")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.actualizar-atencion.href", containsString("/atenciones-medicas/1")));
        
        verify(atencionMedicaService, times(1)).createAtencion(any(AtencionMedica.class));
    }

 
    @Test
    void cuandoActualizarAtencionExistente_entoncesRetornarAtencionActualizadaConEnlaces() throws Exception {
        // Given
        AtencionMedica atencionActualizada = new AtencionMedica();
        atencionActualizada.setId(1L);
        atencionActualizada.setPacienteId(1L); // Agregando campos requeridos
        atencionActualizada.setMedicoId(1L);   // Agregando campos requeridos
        atencionActualizada.setMotivoConsulta("Motivo actualizado");
        atencionActualizada.setEstado("Cancelada");
        atencionActualizada.setFechaAtencion(LocalDateTime.now()); // Agregando fecha requerida

        when(atencionMedicaService.updateAtencion(eq(1L), any(AtencionMedica.class))).thenReturn(atencionActualizada);

        // When & Then
        mockMvc.perform(put("/atenciones-medicas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atencionActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.motivoConsulta", is("Motivo actualizado")))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas/1")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.eliminar-atencion.href", containsString("/atenciones-medicas/1")));
        
        verify(atencionMedicaService, times(1)).updateAtencion(eq(1L), any(AtencionMedica.class));
    }

    @Test
    void cuandoActualizarAtencionNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {
        // Given
        when(atencionMedicaService.updateAtencion(eq(999L), any(AtencionMedica.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/atenciones-medicas/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atencionMedica)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.crear-atencion.href", containsString("/atenciones-medicas")));
        
        verify(atencionMedicaService, times(1)).updateAtencion(eq(999L), any(AtencionMedica.class));
    }

    @Test
    void cuandoEliminarAtencionExistente_entoncesRetornarOkConEnlaces() throws Exception {
        // Given
        when(atencionMedicaService.deleteAtencion(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/atenciones-medicas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", containsString("eliminada correctamente")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.crear-atencion.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.estadisticas.href", containsString("/atenciones-medicas/estadisticas")));
        
        verify(atencionMedicaService, times(1)).deleteAtencion(1L);
    }

    @Test
    void cuandoEliminarAtencionNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {
        // Given
        when(atencionMedicaService.deleteAtencion(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/atenciones-medicas/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.crear-atencion.href", containsString("/atenciones-medicas")));
        
        verify(atencionMedicaService, times(1)).deleteAtencion(999L);
    }

    @Test
    void getAtencionesPorPacienteId_entoncesRetornarListaConEnlaces() throws Exception {
        // Given
        List<AtencionMedica> atenciones = Arrays.asList(atencionMedica);
        when(atencionMedicaService.getAtencionesByPacienteId(1L)).thenReturn(atenciones);

        // When & Then
        mockMvc.perform(get("/atenciones-medicas/paciente/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.atencionMedicaList[0].pacienteId", is(1)))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas/paciente/1")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.estadisticas.href", containsString("/atenciones-medicas/estadisticas")));
        
        verify(atencionMedicaService, times(1)).getAtencionesByPacienteId(1L);
    }

    @Test
    void getAtencionesPorMedicoId_entoncesRetornarListaConEnlaces() throws Exception {
        // Given
        List<AtencionMedica> atenciones = Arrays.asList(atencionMedica);
        when(atencionMedicaService.getAtencionesByMedicoId(1L)).thenReturn(atenciones);

        // When & Then
        mockMvc.perform(get("/atenciones-medicas/medico/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.atencionMedicaList[0].medicoId", is(1)))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas/medico/1")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.estadisticas.href", containsString("/atenciones-medicas/estadisticas")));
        
        verify(atencionMedicaService, times(1)).getAtencionesByMedicoId(1L);
    }

    @Test
    void getAtencionesPorEstadoValido_entoncesRetornarListaConEnlaces() throws Exception {
        // Given
        List<AtencionMedica> atenciones = Arrays.asList(atencionMedica);
        when(atencionMedicaService.getAtencionesByEstado("Realizada")).thenReturn(atenciones);

        // When & Then
        mockMvc.perform(get("/atenciones-medicas/estado/Realizada")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.atencionMedicaList[0].estado", is("Realizada")))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas/estado/Realizada")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.estadisticas.href", containsString("/atenciones-medicas/estadisticas")));
        
        verify(atencionMedicaService, times(1)).getAtencionesByEstado("Realizada");
    }

    @Test
    void getAtencionesPorEstadoInvalido_entoncesRetornarBadRequestConEnlaces() throws Exception {
        // When & Then
        mockMvc.perform(get("/atenciones-medicas/estado/Invalido")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Estado no válido")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")));
        
        verify(atencionMedicaService, never()).getAtencionesByEstado(anyString());
    }

    @Test
    void getEstadisticas_entoncesRetornarEstadisticasConEnlaces() throws Exception {
        // Given
        when(atencionMedicaService.getAllAtenciones()).thenReturn(Arrays.asList(atencionMedica));
        when(atencionMedicaService.countAtencionesByEstado("Realizada")).thenReturn(1L);
        when(atencionMedicaService.countAtencionesByEstado("Programada")).thenReturn(0L);
        when(atencionMedicaService.countAtencionesByEstado("Cancelada")).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/atenciones-medicas/estadisticas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.realizadas", is(1)))
                .andExpect(jsonPath("$.programadas", is(0)))
                .andExpect(jsonPath("$.canceladas", is(0)))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas/estadisticas")))
                .andExpect(jsonPath("$._links.todas-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.atenciones-realizadas.href", containsString("/atenciones-medicas/estado/Realizada")));
        
        verify(atencionMedicaService, times(1)).getAllAtenciones();
        verify(atencionMedicaService, times(1)).countAtencionesByEstado("Realizada");
        verify(atencionMedicaService, times(1)).countAtencionesByEstado("Programada");
        verify(atencionMedicaService, times(1)).countAtencionesByEstado("Cancelada");
    }

    @Test
    void cuandoCargarDatosEjemplo_entoncesRetornarMensajeConEnlaces() throws Exception {
        // Given
        when(atencionMedicaService.createAtencion(any(AtencionMedica.class))).thenReturn(atencionMedica);

        // When & Then
        mockMvc.perform(post("/atenciones-medicas/cargar-ejemplos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", containsString("cargadas correctamente")))
                .andExpect(jsonPath("$.total_cargadas", is("3")))
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas/cargar-ejemplos")))
                .andExpect(jsonPath("$._links.ver-atenciones.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.ver-estadisticas.href", containsString("/atenciones-medicas/estadisticas")))
                .andExpect(jsonPath("$._links.crear-atencion.href", containsString("/atenciones-medicas")));
        
        verify(atencionMedicaService, times(3)).createAtencion(any(AtencionMedica.class));
    }

    @Test
    void getAtencionesVacia_entoncesRetornarListaVaciaConEnlaces() throws Exception {
        // Given
        when(atencionMedicaService.getAllAtenciones()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/atenciones-medicas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.crear-atencion.href", containsString("/atenciones-medicas")))
                .andExpect(jsonPath("$._links.estadisticas.href", containsString("/atenciones-medicas/estadisticas")));
        
        verify(atencionMedicaService, times(1)).getAllAtenciones();
    }
}