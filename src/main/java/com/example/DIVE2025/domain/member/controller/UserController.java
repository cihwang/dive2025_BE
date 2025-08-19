package com.example.DIVE2025.domain.member.controller;

import com.example.DIVE2025.domain.member.dto.UserResponseDTO;
import com.example.DIVE2025.domain.member.service.UserService;
import com.example.DIVE2025.domain.shelter.entity.Shelter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class UserController {

    private final UserService userService;

    /** username(로그인 ID) 중복 확인 */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.checkUsernameDuplicate(username));
    }

    /** 회원가입 (보호소 등록) */
    @PostMapping("/join")
    public ResponseEntity<UserResponseDTO> join(@RequestBody Shelter shelter) {
        log.info("보호소 회원가입 요청 - username: {}", shelter.getUsername());
        Shelter saved = userService.join(shelter);
        return ResponseEntity.ok(UserResponseDTO.fromEntity(saved));
    }

    /** 로그인된 보호소 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyInfo(@RequestHeader("Authorization") String token) {
        UserResponseDTO dto = userService.getMyInfo(token);
        return ResponseEntity.ok(dto);
    }
}
