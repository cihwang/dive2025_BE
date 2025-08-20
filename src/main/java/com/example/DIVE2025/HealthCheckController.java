package com.example.DIVE2025;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @Value("${server.env}")
    private String env;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.serverAddress}")
    private String serverAddress;
    @Value("${serverName}")
    private String serverName;

    @GetMapping("/")
    public String test(){
        return "dive2025 API SERVER입니다.";
    }

    @GetMapping("/env")
    public ResponseEntity<?> getEnv(){
        return ResponseEntity.ok(env);
    }
}
