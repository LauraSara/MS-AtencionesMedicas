package com.example.demo.service;

import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PacienteService {
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAll();
    }
    
    public Optional<Paciente> getPacienteById(Long id) {
        return pacienteRepository.findById(id);
    }
    
    public Paciente createPaciente(Paciente paciente) {
        if (pacienteRepository.existsByRut(paciente.getRut())) {
            throw new RuntimeException("Ya existe un paciente con el RUT: " + paciente.getRut());
        }
        if (pacienteRepository.existsByCorreo(paciente.getCorreo())) {
            throw new RuntimeException("Ya existe un paciente con el correo: " + paciente.getCorreo());
        }
        return pacienteRepository.save(paciente);
    }
    
    public Paciente updatePaciente(Long id, Paciente pacienteDetails) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findById(id);
        
        if (optionalPaciente.isPresent()) {
            Paciente paciente = optionalPaciente.get();
            paciente.setNombre(pacienteDetails.getNombre());
            paciente.setEdad(pacienteDetails.getEdad());
            paciente.setGenero(pacienteDetails.getGenero());
            paciente.setTelefono(pacienteDetails.getTelefono());
            paciente.setCorreo(pacienteDetails.getCorreo());
            paciente.setDireccion(pacienteDetails.getDireccion());
            
            return pacienteRepository.save(paciente);
        }
        return null;
    }
    
    public boolean deletePaciente(Long id) {
        if (pacienteRepository.existsById(id)) {
            pacienteRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Optional<Paciente> getPacienteByRut(String rut) {
        return pacienteRepository.findByRut(rut);
    }
}