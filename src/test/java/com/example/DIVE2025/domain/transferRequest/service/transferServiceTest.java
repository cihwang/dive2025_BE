package com.example.DIVE2025.domain.transferRequest.service;

import com.example.DIVE2025.domain.transferRequest.dto.TrSaveRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TrUpdateRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TransferRequestResponseDto;
import com.example.DIVE2025.domain.transferRequest.enums.RequestDecision;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class transferServiceTest {

    @Autowired
    private final TransferService transferService;

    @Autowired
    transferServiceTest(TransferService transferService) {
        this.transferService = transferService;
    }

    @Test
    void save(){
        TrSaveRequestDto trSaveRequestDto = new TrSaveRequestDto();
        trSaveRequestDto.setRescuedId(2727L);
        trSaveRequestDto.setFromShelterId(1L);
        trSaveRequestDto.setToShelterId(2L);

        int i = transferService.saveRequest(trSaveRequestDto);

        Assertions.assertThat(i).isEqualTo(1);
    }

    @Test
    void update(){
        /**
         *     private Long trRequestId;
         *     private RequestDecision requestDecision;
         *     private RequestStatus requestStatus;
         *     private String message;
         */
        TrUpdateRequestDto trUpdateRequestDto = TrUpdateRequestDto.builder()
                .trRequestId(3L)
                .requestDecision(RequestDecision.ACCEPTED)
                .message("test message")
                .build();

        int i = transferService.updateRequest(trUpdateRequestDto);
        Assertions.assertThat(i).isEqualTo(1);
    }

    @Test
    void 타겟보호소로요청목록가져오기(){
        List<TransferRequestResponseDto> allRequestsByToShelterId = transferService.getAllRequestsByToShelterId(2L);
        List<TransferRequestResponseDto> allRequestsByFromShelterId = transferService.getAllRequestsByFromShelterId(1L);
        System.out.println(allRequestsByToShelterId);
        Assertions.assertThat(allRequestsByToShelterId).isEqualTo(allRequestsByFromShelterId);
    }
}