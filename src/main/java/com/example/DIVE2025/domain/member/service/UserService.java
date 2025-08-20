package com.example.DIVE2025.domain.member.service;

import com.example.DIVE2025.domain.member.dto.UserJoinRequestDTO;
import com.example.DIVE2025.domain.member.dto.UserResponseDTO;
import com.example.DIVE2025.domain.member.mapper.MemberMapper;
import com.example.DIVE2025.domain.security.JwtTokenProvider;
import com.example.DIVE2025.domain.shelter.entity.Shelter;
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

    /** username으로 보호소 조회 */
    public UserResponseDTO getByUsername(String username) {
        Shelter s = memberMapper.findByUsername(username);
        return toResponseDto(s);
    }

    /** PK(shelterId)로 보호소 조회 */
    public UserResponseDTO getById(Long id) {
        Shelter s = memberMapper.findById(id);
        return toResponseDto(s);
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

    public UserResponseDTO getMyInfo(String token) {
        Long shelterId = jwtTokenProvider.getShelterIdFromToken(token.replace("Bearer ", ""));
        Shelter s = memberMapper.findById(shelterId);
        return UserResponseDTO.fromEntity(s);
    }


}
