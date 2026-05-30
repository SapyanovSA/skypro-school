package ru.hogwarts.school.faculty;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTestRestTemplate {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Очищаем базу после каждого теста
    @AfterEach
    public void clearDatabase() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @Test
    public void testGetFacultyInfo() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффиндор");
        faculty.setColor("Красный");
        Faculty savedFaculty = facultyService.addFaculty(faculty);

        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(
                getBaseUrl() + "/" + savedFaculty.getId(),
                Faculty.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Гриффиндор", response.getBody().getName());
    }

    @Test
    public void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Слизерин");
        faculty.setColor("Зеленый");

        // Ваш FacultyController возвращает целый объект Faculty, а не Long id
        ResponseEntity<Faculty> response = testRestTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getId() > 0);
        Assertions.assertEquals("Слизерин", response.getBody().getName());
    }

    @Test
    public void testEditFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Пуффендуй");
        faculty.setColor("Желтый");
        Faculty savedFaculty = facultyService.addFaculty(faculty);

        savedFaculty.setName("Хаффлпафф");

        HttpEntity<Faculty> entity = new HttpEntity<>(savedFaculty);
        ResponseEntity<Faculty> response = testRestTemplate.exchange(
                getBaseUrl(),
                HttpMethod.PUT,
                entity,
                Faculty.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Хаффлпафф", response.getBody().getName());
    }

    @Test
    public void testDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Когтевран");
        faculty.setColor("Синий");
        Faculty savedFaculty = facultyService.addFaculty(faculty);

        ResponseEntity<Void> responseDelete = testRestTemplate.exchange(
                getBaseUrl() + "/" + savedFaculty.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        Assertions.assertEquals(HttpStatus.OK, responseDelete.getStatusCode());

        ResponseEntity<Faculty> responseGet = testRestTemplate.getForEntity(
                getBaseUrl() + "/" + savedFaculty.getId(),
                Faculty.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }

    @Test
    public void testFindFacultiesByColor() {
        Faculty f1 = new Faculty();
        f1.setName("Гриффиндор");
        f1.setColor("Красный");
        facultyService.addFaculty(f1);
        Faculty f2 = new Faculty();
        f2.setName("Слизерин");
        f2.setColor("Зеленый");
        facultyService.addFaculty(f2);

        ResponseEntity<Faculty[]> response = testRestTemplate.getForEntity(
                getBaseUrl() + "?color=Красный",
                Faculty[].class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().length);
        Assertions.assertEquals("Гриффиндор", response.getBody()[0].getName());
    }

    @Test
    public void testFindByNameOrColorIgnoreCase() {
        Faculty f1 = new Faculty();
        f1.setName("Гриффиндор");
        f1.setColor("Красный");
        facultyService.addFaculty(f1);
        Faculty f2 = new Faculty();
        f2.setName("Слизерин");
        f2.setColor("Зеленый");
        facultyService.addFaculty(f2);

        ResponseEntity<Faculty[]> response = testRestTemplate.getForEntity(
                getBaseUrl() + "/filter?search=ФИН",
                Faculty[].class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().length);
        Assertions.assertEquals("Гриффиндор", response.getBody()[0].getName());
    }

    @Test
    public void testGetStudentsByFaculty() {
        Faculty gryffindor = new Faculty();
        gryffindor.setName("Гриффиндор");
        gryffindor.setColor("Красный");
        Faculty savedFaculty = facultyService.addFaculty(gryffindor);

        Student student = new Student();
        student.setName("Гарри");
        student.setAge(11);
        student.setFaculty(savedFaculty); // Привязываем к факультету
        studentService.addStudent(student);

        ResponseEntity<Student[]> response = testRestTemplate.getForEntity(
                getBaseUrl() + "/" + savedFaculty.getId() + "/students",
                Student[].class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().length);
        Assertions.assertEquals("Гарри", response.getBody()[0].getName());
    }
}

