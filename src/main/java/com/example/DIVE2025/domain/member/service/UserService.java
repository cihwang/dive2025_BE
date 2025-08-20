package com.example.DIVE2025.domain.member.service;

import com.example.DIVE2025.domain.member.dto.UserJoinRequestDTO;
import com.example.DIVE2025.domain.member.dto.UserResponseDTO;
import com.example.DIVE2025.domain.member.mapper.MemberMapper;
import com.example.DIVE2025.domain.security.JwtTokenProvider;
import com.example.DIVE2025.domain.shelter.entity.Shelter;
import com.example.DIVE2025.domain.transporterRequest.entity.Transporter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberMapper memberMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    /** username(로그인 ID) 중복체크 */
    public boolean checkUsernameDuplicate(String username) {
        return memberMapper.countByUsername(username) > 0;
    }

    /** 보호소 등록 (회원가입) */
    public Shelter join(Shelter shelter) {
        shelter.setPassword(passwordEncoder.encode(shelter.getPassword())); // 비밀번호 암호화
        memberMapper.insert(shelter);
        return shelter;
    }

    /** username으로 사용자 조회 (SHELTER → TRANSPORTER 순차) */
    public UserResponseDTO getByUsername(String username) {
        Shelter s = memberMapper.findShelterByUsername(username);
        if (s != null) return toResponseDto(s);

        Transporter t = memberMapper.findTransporterByUsername(username);
        if (t != null) return toResponseDto(t); // ✅ transporter 매핑

        return null;
    }



    /** Shelter → UserResponseDTO 변환 */
    private UserResponseDTO toResponseDto(Shelter s) {
        if (s == null) return null;
        return UserResponseDTO.builder()
                .shelterId(s.getId())
                .username(s.getUsername())
                .description(s.getDescription())
                .tel(s.getTel())
                .addr(s.getAddr())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .shelterFeature(s.getShelterFeature())
                .build();
    }

    /** Transporter → UserResponseDTO 변환(오버로드) */
    private UserResponseDTO toResponseDto(Transporter t) {
        if (t == null) return null;
        return UserResponseDTO.builder()
                // transporterId 필드가 DTO에 없으면 생략 가능. 필요하면 DTO에 추가 권장
                // .transporterId(t.getId())
                .username(t.getUsername())
                .description(t.getStoreName()) // 표시용 이름을 description에 재사용
                .tel(t.getTel())
                .addr(t.getAddr())
                .latitude(t.getLatitude())
                .longitude(t.getLongitude())
                // shelterFeature는 없음 → 세팅하지 않음
                .build();
    }

    /** PK(shelterId)로 보호소 조회 */
    public UserResponseDTO getById(Long id) {
        Shelter s = memberMapper.findShelterById(id);
        return toResponseDto(s);
    }


    public UserResponseDTO getMyInfo(String token) {
        Long shelterId = jwtTokenProvider.getShelterIdFromToken(token.replace("Bearer ", ""));
        Shelter s = memberMapper.findShelterById(shelterId);
        return UserResponseDTO.fromEntity(s);
    }

    /** ✅ PK로 사용자 조회 (stype: SHELTER | TRANSPORTER) */
    public UserResponseDTO getById(String stype, Long id) {
        if ("TRANSPORTER".equalsIgnoreCase(stype)) {
            var t = memberMapper.findTransporterById(id);
            return toResponseDto(t);   // Transporter 오버로드 사용
        } else { // 기본: SHELTER
            var s = memberMapper.findShelterById(id);
            return toResponseDto(s);   // 기존 Shelter 매핑
        }
    }




}
