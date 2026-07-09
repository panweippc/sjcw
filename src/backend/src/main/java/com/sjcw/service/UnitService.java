package com.sjcw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjcw.entity.Unit;

import java.util.List;

public interface UnitService extends IService<Unit> {

    List<Unit> selectUnitTree();

    List<Unit> selectUnitList(Unit unit);

    boolean saveUnit(Unit unit);

    boolean updateUnit(Unit unit);

    boolean removeUnit(String unitId);
}
