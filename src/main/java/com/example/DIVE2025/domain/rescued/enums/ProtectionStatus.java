package com.example.DIVE2025.domain.rescued.enums;

import lombok.Getter;

@Getter
public enum ProtectionStatus {
    PROTECTED,
    FINISHED;

    public static ProtectionStatus fromCode(String code) {
        if(code != null && code.contains("보호")){
            return PROTECTED;
        }
        return FINISHED;
    }
}
