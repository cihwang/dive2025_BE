package com.example.DIVE2025.domain.adoption.controller;

import com.example.DIVE2025.domain.adoption.dto.RecommendAdoptCenterRequestDto;
import com.example.DIVE2025.domain.adoption.dto.RecommendAdoptCenterResponseDto;
import com.example.DIVE2025.domain.adoption.service.AdoptionService;
import com.example.DIVE2025.domain.transferRequest.dto.TrSaveRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TrUpdateRequestDto;
import com.example.DIVE2025.domain.transferRequest.enums.RequestDecision;
import com.example.DIVE2025.domain.transferRequest.service.TransferService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdoptionControllerTest {

    @Autowired
    private AdoptionController adoptionController;
    @Autowired
    private AdoptionService adoptionService;
    @Autowired
    private TransferService transferService;

    @Test
    @DisplayName("save")
    void save() {

        TrSaveRequestDto trSaveRequestDto = TrSaveRequestDto.builder()
                .fromShelterId(1L)
                .toShelterId(7L)
                .rescuedId(2733L)
                .build();

        int i = adoptionService.saveRequest(trSaveRequestDto);
        Assertions.assertThat(i).isEqualTo(1);
    }

    @Test
    @DisplayName("update")
    void update() {

        TrUpdateRequestDto trUpdateRequestDto = TrUpdateRequestDto.builder()
                .trRequestId(12L)
                .message("슝우우ㅜ우ㅜㅅ ")
                .requestDecision(RequestDecision.ACCEPTED)
                .build();
        int i = transferService.updateRequest(trUpdateRequestDto);
        Assertions.assertThat(i).isEqualTo(1);
    }

    @Test
    @DisplayName("delete")
    void delete() {

        int i = adoptionService.deleteRequest(12L);
        Assertions.assertThat(i).isEqualTo(1);
    }

    @Test
    @DisplayName("종영구하기")
    void getList(){
        RecommendAdoptCenterRequestDto recommendAdoptCenterRequestDto = RecommendAdoptCenterRequestDto.builder()
                .fromShelterLatitude(35.151443)
                .fromShelterLongitude(128.93962)
                .build();

        for (RecommendAdoptCenterResponseDto recommendAdoptCenterResponseDto : (List<RecommendAdoptCenterResponseDto>) adoptionController.getRecommendAdoptCenter(recommendAdoptCenterRequestDto).getBody()) {
            System.out.println(recommendAdoptCenterResponseDto);
        }


    }
}