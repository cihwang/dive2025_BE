package com.example.DIVE2025.domain.security;

import com.example.DIVE2025.domain.member.mapper.MemberMapper;
import com.example.DIVE2025.domain.security.dto.CustomUserDetails;
import com.example.DIVE2025.domain.shelter.entity.Shelter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Shelter shelter = memberMapper.findByUsername(username);
        if (shelter == null) {
            throw new UsernameNotFoundException("Shelter not found with username: " + username);
        }
        return CustomUserDetails.fromShelterEntity(shelter); //Shelter → CustomUserDetails 변환
    }
}
