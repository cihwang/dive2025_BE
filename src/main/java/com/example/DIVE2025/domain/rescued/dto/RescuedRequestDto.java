package com.example.DIVE2025.domain.rescued.dto;

import com.example.DIVE2025.domain.rescued.enums.NeuterStatus;
import com.example.DIVE2025.domain.rescued.enums.Sex;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;


@Data
public class RescuedRequestDto {

    // --- 식별(둘 중 하나는 필수) ---
    /** 내부 보호소 PK */
    private Long shelterId;

    /** 외부 보호소 등록번호 */
    private String careRegNo;

    private List<String> careRegNos;

    // --- 필터 ---
    /** 성별 (M/F/U) */
    private Sex sex;

    /** 중성화 여부 (Y/N/U) */
    private NeuterStatus neuterYn;

    /** 대분류(개/고양이/기타 등) */
    private String upkindNm;

    /** 품종명(부분검색) */
    private String kindNm;

    /** 구조일(시작) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate rescueDateFrom;

    /** 구조일(끝) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate rescueDateTo;

    /** 자유검색(품종/특징 등) */
    private String keyword;

    // --- 페이징 & 정렬 ---
    /** 페이지(0-base) */
    private Integer page = 0;

    /** 페이지 크기 */
    private Integer size = 20;

    /**
     * 정렬 컬럼: rescueDate(기본) | happenDt
     * Mapper XML에서 안전하게 스위칭함.
     */
    private String sort = "rescueDate";

    /** 정렬 방향: asc | desc (기본 desc) */
    private String order = "desc";

}
