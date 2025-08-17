package com.example.DIVE2025.domain.shelter.entity;

import com.example.DIVE2025.domain.shelter.enums.ShelterFeature;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shelter {
    private Long id;
    private String username;
    private String password;
    private String tel;
    private String description;
    private Double longitude;
    private Double latitude;
    private ShelterFeature shelterFeature;
    private String addr;
    private LocalDateTime createdAt;
}
