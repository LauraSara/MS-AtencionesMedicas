package com.example.demo.service;

import com.example.demo.model.AtencionMedica;
import com.example.demo.repository.AtencionMedicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AtencionMedicaService {
    
    @Autowired
    private AtencionMedicaRepository atencionMedicaRepository;
    
    public List<AtencionMedica> getAllAtenciones() {
        return atencionMedicaRepository.findAll();
    }
    
    public Optional<AtencionMedica> getAtencionById(Long id) {
        return atencionMedicaRepository.findById(id);
    }
    
    public AtencionMedica createAtencion(AtencionMedica atencionMedica) {
        return atencionMedicaRepository.save(atencionMedica);
    }
    
    public AtencionMedica updateAtencion(Long id, AtencionMedica atencionDetails) {
        Optional<AtencionMedica> optionalAtencion = atencionMedicaRepository.findById(id);
        
        if (optionalAtencion.isPresent()) {
            AtencionMedica atencion = optionalAtencion.get();
            atencion.setPacienteId(atencionDetails.getPacienteId());
            atencion.setMedicoId(atencionDetails.getMedicoId());
            atencion.setFechaAtencion(atencionDetails.getFechaAtencion());
            atencion.setMotivoConsulta(atencionDetails.getMotivoConsulta());
            atencion.setDiagnostico(atencionDetails.getDiagnostico());
            atencion.setTratamiento(atencionDetails.getTratamiento());
            atencion.setObservaciones(atencionDetails.getObservaciones());
            atencion.setEstado(atencionDetails.getEstado());
            
            return atencionMedicaRepository.save(atencion);
        }
        return null;
    }
    
    public boolean deleteAtencion(Long id) {
        if (atencionMedicaRepository.existsById(id)) {
            atencionMedicaRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<AtencionMedica> getAtencionesByPacienteId(Long pacienteId) {
        return atencionMedicaRepository.findByPacienteId(pacienteId);
    }
    
    public List<AtencionMedica> getAtencionesByMedicoId(Long medicoId) {
        return atencionMedicaRepository.findByMedicoId(medicoId);
    }
    
    public List<AtencionMedica> getAtencionesByEstado(String estado) {
        return atencionMedicaRepository.findByEstado(estado);
    }
    
    public Long countAtencionesByEstado(String estado) {
        return atencionMedicaRepository.countByEstado(estado);
    }
}