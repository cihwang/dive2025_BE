package com.example.DIVE2025.domain.rescued.ai;

import java.util.List;

public class AiLexicon {
    public static final List<String> SEVERE_TERMS = List.of(
            "파보","홍역","호흡곤란","호흡부전","다발성골절","개방성골절",
            "출혈","패혈","sepsis","shock","응급","탈수심함"
    );
    public static final List<String> MILD_TERMS = List.of(
            "피부염","결막염","경미","가벼운","긁힘","콧물","mild"
    );
    public static final List<String> NORMAL_TERMS = List.of(
            "특이사항 없음","정상","양호","괜찮음","문제 없음","normal","healthy"
    );
}
