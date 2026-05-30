package ru.hogwarts.school.student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    public void testCreateStudent() throws Exception {
        Student student = new Student();
        student.setName("Гарри Поттер");
        student.setAge(11);

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Гарри Поттер");
        savedStudent.setAge(11);
        Mockito.when(studentService.addStudent(Mockito.any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    public void testGetStudentInfo() throws Exception {
        Long id = 1L;
        Student student = new Student();
        student.setId(id);
        student.setName("Гермиона Грейнджер");
        student.setAge(12);

        Mockito.when(studentService.findStudent(id)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Гермиона Грейнджер"))
                .andExpect(jsonPath("$.age").value(12));
    }

    @Test
    public void testGetStudentInfoNotFound() throws Exception {
        Long id = 999L;

        Mockito.when(studentService.findStudent(id)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEditStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Рональд Уизли");
        student.setAge(12);

        Mockito.when(studentService.editStudent(Mockito.any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Рональд Уизли"))
                .andExpect(jsonPath("$.age").value(12));
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Long id = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testFindStudentsByAge() throws Exception {
        int age = 17;
        Student student = new Student();
        student.setId(1L);
        student.setName("Седрик Диггори");
        student.setAge(age);

        Mockito.when(studentService.findByAge(age)).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .param("age", String.valueOf(age))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Седрик Диггори"));
    }

    @Test
    public void testFindStudentsByAgeRange() throws Exception {
        int minAge = 10;
        int maxAge = 15;

        Student student = new Student();
        student.setId(1L);
        student.setName("Невилл Долгопупс");
        student.setAge(11);

        Mockito.when(studentService.findByAgeBetween(minAge, maxAge)).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/age-range")
                        .param("minAge", String.valueOf(minAge))
                        .param("maxAge", String.valueOf(maxAge))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Невилл Долгопупс"));
    }

    @Test
    public void testGetFacultyByStudent() throws Exception {
        Long studentId = 1L;

        Faculty faculty = new Faculty();
        faculty.setId(10L);
        faculty.setName("Гриффиндор");
        faculty.setColor("Красный");

        Student student = new Student();
        student.setId(studentId);
        student.setName("Гарри Поттер");
        student.setAge(11);
        student.setFaculty(faculty);

        Mockito.when(studentService.findStudent(studentId)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + studentId + "/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }
}
