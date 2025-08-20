package com.example.DIVE2025.domain.rescued.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RescuedResponseDto {

    // 기본 식별
    private Long id;
    private String desertionNo;

    // 보호소 정보
    private Long shelterId;
    private String shelterName;     // shelter.description
    private String shelterFeature;  // 병원/일반 구분 등
    private Double latitude;
    private Double longitude;
    private String shelterTel;
    private String shelterAddr;

    // 동물 기본
    private String upkindNm;
    private String kindNm;
    private String age;
    private String weight;
    private String sex;             // "M","F","U"
    private String neuterYn;        // "Y","N","U"

    // 상태/일자
    private String protectionStatus; // "PROTECTED","FINISHED"

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate happenDt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate rescueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate moveDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate protectEndDate;

    // 기타
    private String careRegNo;
    private String rfidCd;
    private String popfile1;
    private String popfile2;

    // 공통 계산 필드(조회용)
    private Integer daysProtected;  // 오늘 - happenDt
    private Boolean overdue;        // daysProtected > 10
    private Boolean needsTransfer;  // 2번 요건을 위한 flag(아픈데 병원 아님 등)

}
