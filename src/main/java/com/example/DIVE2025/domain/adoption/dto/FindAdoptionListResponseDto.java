package com.example.DIVE2025.domain.adoption.dto;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindAdoptionListResponseDto {
    private Long id;
    private Long shelterId;
    private String desertionNo;
    private String parseAge;
    private String parseWeight;
    private String sex;
    private AnimalCondition animalCondition;
}
