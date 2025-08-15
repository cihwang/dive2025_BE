package com.example.DIVE2025.domain.login.mapper;

import com.example.DIVE2025.domain.login.entity.RefreshTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshMapper {

    boolean existsByRefresh(@Param("refresh") String refresh);

    int deleteByEmail(@Param("email") String email);

    int insert(RefreshTokenEntity token);
}
