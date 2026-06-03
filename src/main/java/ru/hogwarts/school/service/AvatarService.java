package ru.hogwarts.school.service;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

@Service
@Transactional
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar findAvatar(long studentId) {
        logger.info("Was invoked method for find avatar");
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(null);
        if (avatar == null) {
            logger.warn("Avatar for student id={} not found", studentId);
        }
        return avatar;
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar");

        if (!studentRepository.existsById(studentId)) {
            logger.error("No student with id=" + studentId);
            throw new StudentNotFoundException("Student with id " + studentId + " not found");
        }
        Student student = studentRepository.findById(studentId).get();

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (
                InputStream is = file.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            logger.debug("Transferring bytes to file system");
            is.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
        logger.info("Avatar for student id={} successfully saved", studentId);
    }

    private String getExtension(String fileName) {
        logger.debug("Was invoked method for get extension of file: {}", fileName);
        if (fileName == null){
            logger.warn("File name is null");
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public List<Avatar> getAllAvatars(int pageNumber, int pageSize) {
        logger.info("Was invoked method for get all avatars with pagination");
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}