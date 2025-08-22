package com.example.DIVE2025.domain.transferRequest.service;

import com.example.DIVE2025.domain.transferRequest.Mapper.TransferMapper;
import com.example.DIVE2025.domain.transferRequest.dto.*;
import com.example.DIVE2025.domain.transferRequest.enums.RequestDecision;
import com.example.DIVE2025.domain.transferRequest.enums.RequestStatus;
import com.example.DIVE2025.domain.transporterRequest.dto.FindTransporterStoreNameDto;
import com.example.DIVE2025.domain.transporterRequest.enums.TprDecisionStatus;
import com.example.DIVE2025.domain.transporterRequest.mapper.TransportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferService {

    private final TransferMapper transferMapper;
    private final TransportMapper transportMapper;

    @Autowired
    public TransferService(TransferMapper transferMapper, TransportMapper transportMapper) {
        this.transferMapper = transferMapper;
        this.transportMapper = transportMapper;
    }

    /**
     * rescuedId, shelterId 받아 transferRequest 생성
     * 한 동물당 하나의 Request -> 추가하고 싶다면 기존 동물 request delete 필요
     */
    public int saveRequest(TrSaveRequestDto trSaveRequestDto) {
        Long targetRescued = trSaveRequestDto.getRescuedId();
        if(transferMapper.findTrRequestByRescuedId(targetRescued) == 1){
            throw new IllegalStateException("request rescuedId already exist");
        }
        return transferMapper.saveTransferRequest(trSaveRequestDto);
    }

    /**
     * 받는 보호소 기준 수락/거절 선택 기능
     */
    public int updateRequest(TrUpdateRequestDto dto) {
        if(RequestDecision.ACCEPTED.equals(dto.getRequestDecision())){
            dto.setRequestStatus(RequestStatus.TARGET_ACCEPTED);
        }else{
            dto.setRequestStatus(RequestStatus.TARGET_REJECTED);
        }

        return transferMapper.updateRequestStatus(dto);
    }

    public int deleteRequest(Long id) {
        return transferMapper.deleteTransferRequest(id);
    }

    /**
     * 보내는 보호소 기준 모든 Request 조회
     */
    public List<TransferRequestResponseDto> getAllRequestsByFromShelterId(Long shelterId) {
        List<TransferRequestResponseDto> transferByFromShelterId = transferMapper.getTransferByFromShelterId(shelterId);

        for (TransferRequestResponseDto dto : transferByFromShelterId) {
            if(dto.getTransporterId() != null){
                String storeName = transportMapper.getTransporterNameById(dto.getTransporterId()).getStoreName();
                dto.setTransporterName(storeName);
            }
        }

        return transferByFromShelterId;
    }

    /**
     * 받는 보호소 기준 모든 Request 조회
     */
    public List<TransferRequestResponseDto> getAllRequestsByToShelterId(Long shelterId) {

        List<TransferRequestResponseDto> transferByToShelterId = transferMapper.getTransferByToShelterId(shelterId);

        for (TransferRequestResponseDto dto : transferByToShelterId) {
            if(dto.getTransporterId() != null){
                String storeName = transportMapper.getTransporterNameById(dto.getTransporterId()).getStoreName();
                dto.setTransporterName(storeName);
            }
        }

        return transferByToShelterId;
    }

    public int updateTfrStatusByTpr(UpdateTfrStatusRequestByTprDto dto){
        TprDecisionStatus curStatus = dto.getTprDecisionStatus();

        if(curStatus.equals(TprDecisionStatus.ACCEPT)){
            UpdateTfrStatusResponseByTprDto updateData = UpdateTfrStatusResponseByTprDto.builder()
                    .id(dto.getId())
                    .transporterId(dto.getTransporterId())
                    .message(dto.getMessage())
                    .requestStatus(RequestStatus.TRANSPORTER_ACCEPTED)
                    .build();

            return transferMapper.updateRequestStatusByTpr(updateData);

        }else if(curStatus.equals(TprDecisionStatus.REJECT)){
            UpdateTfrStatusResponseByTprDto updateData = UpdateTfrStatusResponseByTprDto.builder()
                    .id(dto.getId())
                    .transporterId(dto.getTransporterId())
                    .message(dto.getMessage())
                    .requestStatus(RequestStatus.TRANSPORTER_REJECTED)
                    .build();

            return transferMapper.updateRequestStatusByTpr(updateData);
        }else return -1;
    }
}
