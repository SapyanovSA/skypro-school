package ru.hogwarts.school.student;

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


import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.service.FacultyService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTestRestTemplate {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentService studentService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    //Очистка БД
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @AfterEach
    public void clearDatabase() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/student";
    }

    @Test
    public void testCreateStudent() {
        Student student = new Student();
        student.setName("Гарри Поттер");
        student.setAge(11);

        ResponseEntity<Long> response = testRestTemplate.postForEntity(getBaseUrl(), student, Long.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody() > 0);
    }

    @Test
    public void testGetStudentInfo() {
        Student student = new Student();
        student.setName("Гермиона Грейнджер");
        student.setAge(12);

        Student savedStudent = studentService.addStudent(student);

        ResponseEntity<Student> response = testRestTemplate.getForEntity(
                getBaseUrl() + "/" + savedStudent.getId(),
                Student.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Гермиона Грейнджер", response.getBody().getName());
        Assertions.assertEquals(12, response.getBody().getAge());
    }

    @Test
    public void testEditStudent() {
        Student student = new Student();
        student.setName("Рон Уизли");
        student.setAge(11);
        Student savedStudent = studentService.addStudent(student);

        savedStudent.setName("Рональд Уизли");
        savedStudent.setAge(12);

        HttpEntity<Student> entity = new HttpEntity<>(savedStudent);
        ResponseEntity<Student> response = testRestTemplate.exchange(
                getBaseUrl(),
                HttpMethod.PUT,
                entity,
                Student.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Рональд Уизли", response.getBody().getName());
        Assertions.assertEquals(12, response.getBody().getAge());
    }

    @Test
    public void testDeleteStudent() {
        Student student = new Student();
        student.setName("Драко Малфой");
        student.setAge(11);
        Student savedStudent = studentService.addStudent(student);

        ResponseEntity<Void> responseDelete = testRestTemplate.exchange(
                getBaseUrl() + "/" + savedStudent.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseDelete.getStatusCode());

        ResponseEntity<Student> responseGet = testRestTemplate.getForEntity(
                getBaseUrl() + "/" + savedStudent.getId(),
                Student.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }

    @Test
    public void testFindStudentsByAge() {
        Student s1 = new Student();
        s1.setName("Седрик Диггори");
        s1.setAge(17);
        studentService.addStudent(s1);

        Student s2 = new Student();
        s2.setName("Полумна Лавгуд");
        s2.setAge(11);
        studentService.addStudent(s2);

        ResponseEntity<Student[]> response = testRestTemplate.getForEntity(
                getBaseUrl() + "?age=17",
                Student[].class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().length);
        Assertions.assertEquals("Седрик Диггори", response.getBody()[0].getName());
    }

    @Test
    public void testFindStudentsByAgeRange() {
        Student s1 = new Student(); s1.setName("Невилл Долгопупс"); s1.setAge(11); studentService.addStudent(s1);
        Student s2 = new Student(); s2.setName("Джинни Уизли"); s2.setAge(12); studentService.addStudent(s2);
        Student s3 = new Student(); s3.setName("Альбус Дамблдор"); s3.setAge(115); studentService.addStudent(s3);

        ResponseEntity<Student[]> response = testRestTemplate.getForEntity(
                getBaseUrl() + "/age-range?minAge=10&maxAge=15",
                Student[].class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().length);
    }

    @Test
    public void testGetFacultyByStudent() {
        Faculty gryffindor = new Faculty();
        gryffindor.setName("Гриффиндор");
        gryffindor.setColor("Красный");

        Faculty savedFaculty = facultyService.addFaculty(gryffindor);

        Student student = new Student();
        student.setName("Гарри Поттер");
        student.setAge(11);
        student.setFaculty(savedFaculty);
        Student savedStudent = studentService.addStudent(student);

        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(
                getBaseUrl() + "/" + savedStudent.getId() + "/faculty",
                Faculty.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Гриффиндор", response.getBody().getName());
    }
}