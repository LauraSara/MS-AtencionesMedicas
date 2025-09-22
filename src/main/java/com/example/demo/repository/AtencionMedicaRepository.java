package com.example.demo.repository;

import com.example.demo.model.AtencionMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtencionMedicaRepository extends JpaRepository<AtencionMedica, Long> {
    
    List<AtencionMedica> findByPacienteId(Long pacienteId);
    
    List<AtencionMedica> findByMedicoId(Long medicoId);
    
    List<AtencionMedica> findByEstado(String estado);
    
    @Query("SELECT COUNT(a) FROM AtencionMedica a WHERE a.estado = :estado")
    Long countByEstado(@Param("estado") String estado);
    
    @Query("SELECT a FROM AtencionMedica a WHERE a.pacienteId = :pacienteId AND a.estado = :estado")
    List<AtencionMedica> findByPacienteIdAndEstado(@Param("pacienteId") Long pacienteId, 
                                                  @Param("estado") String estado);
}