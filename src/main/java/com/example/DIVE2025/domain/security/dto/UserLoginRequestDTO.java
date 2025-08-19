package com.example.DIVE2025.domain.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequestDTO {

    @JsonProperty("userId")
    private String userId; // 사용자 ID

    @JsonProperty("password")
    private String password; // 비밀번호

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

}
