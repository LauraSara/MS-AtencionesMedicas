package com.example.demo.repository;

import com.example.demo.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    
    Optional<Medico> findByRut(String rut);
    
    Optional<Medico> findByCorreo(String correo);
    
    boolean existsByRut(String rut);
    
    boolean existsByCorreo(String correo);
}