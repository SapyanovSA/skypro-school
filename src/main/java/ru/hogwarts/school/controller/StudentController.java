package ru.hogwarts.school.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public Student getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        return student;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.addStudent(student);
        return createdStudent.getId();
    }

    @PutMapping
    public Student editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
        }
        return foundStudent;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping
    public Collection<Student> findStudents(@RequestParam(required = false, defaultValue = "0") int age) {
        if (age > 0) {
            return studentService.findByAge(age);
        }
        return Collections.emptyList();
    }

    @GetMapping("/age-range")
    public Collection<Student> findStudentsByAgeRange(@RequestParam int minAge, @RequestParam int maxAge) {
        if (minAge < 0 || minAge > maxAge) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return studentService.findByAgeBetween(minAge, maxAge);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getFacultyByStudent(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return student.getFaculty();
    }
}