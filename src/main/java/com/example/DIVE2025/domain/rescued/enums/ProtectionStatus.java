package com.example.DIVE2025.domain.rescued.enums;

import lombok.Getter;

@Getter
public enum ProtectionStatus {
    PROTECTED,
    FINISHED;

    public static ProtectionStatus fromCode(String code) {
        if (code == null) return FINISHED;
        String v = code.replaceAll("\\s", ""); // 공백 제거
        return v.contains("보호") ? PROTECTED : FINISHED;
    }
}
