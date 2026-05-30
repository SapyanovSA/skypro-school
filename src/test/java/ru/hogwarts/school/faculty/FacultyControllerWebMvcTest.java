package ru.hogwarts.school.faculty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService facultyService;

    @Test
    public void testGetFacultyInfo() throws Exception {
        Long id = 1L;
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName("Гриффиндор");
        faculty.setColor("Красный");

        Mockito.when(facultyService.findFaculty(id)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }

    @Test
    public void testGetFacultyInfoNotFound() throws Exception {
        Long id = 999L;
        Mockito.when(facultyService.findFaculty(id)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Слизерин");
        faculty.setColor("Зеленый");

        Faculty savedFaculty = new Faculty();
        savedFaculty.setId(2L);
        savedFaculty.setName("Слизерин");
        savedFaculty.setColor("Зеленый");

        Mockito.when(facultyService.addFaculty(Mockito.any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Слизерин"))
                .andExpect(jsonPath("$.color").value("Зеленый"));
    }

    @Test
    public void testEditFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Хаффлпафф");
        faculty.setColor("Желтый");

        Mockito.when(facultyService.editFaculty(Mockito.any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Хаффлпафф"));
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        Long id = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindFacultiesByColor() throws Exception {
        String color = "Красный";
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Гриффиндор");
        faculty.setColor(color);

        Mockito.when(facultyService.findByColor(color)).thenReturn(List.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .param("color", color)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

    @Test
    public void testFindByNameOrColorIgnoreCase() throws Exception {
        String search = "ФИН";
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Гриффиндор");
        faculty.setColor("Красный");

        Mockito.when(facultyService.findByNameOrColorIgnoreCase(search)).thenReturn(List.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter")
                        .param("search", search)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

    @Test
    public void testGetStudentsByFaculty() throws Exception {
        Long facultyId = 1L;

        Student student = new Student();
        student.setId(10L);
        student.setName("Гарри Поттер");
        student.setAge(11);

        Faculty faculty = new Faculty();
        faculty.setId(facultyId);
        faculty.setName("Гриффиндор");
        faculty.setColor("Красный");
        faculty.setStudents(Set.of(student));

        Mockito.when(facultyService.findFaculty(facultyId)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + facultyId + "/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"));
    }
}