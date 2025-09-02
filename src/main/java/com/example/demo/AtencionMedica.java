
package com.example.demo;

public class AtencionMedica {
    private int id;
    private int pacienteId;
    private int medicoId;
    private String fechaAtencion;
    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private String estado; 
    // estados: "Programada", "Realizada", "Cancelada"

    public AtencionMedica(int id, int pacienteId, int medicoId, String fechaAtencion,
            String motivoConsulta, String diagnostico, String tratamiento,
            String observaciones, String estado) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.fechaAtencion = fechaAtencion;
        this.motivoConsulta = motivoConsulta;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public int getMedicoId() {
        return medicoId;
    }

    public String getFechaAtencion() {
        return fechaAtencion;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getEstado() {
        return estado;
    }

}