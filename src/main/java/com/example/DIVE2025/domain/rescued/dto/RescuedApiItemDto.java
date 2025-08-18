package com.example.DIVE2025.domain.rescued.dto;

import com.example.DIVE2025.domain.rescued.entity.Rescued;
import com.example.DIVE2025.domain.rescued.enums.NeuterStatus;
import com.example.DIVE2025.domain.rescued.enums.ProtectionStatus;
import com.example.DIVE2025.domain.rescued.enums.Sex;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class RescuedApiItemDto {
    private String desertionNo;
    private String happenDt;      // "yyyyMMdd"
    private String upKindNm;      // "개", "고양이"...
    private String kindNm;        // "믹스견" 등
    private String age;
    private String weight;
    private String neuterYn;      // Y/N/U
    private String sexCd;         // M/F/Q
    private String rfidNo;        // 없으면 null
    private String processState;  // "보호중" 등
    private String careRegNo;
    private String popfile1;
    private String popfile2;
    private String specialMark;


    private static final DateTimeFormatter API_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static String nz(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    public Rescued toEntity() {
        LocalDate happen = parseYmd(this.happenDt);             // 비교 용이하게 LocalDate로 변환
        LocalDate protectEnd = (happen != null) ? happen.plusDays(10) : null;  // 법정 10일

        return Rescued.builder()
                .desertionNo(this.desertionNo)
                .happenDt(happen)                // 엔티티는 LocalDate 권장(쿼리/비교 쉬움)
                .upkindNm(nz(this.upKindNm))
                .kindNm(nz(this.kindNm))
                .age(nz(this.age))
                .weight(nz(this.weight))
                .neuterYn(mapNeuter(this.neuterYn))
                .sex(mapSex(this.sexCd))
                .rfidCd(nz(this.rfidNo))
                .popfile1(nz(this.popfile1))
                .popfile2(nz(this.popfile2))
                .protectionStatus(mapProcess(this.processState))
                .rescueDate(happen)              // 구조일 = happenDt
                .moveDate(null)
                .protectEndDate(protectEnd)      // happenDt + 10일
                .careRegNo(this.careRegNo)
                .build();
    }

    private static LocalDate parseYmd(String ymd) {
        if (ymd == null || ymd.length() != 8) return null;
        return LocalDate.parse(ymd, API_FMT);
    }

    private static NeuterStatus mapNeuter(String v){
        if (v == null) return NeuterStatus.U;
        switch (v.toUpperCase()){
            case "Y": return NeuterStatus.Y;
            case "N": return NeuterStatus.N;
            default : return NeuterStatus.U;
        }
    }
    private static Sex mapSex(String v){
        if (v == null) return Sex.U;
        switch (v.toUpperCase()){
            case "M": return Sex.M;
            case "F": return Sex.F;
            default : return Sex.U; // Q 등은 미상
        }
    }
    private static ProtectionStatus mapProcess(String s) {
        return ProtectionStatus.fromCode(s);
    }

}
