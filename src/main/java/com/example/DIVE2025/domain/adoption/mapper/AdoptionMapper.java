package com.example.DIVE2025.domain.adoption.mapper;

import com.example.DIVE2025.domain.adoption.dto.FindAdoptionListResponseDto;
import com.example.DIVE2025.domain.transferRequest.dto.TransferRequestResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdoptionMapper {

    // shelterId로 normal인 동물들만 가져오기
    List<FindAdoptionListResponseDto> findAdoptionListByShelterId(Long shelterId);

    List<TransferRequestResponseDto> getTransferByAdoptionId(@Param("adoptionId") Long fromShelterId);
}
