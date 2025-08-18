package com.example.DIVE2025.domain.rescued.entity;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition;
import com.example.DIVE2025.domain.rescued.enums.NeuterStatus;
import com.example.DIVE2025.domain.rescued.enums.ProtectionStatus;
import com.example.DIVE2025.domain.rescued.enums.Sex;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rescued {
    private Long id;
    private Long shelterId;
    private String desertionNo;
    private String careRegNo;

    //발생일자
    private LocalDate happenDt;

    //동물 기본 정보
    private String upkindNm;
    private String kindNm;
    private String age;
    private String weight;

    private NeuterStatus neuterYn;
    private Sex sex;

    private String rfidCd;

    //동물 사진
    private String popfile1;
    private String popfile2;

    //동물 상세 정보
    private ProtectionStatus protectionStatus; // PROTECTED, FINISHED
    private AnimalCondition animalCondition; // NORMAL, MILD, SEVERE

    //날짜 정보
    private LocalDate rescueDate;
    private LocalDate moveDate;
    private LocalDate protectEndDate;

    private java.time.LocalDateTime updatedAt;

    @Builder
    public Rescued(Long id, Long shelterId, String desertionNo, String careRegNo,
                   LocalDate happenDt,
                   String upkindNm, String kindNm, String age, String weight,
                   NeuterStatus neuterYn, Sex sex, String rfidCd,
                   String popfile1, String popfile2,
                   ProtectionStatus protectionStatus, AnimalCondition animalCondition,
                   LocalDate rescueDate, LocalDate moveDate) {

        this.id = id;
        this.shelterId = shelterId;
        this.desertionNo = desertionNo;
        this.careRegNo = careRegNo;
        this.happenDt = happenDt;

        this.upkindNm = upkindNm;
        this.kindNm = kindNm;
        this.age = age;
        this.weight = weight;

        this.neuterYn = neuterYn;
        this.sex = sex;
        this.rfidCd = rfidCd;

        this.popfile1 = popfile1;
        this.popfile2 = popfile2;

        this.protectionStatus = protectionStatus;
        this.animalCondition = animalCondition;

        this.rescueDate = rescueDate;
        this.moveDate = moveDate;

        // 보호 종료일 = 구조일 + 10일
        this.protectEndDate = (rescueDate != null) ? rescueDate.plusDays(10) : null;
    }
}
