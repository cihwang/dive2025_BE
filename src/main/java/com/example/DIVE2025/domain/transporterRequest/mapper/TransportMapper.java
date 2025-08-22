package com.example.DIVE2025.domain.transporterRequest.mapper;

import com.example.DIVE2025.domain.transporterRequest.dto.*;
import com.example.DIVE2025.domain.transporterRequest.entity.TransportRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransportMapper {

    // from_shelter 기준 가까운 운송 업체 10개 추천
    List<RecommendTransporterResponseDto> recommendTransportId(RecommendTransporterRequestDto recommendTransporterRequestDto);

    // TransporterRequest 저장
    int saveTransportRequest(TransportRequest transportRequest);

    FindResultDto findTransportRequestIdByTransferReqeustId(@Param("transferRequestId") Long transferRequestId);
    int updateTransportRequestStatus(UpdateTprRequestDto updateTprRequestDto);
    int deleteTransportRequest(@Param("id") Long id);

    List<TprListResponseDto> getAllRequestByTransporterId(@Param("transporterId") Long transporterId);

    FindTransporterIdResponseDto getTransporterIdById(@Param("transportRequestId") Long transportRequestId);

    FindTransporterStoreNameDto getTransporterNameById(@Param("transporterId") Long transporterId);
}
