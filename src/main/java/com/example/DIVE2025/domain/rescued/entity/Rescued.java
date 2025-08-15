package com.example.DIVE2025.domain.rescued.entity;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition;
import com.example.DIVE2025.domain.rescued.enums.NeuterStatus;
import com.example.DIVE2025.domain.rescued.enums.ProtectionStatus;
import com.example.DIVE2025.domain.rescued.enums.Sex;
import lombok.*;

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
    private String happenDt;
    private String upkindNm;
    private String kindNm;
    private String age;
    private String weight;
    private NeuterStatus neuterYn;
    private Sex sex;
    private String rfidCd;
    private ProtectionStatus protectionStatus; // PROTECTED, FINISHED
    private LocalDateTime rescueDate;
    private LocalDateTime moveDate;
    private LocalDateTime protectEndDate;
    private AnimalCondition animalCondition; // INJURED, NORMAL

    @Builder
    public Rescued(Long id, Long shelterId, String desertionNo, String happenDt, String upkindNm, String kindNm,
                   String age, String weight, NeuterStatus neuterYn, Sex sex, String rfidCd,
                   ProtectionStatus protectionStatus, LocalDateTime rescueDate, LocalDateTime moveDate,
                   AnimalCondition animalCondition) {

        this.id = id;
        this.shelterId = shelterId;
        this.desertionNo = desertionNo;
        this.happenDt = happenDt;
        this.upkindNm = upkindNm;
        this.kindNm = kindNm;
        this.age = age;
        this.weight = weight;
        this.neuterYn = neuterYn;
        this.sex = sex;
        this.rfidCd = rfidCd;
        this.protectionStatus = protectionStatus;
        this.rescueDate = rescueDate;
        this.moveDate = moveDate;
        this.animalCondition = animalCondition;
        this.protectEndDate = rescueDate.plusDays(10);
    }
}
