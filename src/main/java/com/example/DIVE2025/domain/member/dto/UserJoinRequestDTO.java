package com.example.DIVE2025.domain.member.dto;

import com.example.DIVE2025.domain.shelter.entity.Shelter;
import com.example.DIVE2025.domain.shelter.enums.ShelterFeature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinRequestDTO {
    private String username;       // 로그인 ID
    private String password;       // 비밀번호
    private String description;    // 보호소 이름/설명
    private String tel;            // 전화번호
    private String addr;           // 주소
    private Double latitude;       // 위도
    private Double longitude;      // 경도
    private ShelterFeature shelterFeature; // 부가 속성(예: HOSPITAL, NORMAL 등)

    public Shelter toEntity(String encodedPassword) {
        return Shelter.builder()
                .username(this.username)
                .password(encodedPassword)
                .description(this.description)
                .tel(this.tel)
                .addr(this.addr)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .shelterFeature(this.shelterFeature)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
