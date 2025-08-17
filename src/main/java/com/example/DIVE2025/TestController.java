package com.example.DIVE2025;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Good?!";
    }

    @GetMapping("/api/secure")
    public String secure() {
        return "Secure!";
    }
}
