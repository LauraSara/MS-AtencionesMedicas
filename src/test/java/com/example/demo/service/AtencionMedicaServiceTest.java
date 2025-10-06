// Archivo: src/test/java/com/example/demo/service/AtencionMedicaServiceTest.java
package com.example.demo.service;

import com.example.demo.model.AtencionMedica;
import com.example.demo.repository.AtencionMedicaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AtencionMedicaServiceTest {

    @Mock
    private AtencionMedicaRepository atencionMedicaRepository;

    @InjectMocks
    private AtencionMedicaService atencionMedicaService;

    private AtencionMedica atencionMedica;

    @BeforeEach
    void setUp() {
        atencionMedica = new AtencionMedica();
        atencionMedica.setId(1L);
        atencionMedica.setPacienteId(1L);
        atencionMedica.setMedicoId(1L);
        atencionMedica.setFechaAtencion(LocalDateTime.now());
        atencionMedica.setMotivoConsulta("Dolor de cabeza");
        atencionMedica.setDiagnostico("Migraña");
        atencionMedica.setTratamiento("Reposo y medicamentos");
        atencionMedica.setEstado("Realizada");
    }

    @Test
    void cuandoObtenerTodasLasAtenciones_entoncesRetornarLista() {
        // Given
        List<AtencionMedica> atenciones = Arrays.asList(atencionMedica);
        when(atencionMedicaRepository.findAll()).thenReturn(atenciones);

        // When
        List<AtencionMedica> resultado = atencionMedicaService.getAllAtenciones();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(atencionMedicaRepository, times(1)).findAll();
    }

    @Test
    void cuandoObtenerAtencionPorIdExistente_entoncesRetornarAtencion() {
        // Given
        when(atencionMedicaRepository.findById(1L)).thenReturn(Optional.of(atencionMedica));

        // When
        Optional<AtencionMedica> resultado = atencionMedicaService.getAtencionById(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Dolor de cabeza", resultado.get().getMotivoConsulta());
        verify(atencionMedicaRepository, times(1)).findById(1L);
    }

    @Test
    void cuandoCrearAtencionValida_entoncesGuardarYRetornarAtencion() {
        // Given
        when(atencionMedicaRepository.save(any(AtencionMedica.class))).thenReturn(atencionMedica);

        // When
        AtencionMedica resultado = atencionMedicaService.createAtencion(atencionMedica);

        // Then
        assertNotNull(resultado);
        assertEquals("Realizada", resultado.getEstado());
        verify(atencionMedicaRepository, times(1)).save(atencionMedica);
    }

    @Test
    void cuandoActualizarAtencionExistente_entoncesRetornarAtencionActualizada() {
        // Given
        AtencionMedica atencionActualizada = new AtencionMedica();
        atencionActualizada.setMotivoConsulta("Dolor actualizado");
        atencionActualizada.setEstado("Cancelada");

        when(atencionMedicaRepository.findById(1L)).thenReturn(Optional.of(atencionMedica));
        when(atencionMedicaRepository.save(any(AtencionMedica.class))).thenReturn(atencionMedica);

        // When
        AtencionMedica resultado = atencionMedicaService.updateAtencion(1L, atencionActualizada);

        // Then
        assertNotNull(resultado);
        verify(atencionMedicaRepository, times(1)).findById(1L);
        verify(atencionMedicaRepository, times(1)).save(atencionMedica);
    }

    @Test
    void cuandoEliminarAtencionExistente_entoncesRetornarTrue() {
        // Given
        when(atencionMedicaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(atencionMedicaRepository).deleteById(1L);

        // When
        boolean resultado = atencionMedicaService.deleteAtencion(1L);

        // Then
        assertTrue(resultado);
        verify(atencionMedicaRepository, times(1)).existsById(1L);
        verify(atencionMedicaRepository, times(1)).deleteById(1L);
    }

    @Test
    void cuandoEliminarAtencionNoExistente_entoncesRetornarFalse() {
        // Given
        when(atencionMedicaRepository.existsById(999L)).thenReturn(false);

        // When
        boolean resultado = atencionMedicaService.deleteAtencion(999L);

        // Then
        assertFalse(resultado);
        verify(atencionMedicaRepository, times(1)).existsById(999L);
        verify(atencionMedicaRepository, never()).deleteById(any());
    }
}