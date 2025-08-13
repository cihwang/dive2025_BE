package com.example.DIVE2025.global.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String test(){
        return "dive2025 API SERVER입니다.";
    }
}
