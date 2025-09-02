package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AtencionMedicaController {

    private List<AtencionMedica> AtencionMedicas = new ArrayList<>();

    public AtencionMedicaController() {
        AtencionMedicas.add(new AtencionMedica(1, 1, 1, "2024-01-15 10:30",
                "Dolor de pecho y falta de aire", "Angina de pecho",
                "Reposo, nitroglicerina sublingual y control cardiológico",
                "Paciente con factores de riesgo cardiovascular", "Cancelada"));

        AtencionMedicas.add(new AtencionMedica(2, 2, 2, "2024-01-16 11:00",
                "Fiebre alta y dolor de garganta en niño", "Faringitis",
                "Amoxicilina por 10 días y paracetamol para la fiebre",
                "Niño de 5 años, buen estado general", "Realizada"));

        AtencionMedicas.add(new AtencionMedica(3, 3, 3, "2024-01-17 09:15",
                "Dolor en rodilla después de accidente deportivo", "Esguince",
                "Inmovilización, fisioterapia y control en 2 semanas",
                "Paciente futbolista amateur", "Cancelada"));

        AtencionMedicas.add(new AtencionMedica(4, 4, 4, "2024-01-18 14:00",
                "Control ginecológico anual", "Citología normal, salud ginecológica óptima",
                "Control anual, anticoncepción oral continuada",
                "Paciente de 28 años sin antecedentes", "Realizada"));

        AtencionMedicas.add(new AtencionMedica(5, 5, 5, "2024-01-19 16:30",
                "Erupción cutánea con picazón intensa", "Dermatitis",
                "Crema de corticoides tópicos y antihistamínicos orales",
                "Posible desencadenante: estrés laboral", "Realizada"));

        AtencionMedicas.add(new AtencionMedica(6, 6, 6, "2024-01-22 15:45",
                "Dificultad para ver objetos lejanos", "Miopía",
                "Prescripción de lentes -2.5 dioptrías",
                "Primera consulta oftalmológica del paciente", "Realizada"));

        AtencionMedicas.add(new AtencionMedica(7, 7, 7, "2024-01-23 11:20",
                "Dolores de cabeza recurrentes", "Migraña",
                "Sumatriptán para crisis y propranolol como preventivo",
                "Paciente con antecedentes familiares de migraña", "Programada"));

        AtencionMedicas.add(new AtencionMedica(8, 1, 1, "2024-02-01 09:00",
                "Control post angina de pecho", "Evolución favorable bajo tratamiento",
                "Continuar con medicación y estilo de vida cardiosaludable",
                "Paciente refiere mejoría significativa", "Realizada"));

        AtencionMedicas.add(new AtencionMedica(9, 2, 2, "2024-02-05 10:15",
                "Control de niño sano y vacunación", "Niño sano, desarrollo normal",
                "Aplicación de vacuna triple viral y refuerzo de DPT",
                "Padres informados sobre próximos controles", "Programada"));

        AtencionMedicas.add(new AtencionMedica(10, 3, 3, "2024-02-10 08:30",
                "Control de evolución de esguince", "Mejoría progresiva de la lesión",
                "Iniciar rehabilitación y ejercicios de fortalecimiento",
                "Paciente puede retornar gradualmente al deporte", "Programada"));
    }

    @GetMapping("/atencionesMedicas")
    public String getAtencionMedicas() {
        if (AtencionMedicas.isEmpty()) {
            return """
                        <div style='font-family: Arial; padding: 50px; text-align: center; color: #7f8c8d;'>
                            <h3>No hay atenciones médicas registradas</h3>
                        </div>
                    """;
        }

        StringBuilder html = new StringBuilder();
        html.append("""
                    <div style='font-family: Arial; padding: 20px;'>
                        <h3 style='color: #2c3e50;'>Atenciones Médicas (%d)</h3>
                """.formatted(AtencionMedicas.size()));

        for (AtencionMedica atencion : AtencionMedicas) {
            String estadoColor = switch (atencion.getEstado().toLowerCase()) {
                case "realizada" -> "green";
                case "programada" -> "orange";
                case "cancelada" -> "red";
                default -> "gray";
            };

            html.append(String.format("""
                        <div style='border: 1px solid #eee; padding: 15px; margin: 10px 0; border-radius: 5px;'>
                            <b>#%d</b> | <span style='color: %s;'>%s</span> | %s<br>
                            <small>Médico: %d | Paciente: %d</small><br>
                            <small><b>Motivo:</b> %s</small>
                        </div>
                    """,
                    atencion.getId(), estadoColor, atencion.getEstado(), atencion.getFechaAtencion(),
                    atencion.getMedicoId(), atencion.getPacienteId(), atencion.getMotivoConsulta()));
        }

        html.append("</div>");
        return html.toString();
    }

    @GetMapping("/atencionesMedicas/{id}")
    public String getAtencionMedicaById(@PathVariable int id) {
        for (AtencionMedica atencion : AtencionMedicas) {
            if (atencion.getId() == id) {
                String estadoColor = switch (atencion.getEstado().toLowerCase()) {
                    case "realizada" -> "#27ae60";
                    case "programada" -> "#f39c12";
                    case "cancelada" -> "#e74c3c";
                    default -> "#7f8c8d";
                };

                return String.format(
                        """
                                    <div style='font-family: Arial; padding: 20px; background: #f8f9fa; min-height: 100vh;'>
                                        <div style='max-width: 600px; margin: 0 auto;'>
                                            <div style='background: white; padding: 25px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>
                                                <div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;'>
                                                    <h2 style='color: #2c3e50; margin: 0;'>📋 Atención Médica #%d</h2>
                                                    <span style='background: %s; color: white; padding: 6px 12px; border-radius: 15px; font-size: 12px;'>
                                                        %s
                                                    </span>
                                                </div>

                                                <div style='border-top: 1px solid #eee; padding-top: 15px;'>
                                                    <div style='display: grid; gap: 12px;'>
                                                        <div style='display: flex; justify-content: space-between;'>
                                                            <span style='color: #7f8c8d;'><b>📅 Fecha:</b></span>
                                                            <span style='color: #2c3e50;'>%s</span>
                                                        </div>
                                                        <div style='display: flex; justify-content: space-between;'>
                                                            <span style='color: #7f8c8d;'><b>👨‍⚕️ Médico ID:</b></span>
                                                            <span style='color: #2c3e50;'>%d</span>
                                                        </div>
                                                        <div style='display: flex; justify-content: space-between;'>
                                                            <span style='color: #7f8c8d;'><b>👤 Paciente ID:</b></span>
                                                            <span style='color: #2c3e50;'>%d</span>
                                                        </div>
                                                    </div>

                                                    <div style='margin-top: 20px; padding: 15px; background: #f9f9f9; border-radius: 8px;'>
                                                        <h4 style='color: #2c3e50; margin: 0 0 10px 0;'>📝 Motivo de Consulta</h4>
                                                        <p style='color: #34495e; margin: 0;'>%s</p>
                                                    </div>

                                                    <div style='margin-top: 15px; padding: 15px; background: #e8f5e8; border-radius: 8px;'>
                                                        <h4 style='color: #27ae60; margin: 0 0 10px 0;'>🏥 Diagnóstico</h4>
                                                        <p style='color: #34495e; margin: 0;'>%s</p>
                                                    </div>

                                                    <div style='margin-top: 15px; padding: 15px; background: #e3f2fd; border-radius: 8px;'>
                                                        <h4 style='color: #3498db; margin: 0 0 10px 0;'>💊 Tratamiento</h4>
                                                        <p style='color: #34495e; margin: 0;'>%s</p>
                                                    </div>

                                                    <div style='margin-top: 15px; padding: 15px; background: #fef7e0; border-radius: 8px;'>
                                                        <h4 style='color: #f39c12; margin: 0 0 10px 0;'>📋 Observaciones</h4>
                                                        <p style='color: #34495e; margin: 0;'>%s</p>
                                                    </div>
                                                </div>
                                            </div>

                                            <div style='text-align: center; margin-top: 20px;'>
                                                <a href='/atencionesMedicas' style='color: #3498db; text-decoration: none; font-size: 14px;'>
                                                    ← Volver al listado de atenciones
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                """,
                        atencion.getId(),
                        estadoColor,
                        atencion.getEstado(),
                        atencion.getFechaAtencion(),
                        atencion.getMedicoId(),
                        atencion.getPacienteId(),
                        atencion.getMotivoConsulta(),
                        atencion.getDiagnostico(),
                        atencion.getTratamiento(),
                        atencion.getObservaciones());
            }
        }

        return """
                    <div style='font-family: Arial; padding: 50px; text-align: center; background: #f8f9fa; min-height: 100vh;'>
                        <div style='max-width: 400px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>
                            <span style='font-size: 48px; color: #e74c3c;'>❌</span>
                            <h2 style='color: #2c3e50;'>Atención no encontrada</h2>
                            <p style='color: #7f8c8d;'>No se encontró ninguna atención con el ID: %d</p>
                            <div style='margin-top: 20px;'>
                                <a href='/atencionesMedicas' style='color: #3498db; text-decoration: none;'>
                                    ← Volver al listado de atenciones
                                </a>
                            </div>
                        </div>
                    </div>
                """
                .formatted(id);
    }

    @GetMapping("/atencionesMedicas/paciente/{pacienteId}")
    public List<AtencionMedica> getAtencionesByPacienteId(@PathVariable int pacienteId) {
        List<AtencionMedica> resultado = new ArrayList<>();
        for (AtencionMedica atencionMedica : AtencionMedicas) {
            if (atencionMedica.getPacienteId() == pacienteId) {
                resultado.add(atencionMedica);
            }
        }
        return resultado;
    }

    @GetMapping("/atencionesMedicas/medico/{medicoId}")
    public List<AtencionMedica> getAtencionesByMedicoId(@PathVariable int medicoId) {
        List<AtencionMedica> resultado = new ArrayList<>();
        for (AtencionMedica atencionMedica : AtencionMedicas) {
            if (atencionMedica.getMedicoId() == medicoId) {
                resultado.add(atencionMedica);
            }
        }
        return resultado;
    }

    @GetMapping("/atencionesMedicas/estado/{estado}")
    public String getAtencionesByEstado(@PathVariable String estado) {
        List<AtencionMedica> resultado = new ArrayList<>();
        for (AtencionMedica atencionMedica : AtencionMedicas) {
            if (atencionMedica.getEstado().equalsIgnoreCase(estado)) {
                resultado.add(atencionMedica);
            }
        }

        StringBuilder html = new StringBuilder();
        html.append(String.format("""
                <div style='font-family: Arial; padding: 20px;'>
                    <h3 style='color: #2c3e50;'>Atenciones %s (%d)</h3>
                """, estado, resultado.size()));

        if (resultado.isEmpty()) {
            html.append("""
                        <p style='color: #7f8c8d;'>No hay atenciones con este estado.</p>
                    """);
        } else {
            for (AtencionMedica atencion : resultado) {
                html.append(String.format("""
                            <div style='border: 1px solid #ddd; padding: 10px; margin: 5px 0; border-radius: 5px;'>
                                <b>#%d</b> | %s<br>
                                <small>Médico: %d | Paciente: %d</small><br>
                                <small>Motivo: %s</small>
                            </div>
                        """,
                        atencion.getId(),
                        atencion.getFechaAtencion(),
                        atencion.getMedicoId(),
                        atencion.getPacienteId(),
                        atencion.getMotivoConsulta()));
            }
        }

        html.append("""
                    <br>
                    <a href='/atencionesMedicas' style='color: #3498db; text-decoration: none;'>
                        ← Volver a todas las atenciones
                    </a>
                    </div>
                """);

        return html.toString();
    }

    @GetMapping("/")
    public String getEstadisticas() {
        int total = AtencionMedicas.size();
        int realizadas = 0;
        int programadas = 0;
        int canceladas = 0;

        for (AtencionMedica atencion : AtencionMedicas) {
            switch (atencion.getEstado().toLowerCase()) {
                case "realizada":
                    realizadas++;
                    break;
                case "programada":
                    programadas++;
                    break;
                case "cancelada":
                    canceladas++;
                    break;
            }
        }

        return String.format("""
                <div style='font-family: Arial; padding: 20px; background: #f8f9fa;'>
                    <h2 style='color: #2c3e50;'>Estadísticas de Atenciones</h2>
                    <div style='background: white; padding: 20px; border-radius: 8px;'>
                        <p><b>Total:</b> <span font-size: 18px;'>%d</span></p>
                        <p><b>Realizadas:</b> <span style='color: #27ae60; font-size: 18px;'>%d</span></p>
                        <p><b>Programadas:</b> <span style='color: #f39c12; font-size: 18px;'>%d</span></p>
                        <p><b>Canceladas:</b> <span style='color: #e74c3c; font-size: 18px;'>%d</span></p>
                    </div>
                </div>
                """, total, realizadas, programadas, canceladas);
    }
}