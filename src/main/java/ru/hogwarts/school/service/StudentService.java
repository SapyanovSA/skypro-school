package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for add student");
        logger.debug("Payload for creating student: {}", student);
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Was invoked method for find student");
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            logger.warn("Student with id={} not found in database", id);
        }
        return student;
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student");
        if (studentRepository.existsById(student.getId())) {
            return studentRepository.save(student);
        }
        logger.error("Failed to edit student: student with id=" + student.getId() + " does not exist");
        return null;
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student");
        if (!studentRepository.existsById(id)) {
            logger.error("No student with id=" + id);
            throw new StudentNotFoundException("Студент с id " + id + " не найден");
        }
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Was invoked method for find students by age");
        return studentRepository.findByAge(age);
    }

    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for find students by age between");
        logger.debug("Age range: from {} to {}", minAge, maxAge);
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Integer getStudentsCount() {
        logger.info("Was invoked method for get students count");
        return studentRepository.getStudents();
    }

    public Double getAverageAge() {
        logger.info("Was invoked method for get average age of students");
        return studentRepository.getAVGByAge();
    }

    public Collection<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        return studentRepository.get5BackStudent();
    }

    public Collection<String> allStudentsThatBeginWithAToUpperCase(){
        return studentRepository.findAll()
                .stream()
                .map(Student::getName)
                .filter(n -> n != null && n.toUpperCase().startsWith("A"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    public Double AVGStudents() {
        return studentRepository.findAll()
                .stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }
}
