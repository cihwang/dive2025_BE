package com.example.DIVE2025.domain.rescued.mapper;

import com.example.DIVE2025.domain.rescued.dto.RescuedDto;
import com.example.DIVE2025.domain.rescued.entity.Rescued;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RescuedQueryMapper {

    // 1) 특정 보호소의 동물 목록 (PROTECTED 기본, 페이징/선택필터는 XML에서 확장 가능)
    List<Rescued> findByShelter(
            @Param("shelterId") Long shelterId,
            @Param("status") String status,          // "PROTECTED" 권장
            @Param("upkindNm") String upkindNm,      // "개","고양이"…
            @Param("kindNm") String kindNmLike,      // 부분검색
            @Param("sex") String sex,                // "M","F","U"
            @Param("neuterYn") String neuterYn,      // "Y","N","U"
            @Param("from") LocalDate from,           // happen_dt 시작
            @Param("to") LocalDate to,               // happen_dt 끝
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    // 2) 특정 보호소 동물 수 (목록과 동일 필터)
    int countByShelter(
            @Param("shelterId") Long shelterId,
            @Param("status") String status,
            @Param("upkindNm") String upkindNm,
            @Param("kindNm") String kindNmLike,
            @Param("sex") String sex,
            @Param("neuterYn") String neuterYn,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // 3) 보호소별 요약(보호중 마릿수 랭킹)
    List<RescuedDto> countGroupByShelter(@Param("status") String status);

    // 4) 공용 View DTO로 한번에 내려주는 목록 (간단 버전)
    List<RescuedDto> findViewsByShelter(
            @Param("shelterId") Long shelterId,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

}
