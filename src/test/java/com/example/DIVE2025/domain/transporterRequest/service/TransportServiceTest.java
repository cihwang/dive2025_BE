package com.example.DIVE2025.domain.transporterRequest.service;

import com.example.DIVE2025.domain.transporterRequest.dto.TransportRequestSaveDto;
import com.example.DIVE2025.domain.transporterRequest.dto.UpdateTprRequestDto;
import com.example.DIVE2025.domain.transporterRequest.enums.TprDecisionStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransportServiceTest {

    @Autowired
    private TransportService transportService;

    @Test
    @DisplayName("Transporter Request 저장 ")
    void saveTransportRequest() {

        TransportRequestSaveDto transportRequestSaveDto = TransportRequestSaveDto.builder()
                .transferRequestId(12L)
                .transporterId(1L)
                .message("이날까지 꼭 옮겨주세요")
                .fromShelterId(1L)
                .toShelterId(7L)
                .build();

        int i = transportService.saveTransportRequest(transportRequestSaveDto);
        Assertions.assertThat(i).isEqualTo(1);
    }

    @Test
    void updateTransportRequest() {

        UpdateTprRequestDto updateTprRequestDto = UpdateTprRequestDto.builder()
                .id(2L)
                .message("nononononononnono")
                .decisionStatus(TprDecisionStatus.REJECT)
                .transferRequestId(12L)
                .build();

        int i = transportService.updateTransportRequest(updateTprRequestDto);
        Assertions.assertThat(i).isEqualTo(1);

        System.out.println(updateTprRequestDto);
    }

    @Test
    void getAllRequestsByTransporterId() {
    }
}