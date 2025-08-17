package com.example.DIVE2025.domain.rescued.mapper;


import com.example.DIVE2025.domain.rescued.entity.Rescued;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RescuedMapper {

    int upsert(@Param("e") Rescued e);

    @Select("SELECT care_reg_no FROM shelter_registration")
    List<String> selectAllCareRegNos();

    int deleteByDesertionNo(@Param("desertionNo") String desertionNo);

    @Select("SELECT EXISTS (SELECT 1 FROM shelter_registration WHERE care_reg_no = #{careRegNo})")
    boolean existsShelterMapping(String careRegNo);

    @Select("SELECT COUNT(*) FROM rescued WHERE care_reg_no = #{careRegNo}")
    int countByCareRegNo(String careRegNo);
}
