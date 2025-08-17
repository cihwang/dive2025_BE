package com.example.DIVE2025.domain.transferRequest.controller;

import com.example.DIVE2025.domain.transferRequest.dto.TrSaveRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TrUpdateRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TransferRequestResponseDto;
import com.example.DIVE2025.domain.transferRequest.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tr-request")
public class TransferRequestController {

    private final TransferService transferService;

    public TransferRequestController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveRequest(@RequestBody TrSaveRequestDto dto) {
        try {
            int result = transferService.saveRequest(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result); // 201
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409
        }
    }

    @PostMapping("/update")
    public int updateRequest(@RequestBody TrUpdateRequestDto trUpdateRequestDto) {
        return transferService.updateRequest(trUpdateRequestDto);
    }

    @PostMapping("/delete")
    public int deleteRequest(@RequestParam("id") Long id) {
        return transferService.deleteRequest(id);
    }

    @GetMapping("/from-shelter")
    public List<TransferRequestResponseDto> getTrFromShelter(@RequestParam("shelterId") Long shelterId) {
        return transferService.getAllRequestsByFromShelterId(shelterId);
    }

    @GetMapping("/to-shelter")
    public List<TransferRequestResponseDto> getTrToShelter(@RequestParam("shelterId") Long shelterId) {
        return transferService.getAllRequestsByToShelterId(shelterId);
    }

}
