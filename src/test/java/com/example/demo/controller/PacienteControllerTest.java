package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.service.PacienteService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
public class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacienteService pacienteService;

    private Paciente paciente;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setRut("12345678-9");
        paciente.setNombre("Juan Pérez");
        paciente.setEdad(35);
        paciente.setGenero("M");
        paciente.setTelefono("912345678");
        paciente.setCorreo("juan@email.com");
        paciente.setDireccion("Calle 123");
    }

    @BeforeAll
    static void inicializarPruebas() {
        System.out.println("Iniciando todas las pruebas de PacienteController");
    }

    @Test
    void getTodosLosPacientes_entoncesRetornarListaConEnlacesHATEOAS() throws Exception {

        List<Paciente> pacientes = Arrays.asList(paciente);
        when(pacienteService.getAllPacientes()).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.pacienteList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.pacienteList[0].nombre", is("Juan Pérez")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.crear-paciente.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.cargar-ejemplos.href", containsString("/pacientes/cargar-ejemplos")));

        verify(pacienteService, times(1)).getAllPacientes();
    }

    @Test
    void getPacientePorIdExistente_entoncesRetornarPacienteConEnlacesHATEOAS() throws Exception {

        when(pacienteService.getPacienteById(1L)).thenReturn(Optional.of(paciente));

        mockMvc.perform(get("/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Pérez")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pacientes/1")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.actualizar-paciente.href", containsString("/pacientes/1")))
                .andExpect(jsonPath("$._links.eliminar-paciente.href", containsString("/pacientes/1")));

        verify(pacienteService, times(1)).getPacienteById(1L);
    }

    @Test
    void getPacientePorIdNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {

        when(pacienteService.getPacienteById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pacientes/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.crear-paciente.href", containsString("/pacientes")));

        verify(pacienteService, times(1)).getPacienteById(999L);
    }

    @Test
    void createPacienteValido_entoncesRetornarPacienteCreadoConEnlacesHATEOAS() throws Exception {

        when(pacienteService.createPaciente(any(Paciente.class))).thenReturn(paciente);

        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Pérez")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pacientes/1")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.actualizar-paciente.href", containsString("/pacientes/1")));

        verify(pacienteService, times(1)).createPaciente(any(Paciente.class));
    }

    @Test
    void createPacienteConRutDuplicado_entoncesRetornarBadRequestConEnlaces() throws Exception {

        when(pacienteService.createPaciente(any(Paciente.class)))
                .thenThrow(new RuntimeException("Ya existe un paciente con el RUT: 12345678-9"));

        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Ya existe un paciente")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")));

        verify(pacienteService, times(1)).createPaciente(any(Paciente.class));
    }

    @Test
    void updatePacienteExistente_entoncesRetornarPacienteActualizadoConEnlaces() throws Exception {

        Paciente pacienteActualizado = new Paciente();
        pacienteActualizado.setId(1L);
        pacienteActualizado.setNombre("Juan Pérez Actualizado");
        pacienteActualizado.setRut("12345678-9");

        when(pacienteService.updatePaciente(eq(1L), any(Paciente.class))).thenReturn(pacienteActualizado);

        mockMvc.perform(put("/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Pérez Actualizado")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pacientes/1")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.eliminar-paciente.href", containsString("/pacientes/1")));

        verify(pacienteService, times(1)).updatePaciente(eq(1L), any(Paciente.class));
    }

    @Test
    void updatePacienteNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {

        when(pacienteService.updatePaciente(eq(999L), any(Paciente.class))).thenReturn(null);

        mockMvc.perform(put("/pacientes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.crear-paciente.href", containsString("/pacientes")));

        verify(pacienteService, times(1)).updatePaciente(eq(999L), any(Paciente.class));
    }

    @Test
    void deletePacienteExistente_entoncesRetornarOkConEnlaces() throws Exception {

        when(pacienteService.deletePaciente(1L)).thenReturn(true);

        mockMvc.perform(delete("/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", containsString("eliminado correctamente")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.crear-paciente.href", containsString("/pacientes")));

        verify(pacienteService, times(1)).deletePaciente(1L);
    }

    @Test
    void deletePacienteNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {

        when(pacienteService.deletePaciente(999L)).thenReturn(false);

        mockMvc.perform(delete("/pacientes/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.crear-paciente.href", containsString("/pacientes")));

        verify(pacienteService, times(1)).deletePaciente(999L);
    }

    @Test
    void getPacientePorRutExistente_entoncesRetornarPacienteConEnlaces() throws Exception {

        when(pacienteService.getPacienteByRut("12345678-9")).thenReturn(Optional.of(paciente));

        mockMvc.perform(get("/pacientes/rut/12345678-9")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut", is("12345678-9")))
                .andExpect(jsonPath("$.nombre", is("Juan Pérez")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pacientes/rut/12345678-9")))
                .andExpect(jsonPath("$._links.paciente-id.href", containsString("/pacientes/1")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")));

        verify(pacienteService, times(1)).getPacienteByRut("12345678-9");
    }

    @Test
    void getPacientePorRutNoExistente_entoncesRetornarNotFoundConEnlaces() throws Exception {

        when(pacienteService.getPacienteByRut("00000000-0")).thenReturn(Optional.empty());

        mockMvc.perform(get("/pacientes/rut/00000000-0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")))
                .andExpect(jsonPath("$._links.todos-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.crear-paciente.href", containsString("/pacientes")));

        verify(pacienteService, times(1)).getPacienteByRut("00000000-0");
    }

    @Test
    void cargarPacientesEjemplo_entoncesRetornarMensajeConEnlaces() throws Exception {

        when(pacienteService.createPaciente(any(Paciente.class))).thenReturn(paciente);

        mockMvc.perform(post("/pacientes/cargar-ejemplos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", containsString("cargados correctamente")))
                .andExpect(jsonPath("$.total_cargados", is("3")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pacientes/cargar-ejemplos")))
                .andExpect(jsonPath("$._links.ver-pacientes.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.crear-paciente.href", containsString("/pacientes")));

        verify(pacienteService, times(3)).createPaciente(any(Paciente.class));
    }

    @Test
    void getPacientesVacia_entoncesRetornarListaVaciaConEnlaces() throws Exception {

        when(pacienteService.getAllPacientes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pacientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.crear-paciente.href", containsString("/pacientes")))
                .andExpect(jsonPath("$._links.cargar-ejemplos.href", containsString("/pacientes/cargar-ejemplos")));

        verify(pacienteService, times(1)).getAllPacientes();
    }

    @AfterAll
    static void finalizarPruebas() {
        System.out.println("Finalizando todas las pruebas de PacienteController");
    }
}