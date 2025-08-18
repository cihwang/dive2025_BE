package com.example.DIVE2025.domain.rescued.mapper;


import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
import com.example.DIVE2025.domain.rescued.entity.Rescued;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RescuedMapper {

    int upsert(@Param("e") Rescued e);

    RescuedResponseDto getShelterIdByDesertionNo(@Param("desertionNo") String desertionNo);

    @Select("SELECT care_reg_no FROM shelter_registration")
    List<String> selectAllCareRegNos();

    int deleteByDesertionNo(@Param("desertionNo") String desertionNo);

    @Select("SELECT EXISTS (SELECT 1 FROM shelter_registration WHERE care_reg_no = #{careRegNo})")
    boolean existsShelterMapping(String careRegNo);

    @Select("SELECT COUNT(*) FROM rescued WHERE care_reg_no = #{careRegNo}")
    int countByCareRegNo(String careRegNo);

    List<Rescued> selectByShelter(@Param("careRegNo") String careRegNo,
                                  @Param("state") String state,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    List<Map<String,Object>> countByShelter(@Param("state") String state);

    List<Rescued> selectRisky(@Param("careRegNo") String careRegNo,
                              @Param("state") String state,
                              @Param("injuredOnly") boolean injuredOnly,
                              @Param("dueInDays") int dueInDays,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

}
