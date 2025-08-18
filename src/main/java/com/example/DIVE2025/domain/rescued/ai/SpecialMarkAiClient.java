package com.example.DIVE2025.domain.rescued.ai;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition;

public interface SpecialMarkAiClient {
    AnimalCondition analyze(String specialMark);
}
