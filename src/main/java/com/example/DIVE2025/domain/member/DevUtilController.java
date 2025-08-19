package com.example.DIVE2025.domain.member;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev/util")
@Profile("local")
@RequiredArgsConstructor
public class DevUtilController {

    private final PasswordEncoder encoder;

    @GetMapping("/hash")
    public String hash(@RequestParam String raw) {
        return encoder.encode(raw);
    }
}
