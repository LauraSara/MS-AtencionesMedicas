package com.example.demo.service;

import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicoService {
    
    @Autowired
    private MedicoRepository medicoRepository;
    
    public List<Medico> getAllMedicos() {
        return medicoRepository.findAll();
    }
    
    public Optional<Medico> getMedicoById(Long id) {
        return medicoRepository.findById(id);
    }
    
    public Medico createMedico(Medico medico) {
        if (medicoRepository.existsByRut(medico.getRut())) {
            throw new RuntimeException("Ya existe un médico con el RUT: " + medico.getRut());
        }
        if (medicoRepository.existsByCorreo(medico.getCorreo())) {
            throw new RuntimeException("Ya existe un médico con el correo: " + medico.getCorreo());
        }
        return medicoRepository.save(medico);
    }
    
    public Medico updateMedico(Long id, Medico medicoDetails) {
        Optional<Medico> optionalMedico = medicoRepository.findById(id);
        
        if (optionalMedico.isPresent()) {
            Medico medico = optionalMedico.get();
            medico.setNombre(medicoDetails.getNombre());
            medico.setEdad(medicoDetails.getEdad());
            medico.setGenero(medicoDetails.getGenero());
            medico.setTelefono(medicoDetails.getTelefono());
            medico.setCorreo(medicoDetails.getCorreo());
            medico.setDireccion(medicoDetails.getDireccion());
            medico.setEspecialidad(medicoDetails.getEspecialidad());
            
            return medicoRepository.save(medico);
        }
        return null;
    }
    
    public boolean deleteMedico(Long id) {
        if (medicoRepository.existsById(id)) {
            medicoRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Optional<Medico> getMedicoByRut(String rut) {
        return medicoRepository.findByRut(rut);
    }
}