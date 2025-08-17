package com.example.DIVE2025.domain.shelter.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShelterCapacity {
    private Long id;
    private Long shelterId;
    private Long totalCapacity;
    private Long curCapacity;
    private LocalDateTime createdAt;
}
