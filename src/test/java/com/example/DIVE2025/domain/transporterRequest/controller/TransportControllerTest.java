package com.example.DIVE2025.domain.transporterRequest.controller;

import com.example.DIVE2025.domain.transporterRequest.dto.RecommendTransporterRequestDto;
import com.example.DIVE2025.domain.transporterRequest.dto.TransportRequestSaveDto;
import com.example.DIVE2025.domain.transporterRequest.dto.UpdateTprRequestDto;
import com.example.DIVE2025.domain.transporterRequest.enums.TprDecisionStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class TransportControllerTest {

    @Autowired
    private TransportController transportController;

    @Test
    @DisplayName("운송 업체 추천 기능")
    void recommendTransport() {
        RecommendTransporterRequestDto recommendTransporterRequestDto = RecommendTransporterRequestDto.builder()
                .fromShelterLatitude(35.151443)
                .toShelterLongitude(128.93962)
                .build();

        ResponseEntity<?> allRecommendedTransporter = transportController.findAllRecommendedTransporter(recommendTransporterRequestDto);
        Assertions.assertThat(allRecommendedTransporter.getBody()).isNotNull();
        System.out.println(allRecommendedTransporter.getBody());
    }

    @Test
    @DisplayName("TransportRequest 저장")
    void saveRecommendedTransporter() {
        TransportRequestSaveDto transportRequestSaveDto = TransportRequestSaveDto.builder()
                .transferRequestId(3L)
                .transporterId(1L)
                .fromShelterId(1L)
                .toShelterId(2L)
                .message("2025.08.20")
                .build();

        ResponseEntity<?> responseEntity = transportController.saveTransportRequest(transportRequestSaveDto);
        Assertions.assertThat(transportRequestSaveDto).isNotNull();
        System.out.println(responseEntity.getBody());
    }

    @Test
    @DisplayName("운송업체 결정 -> 이관 신청 상태 업데이트")
    void updateStatus(){

        UpdateTprRequestDto dto = UpdateTprRequestDto.builder()
                .id(1L)
                .transferRequestId(3L)
                .decisionStatus(TprDecisionStatus.ACCEPT)
                .build();

        ResponseEntity<?> responseEntity = transportController.updateTransportRequest(dto);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(1);
    }

    @Test
    @DisplayName("운송 신청 삭제")
    void delete(){
        ResponseEntity<?> responseEntity = transportController.deleteTransportRequest(1L);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(1);
    }
}