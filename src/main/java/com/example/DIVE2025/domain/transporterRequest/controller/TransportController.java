package com.example.DIVE2025.domain.transporterRequest.controller;

import com.example.DIVE2025.domain.transporterRequest.dto.*;
import com.example.DIVE2025.domain.transporterRequest.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transport")
public class TransportController {

    private final TransportService transportService;

    @Autowired
    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping("/recommend-transporter")
    public ResponseEntity<?> findAllRecommendedTransporter(RecommendTransporterRequestDto recommendTransporterRequestDto) {
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
    public ResponseEntity<?> deleteTransportRequest(@RequestParam("id") Long id) {
        int i = transportService.deleteTransportRequest(id);
        return ResponseEntity.ok(i);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllTransportRequest(@RequestParam("id") Long id) {
        List<TprListResponseDto> allRequestsByTransporterId = transportService.getAllRequestsByTransporterId(id);
        return ResponseEntity.ok(allRequestsByTransporterId);
    }
}
