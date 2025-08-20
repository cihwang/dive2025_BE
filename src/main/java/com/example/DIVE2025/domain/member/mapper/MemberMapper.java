package com.example.DIVE2025.domain.member.mapper;

import com.example.DIVE2025.domain.shelter.entity.Shelter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    /** username(로그인 ID) 중복 체크 */
    int countByUsername(@Param("username") String username);

    /** username으로 Shelter 조회 */
    Shelter findByUsername(@Param("username") String username);

    /** PK(shelterId)로 Shelter 조회 */
    Shelter findById(@Param("id") Long id);

    /** 신규 보호소 등록 */
    int insert(Shelter shelter);

}
