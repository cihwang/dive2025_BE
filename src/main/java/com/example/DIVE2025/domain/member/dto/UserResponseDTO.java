package com.example.DIVE2025.domain.member.dto;

import com.example.DIVE2025.domain.shelter.entity.Shelter;
import com.example.DIVE2025.domain.shelter.enums.ShelterFeature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long shelterId;       // 보호소 고유 번호 (PK)
    private String username;      // 로그인 ID
    private String description;   // 보호소 이름/설명
    private String tel;           // 전화번호
    private String addr;          // 주소
    private Double latitude;      // 위도
    private Double longitude;     // 경도
    private ShelterFeature shelterFeature; // 보호소 특성 (예: HOSPITAL, NORMAL)

    // 엔티티 → DTO 변환
    public static UserResponseDTO fromEntity(Shelter s) {
        return UserResponseDTO.builder()
                .shelterId(s.getId())
                .username(s.getUsername())
                .description(s.getDescription())
                .tel(s.getTel())
                .addr(s.getAddr())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .shelterFeature(s.getShelterFeature())
                .build();
    }
}
