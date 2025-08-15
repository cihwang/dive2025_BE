package com.example.DIVE2025.domain.login.mapper;

import com.example.DIVE2025.domain.login.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    UserEntity findByEmail(@Param("email") String email);

    int insertUser(UserEntity user);

    int updateUser(UserEntity user);
}
