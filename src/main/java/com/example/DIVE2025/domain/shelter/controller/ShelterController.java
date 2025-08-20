package com.example.DIVE2025.domain.shelter.controller;

import com.example.DIVE2025.domain.shelter.dto.RecommendRequestDto;
import com.example.DIVE2025.domain.shelter.dto.RecommendResponseDto;
import com.example.DIVE2025.domain.shelter.service.ShelterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shelter")
public class ShelterController {

    private final ShelterService shelterService;

    @Autowired
    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping("/recommend-for-rescued")
    public ResponseEntity<?> recommendForRescued(RecommendRequestDto recommendRequestDto) {
        List<RecommendResponseDto> shelterPriority = shelterService.findShelterPriority(recommendRequestDto);
        log.info("shelter priority: {}", shelterPriority);
        return ResponseEntity.ok(shelterPriority);
    }
}
