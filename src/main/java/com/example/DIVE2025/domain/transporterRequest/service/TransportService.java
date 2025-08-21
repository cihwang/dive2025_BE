package com.example.DIVE2025.domain.transporterRequest.service;

import com.example.DIVE2025.domain.shelter.dto.GetUsernameRequestDto;
import com.example.DIVE2025.domain.shelter.mapper.ShelterMapper;
import com.example.DIVE2025.domain.transferRequest.Mapper.TransferMapper;
import com.example.DIVE2025.domain.transferRequest.dto.UpdateTfrStatusRequestByTprDto;
import com.example.DIVE2025.domain.transferRequest.service.TransferService;
import com.example.DIVE2025.domain.transporterRequest.dto.*;
import com.example.DIVE2025.domain.transporterRequest.entity.TransportRequest;
import com.example.DIVE2025.domain.transporterRequest.mapper.TransportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransportService {

    private final TransportMapper transportMapper;
    private final ShelterMapper shelterMapper;
    private final TransferMapper transferMapper;
    private final TransferService transferService;

    @Autowired
    public TransportService(TransportMapper transportMapper, ShelterMapper shelterMapper, TransferMapper transferMapper, TransferService transferService) {
        this.transportMapper = transportMapper;
        this.shelterMapper = shelterMapper;
        this.transferMapper = transferMapper;
        this.transferService = transferService;
    }

    /**
     * fromShelter 위치로 가까운 운송업체 리스트 반환
     */
    public List<RecommendTransporterResponseDto> findTransporterByFromShelter(RecommendTransporterRequestDto recommendTransporterRequestDto) {
        return transportMapper.recommendTransportId(recommendTransporterRequestDto);
    }

    public int saveTransportRequest(TransportRequestSaveDto dto) {

        FindResultDto isExist = transportMapper.findTransportRequestIdByTransferReqeustId(dto.getTransferRequestId());
        if(isExist != null){
            throw new IllegalStateException("transport request already exist");
        }

        TransportRequest entity = TransportRequest.builder()
                .transferRequestId(dto.getTransferRequestId())
                .transporterId(dto.getTransporterId())
                .fromShelterName(shelterMapper.getUsernameById(new GetUsernameRequestDto(dto.getFromShelterId())).getUsername())
                .toShelterName(shelterMapper.getUsernameById(new GetUsernameRequestDto(dto.getToShelterId())).getUsername())
                .message(dto.getMessage())
                .build();

        log.info("save transport-request: {}", entity.toString());
        return transportMapper.saveTransportRequest(entity);
    }

    public int updateTransportRequest(UpdateTprRequestDto dto) {
        int i = transportMapper.updateTransportRequestStatus(dto);

        if(i != 1){
            throw new IllegalStateException("update transport request status failed");
        }

        UpdateTfrStatusRequestByTprDto updateDto = UpdateTfrStatusRequestByTprDto.builder()
                .id(dto.getTransferRequestId())
                .tprDecisionStatus(dto.getDecisionStatus())
                .build();

        int j = transferService.updateTfrStatusByTpr(updateDto);
        if(j != 1){
            throw new IllegalStateException("update transfer request status (by Transport Decision status)failed(2)");
        }

        return j;
    }

    public int deleteTransportRequest(Long transferRequestId) {
        return transportMapper.deleteTransportRequest(transferRequestId);
    }

    public List<TprListResponseDto> getAllRequestsByTransporterId(Long transporterId) {
        return transportMapper.getAllRequestByTransporterId(transporterId);
    }


}
