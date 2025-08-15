package com.example.DIVE2025.domain.login.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {

    private Long id;
    private String email;
    private String refresh;
    private LocalDateTime createdAt;
    private LocalDateTime expiration;
}
