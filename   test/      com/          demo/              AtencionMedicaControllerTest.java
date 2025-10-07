import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AtencionMedicaController.class)
public class AtencionMedicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // Configuración inicial si es necesario
    }

    @Test
    public void updateAtencionExistente_entoncesRetornarAtencionActualizadaConEnlaces() throws Exception {
        mockMvc.perform(put("/atencion/1")
                .contentType("application/json")
                .content("{\"campo\":\"valorActualizado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campo").value("valorActualizado"));
    }

    @Test
    public void createAtencionSinPacienteId_entoncesRetornarBadRequestConEnlaces() throws Exception {
        mockMvc.perform(post("/atencion")
                .contentType("application/json")
                .content("{\"campo\":\"valor\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}