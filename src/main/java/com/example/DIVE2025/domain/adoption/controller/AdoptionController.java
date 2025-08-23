package com.example.DIVE2025.domain.adoption.controller;

import com.example.DIVE2025.domain.adoption.dto.FindAdoptionListResponseDto;
import com.example.DIVE2025.domain.adoption.dto.RecommendAdoptCenterRequestDto;
import com.example.DIVE2025.domain.adoption.dto.RecommendAdoptCenterResponseDto;
import com.example.DIVE2025.domain.adoption.service.AdoptionService;
import com.example.DIVE2025.domain.transferRequest.dto.TrSaveRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TrUpdateRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TransferRequestResponseDto;
import com.example.DIVE2025.domain.transferRequest.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/adoption")
public class AdoptionController {

    private final AdoptionService adoptionService;
    private final TransferService transferService;

    @Autowired
    public AdoptionController(AdoptionService adoptionService, TransferService transferService) {
        this.adoptionService = adoptionService;
        this.transferService = transferService;
    }

    @GetMapping("/get-adoption-list")
    public ResponseEntity<?> getAdoption(@RequestParam("shelterId") Long shelterId) {
        List<FindAdoptionListResponseDto> adoptionListByShelterId = adoptionService.getAdoptionListByShelterId(shelterId);
        return ResponseEntity.ok(adoptionListByShelterId);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveRequest(@RequestBody TrSaveRequestDto dto) {
        try {
            int result = adoptionService.saveRequest(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result); // 201
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateRequest(@RequestBody TrUpdateRequestDto trUpdateRequestDto) {
        int i = transferService.updateRequest(trUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(i);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteRequest(@RequestParam("id") Long id) {
        int i = adoptionService.deleteRequest(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(i);
    }

    @GetMapping("/get-list")
    public ResponseEntity<?> getAdoptionList(@RequestParam("adoptionId") Long adoptionId) {
        List<TransferRequestResponseDto> allRequestsByAdoptionId = adoptionService.getAllRequestsByAdoptionId(adoptionId);
        return ResponseEntity.ok(allRequestsByAdoptionId);
    }

    @GetMapping("/get-recommend-adopt-center")
    public ResponseEntity<?> getRecommendAdoptCenter(RecommendAdoptCenterRequestDto recommendAdoptCenterRequestDto) {
        List<RecommendAdoptCenterResponseDto> adoptCenterByFromShelter = adoptionService.findAdoptCenterByFromShelter(recommendAdoptCenterRequestDto);
        log.info("AdoptionController");
        return ResponseEntity.ok(adoptCenterByFromShelter);
    }

}
