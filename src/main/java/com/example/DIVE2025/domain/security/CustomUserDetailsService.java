package com.example.DIVE2025.domain.security;

import com.example.DIVE2025.domain.member.mapper.MemberMapper;
import com.example.DIVE2025.domain.security.dto.CustomUserDetails;
import com.example.DIVE2025.domain.shelter.entity.Shelter;
import com.example.DIVE2025.domain.transporterRequest.entity.Transporter;
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
        Shelter s = memberMapper.findShelterByUsername(username);
        if (s != null) return CustomUserDetails.fromShelterEntity(s);

        Transporter t = memberMapper.findTransporterByUsername(username);
        if (t != null) return CustomUserDetails.fromTransporter(t);

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
