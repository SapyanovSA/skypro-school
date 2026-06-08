package ru.hogwarts.school.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/port")
    public String getPort() {
        return serverPort;
    }

    // Эндпоинт для Шага 4 (Оптимизированное вычисление суммы)
    @GetMapping("/calculate-sum")
    public int getCalculatedSum() {
        return IntStream.rangeClosed(1, 1_000_000)
                .parallel()
                .sum();
    }
}
