package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MedicoController {

        private List<Medico> Medicos = new ArrayList<>();

        public MedicoController() {
                Medicos.add(new Medico(1, "12345678-9", "Dr. Carlos M.", 45, "M", 912345678, "carlos@clinica.com",
                                "Av. 123",
                                "Cardiología"));
                Medicos.add(new Medico(2, "23456789-0", "Dra. Ana L.", 38, "F", 923456789, "ana@hospital.com",
                                "Calle 456",
                                "Pediatría"));
                Medicos.add(new Medico(3, "34567890-1", "Dr. Roberto S.", 52, "M", 934567890, "roberto@salud.cl",
                                "Pasaje 789",
                                "Traumatología"));
                Medicos.add(new Medico(4, "45678901-2", "Dra. Marta G.", 41, "F", 945678901, "marta@Medicos.cl",
                                "Plaza 101",
                                "Ginecología"));
                Medicos.add(new Medico(5, "56789012-3", "Dr. Javier R.", 36, "M", 956789012, "javier@clinica.com",
                                "Av. 202",
                                "Dermatología"));
                Medicos.add(new Medico(6, "67890123-4", "Dra. Claudia V.", 48, "F", 967890123, "claudia@hospital.com",
                                "Calle 303", "Oftalmología"));
                Medicos.add(new Medico(7, "78901234-5", "Dr. Miguel T.", 43, "M", 978901234, "miguel@salud.cl",
                                "Pasaje 404",
                                "Neurología"));
        }

        @GetMapping("/medicos")
        public List<Medico> getMedicos() {
                return Medicos;
        }

        @GetMapping("/medicos/{id}")
        public Medico getMedicoById(@PathVariable int id) {
                for (Medico medico : Medicos) {
                        if (medico.getId() == id) {
                                return medico;
                        }
                }

                return null;
        }

}