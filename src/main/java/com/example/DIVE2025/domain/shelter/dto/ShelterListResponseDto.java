package com.example.DIVE2025.domain.shelter.dto;

import com.example.DIVE2025.domain.shelter.enums.ShelterFeature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShelterListResponseDto {
    private Long id;
    private String username;
    private String tel;
    private Double latitude;
    private Double longitude;
    private String addr;
    private ShelterFeature shelterFeature;
    private int totalCapacity;
    private int curCapacity;
}
