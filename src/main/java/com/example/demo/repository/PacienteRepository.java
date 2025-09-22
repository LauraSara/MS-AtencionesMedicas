package com.example.demo.repository;

import com.example.demo.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    
    Optional<Paciente> findByRut(String rut);
    
    Optional<Paciente> findByCorreo(String correo);
    
    boolean existsByRut(String rut);
    
    boolean existsByCorreo(String correo);
}