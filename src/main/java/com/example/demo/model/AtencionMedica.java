package com.example.demo.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "atenciones_medicas")
public class AtencionMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atencion_seq")
    @SequenceGenerator(name = "atencion_seq", sequenceName = "atencion_medica_seq", allocationSize = 1)
    private Long id;
    
    @NotNull(message = "El ID del paciente es obligatorio")
    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;
    
    @NotNull(message = "El ID del médico es obligatorio")
    @Column(name = "medico_id", nullable = false)
    private Long medicoId;
    
    @NotNull(message = "La fecha de atención es obligatoria")
    @Column(name = "fecha_atencion", nullable = false)
    private LocalDateTime fechaAtencion;
    
    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(max = 500, message = "El motivo de consulta no puede exceder los 500 caracteres")
    @Column(name = "motivo_consulta", nullable = false, length = 500)
    private String motivoConsulta;
    
    @Size(max = 1000, message = "El diagnóstico no puede exceder los 1000 caracteres")
    @Column(name = "diagnostico", length = 1000)
    private String diagnostico;
    
    @Size(max = 1000, message = "El tratamiento no puede exceder los 1000 caracteres")
    @Column(name = "tratamiento", length = 1000)
    private String tratamiento;
    
    @Size(max = 1000, message = "Las observaciones no pueden exceder los 1000 caracteres")
    @Column(name = "observaciones", length = 1000)
    private String observaciones;
    
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "Programada|Realizada|Cancelada", message = "El estado debe ser: Programada, Realizada o Cancelada")
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    
    public AtencionMedica() {}
    
    public AtencionMedica(Long pacienteId, Long medicoId, LocalDateTime fechaAtencion, 
                         String motivoConsulta, String diagnostico, String tratamiento, 
                         String observaciones, String estado) {
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.fechaAtencion = fechaAtencion;
        this.motivoConsulta = motivoConsulta;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.estado = estado;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    
    public LocalDateTime getFechaAtencion() { return fechaAtencion; }
    public void setFechaAtencion(LocalDateTime fechaAtencion) { this.fechaAtencion = fechaAtencion; }
    
    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }
    
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}