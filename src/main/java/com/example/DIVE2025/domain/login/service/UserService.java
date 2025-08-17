package com.example.DIVE2025.domain.login.service;

import com.example.DIVE2025.domain.login.entity.UserEntity;
import com.example.DIVE2025.domain.login.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Transactional
    public UserEntity upsertByEmail(String email, String username) {
        UserEntity found = userMapper.findByEmail(email);
        if (found == null) {
            UserEntity user = UserEntity.builder()
                    .email(email)
                    .username(username)
                    .role("ROLE_USER")
                    .build();
            userMapper.insertUser(user);
            return user;
        } else {
            found.setUsername(username);
            // found.setRole("ROLE_ADMIN");
            userMapper.updateUser(found);
            return found;
        }
    }
}
