package com.sjcw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjcw.entity.Unit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UnitMapper extends BaseMapper<Unit> {

    List<Unit> selectUnitTree();

    List<Unit> selectUnitList(Unit unit);
}
