package com.example.DIVE2025.domain.rescued.mapper;

import com.example.DIVE2025.domain.rescued.dto.RescuedRequestDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface RescuedQueryMapper {

    /** 보호센터별 보유 동물 목록 (동적 필터/정렬/페이징) */
    List<RescuedResponseDto> findAnimalsByShelter(@Param("q") RescuedRequestDto q);

    /** 특정 보호센터(PK) 보유 수 */
    long countByShelter(@Param("shelterId") Long shelterId);

    /** 특정 보호센터(등록번호) 보유 수 */
    long countByCareRegNo(@Param("careRegNo") String careRegNo);

    /** 보호센터별 보유 수 그룹핑 (대시보드용) — DTO 없이 HashMap 리스트 */
    List<HashMap<String, Object>> countGroupByShelter();

    /** 특정 보호센터 내 지역구(care_reg_no)별 카운트 — DTO 없이 HashMap 리스트 */
    List<HashMap<String, Object>> countByShelterGroupByCareRegNo(@Param("shelterId") Long shelterId);


    List<RescuedResponseDto> findTransferCandidates(
            @Param("shelterId") Long shelterId,
            @Param("usePeriod") boolean usePeriod,
            @Param("dueWithinDays") int dueWithinDays, // 0=경과만, n=임박 포함
            @Param("useSeverity") boolean useSeverity,
            @Param("condition") String condition,
            @Param("sort") String sort,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("limit") int limit
    );
}
