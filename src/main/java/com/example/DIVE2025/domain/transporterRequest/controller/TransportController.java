package com.example.DIVE2025.domain.transporterRequest.controller;

import com.example.DIVE2025.domain.transporterRequest.dto.RecommendTransporterRequestDto;
import com.example.DIVE2025.domain.transporterRequest.dto.RecommendTransporterResponseDto;
import com.example.DIVE2025.domain.transporterRequest.dto.TransportRequestSaveDto;
import com.example.DIVE2025.domain.transporterRequest.dto.UpdateTprRequestDto;
import com.example.DIVE2025.domain.transporterRequest.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transport")
public class TransportController {

    private final TransportService transportService;

    @Autowired
    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping("/recommend-transporter")
    public ResponseEntity<?> findAllRecommendedTransporter(@RequestBody RecommendTransporterRequestDto recommendTransporterRequestDto) {
        List<RecommendTransporterResponseDto> transporterByFromShelter = transportService.findTransporterByFromShelter(recommendTransporterRequestDto);
        return ResponseEntity.ok(transporterByFromShelter);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveTransportRequest(@RequestBody TransportRequestSaveDto transportRequestSaveDto) {
        int i = transportService.saveTransportRequest(transportRequestSaveDto);
        return ResponseEntity.ok(i);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateTransportRequest(@RequestBody UpdateTprRequestDto updateTprRequestDto) {
        int i = transportService.updateTransportRequest(updateTprRequestDto);
        return ResponseEntity.ok(i);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteTransportRequest(@RequestParam Long id) {
        int i = transportService.deleteTransportRequest(id);
        return ResponseEntity.ok(i);
    }
}
