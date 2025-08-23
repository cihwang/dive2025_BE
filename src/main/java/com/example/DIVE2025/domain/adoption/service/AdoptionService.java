package com.example.DIVE2025.domain.adoption.service;

import com.example.DIVE2025.domain.adoption.dto.FindAdoptionListResponseDto;
import com.example.DIVE2025.domain.adoption.dto.RecommendAdoptCenterRequestDto;
import com.example.DIVE2025.domain.adoption.dto.RecommendAdoptCenterResponseDto;
import com.example.DIVE2025.domain.adoption.mapper.AdoptionMapper;
import com.example.DIVE2025.domain.transferRequest.Mapper.TransferMapper;
import com.example.DIVE2025.domain.transferRequest.dto.TrSaveRequestDto;
import com.example.DIVE2025.domain.transferRequest.dto.TransferRequestResponseDto;
import com.example.DIVE2025.domain.transporterRequest.mapper.TransportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionService {

    private final AdoptionMapper adoptionMapper;
    private final TransferMapper transferMapper;
    private final TransportMapper transportMapper;

    @Autowired
    public AdoptionService(AdoptionMapper adoptionMapper, TransferMapper transferMapper, TransportMapper transportMapper) {
        this.adoptionMapper = adoptionMapper;
        this.transferMapper = transferMapper;
        this.transportMapper = transportMapper;

    }

    public List<RecommendAdoptCenterResponseDto> findAdoptCenterByFromShelter(RecommendAdoptCenterRequestDto recommendAdoptCenterRequestDto) {
        return adoptionMapper.recommendAdoptCenter(recommendAdoptCenterRequestDto);
    }

    public List<FindAdoptionListResponseDto> getAdoptionListByShelterId(Long shelterId) {
        return adoptionMapper.findAdoptionListByShelterId(shelterId);
    }

    public int saveRequest(TrSaveRequestDto trSaveRequestDto) {
        Long targetRescued = trSaveRequestDto.getRescuedId();
        if(transferMapper.findTrRequestByRescuedId(targetRescued) == 1){
            throw new IllegalStateException("request rescuedId already exist");
        }
        return transferMapper.saveTransferRequest(trSaveRequestDto);
    }

    public int deleteRequest(Long id) {
        return transferMapper.deleteTransferRequest(id);
    }

    public List<TransferRequestResponseDto> getAllRequestsByAdoptionId(Long adoptionId) {

        List<TransferRequestResponseDto> transferByAdoptionId = adoptionMapper.getTransferByAdoptionId(adoptionId);

        for (TransferRequestResponseDto dto : transferByAdoptionId) {
            if(dto.getTransporterId() != null){
                String storeName = transportMapper.getTransporterNameById(dto.getTransporterId()).getStoreName();
                dto.setTransporterName(storeName);
            }
        }

        return transferByAdoptionId;
    }
}
