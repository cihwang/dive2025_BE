package com.example.DIVE2025.domain.rescued.mapper;


import com.example.DIVE2025.domain.rescued.entity.Rescued;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RescuedMapper {

    int upsert(Rescued e);

    @Select("SELECT care_reg_no FROM shelter_registration")
    List<String> selectAllCareRegNos();

    int deleteByDesertionNo(String desertionNo);


}
