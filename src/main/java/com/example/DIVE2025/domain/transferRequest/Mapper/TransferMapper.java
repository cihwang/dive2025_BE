package com.example.DIVE2025.domain.transferRequest.Mapper;

import com.example.DIVE2025.domain.transferRequest.dto.TrSaveRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TrUpdateRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TransferRequestResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransferMapper {

    /**
     * TransferRequest 저장
     */
    int saveTransferRequest(TrSaveRequestDto trSaveRequestDto);

    /**
     *  shelter_id 기준 request 목록 가져오기(발신 보호소, 수신 보호소)
     */
    List<TransferRequestResponseDto> getTransferByFromShelterId(@Param("fromShelterId") Long fromShelterId);
    List<TransferRequestResponseDto> getTransferByToShelterId(@Param("toShelterId") Long toShelterId);

    int updateRequestStatus(TrUpdateRequestDto trUpdateRequestDto);
    int deleteTransferRequest(@Param("id") Long id);
    int findTrRequestByRescuedId(@Param("rescuedId") Long rescuedId);

}
