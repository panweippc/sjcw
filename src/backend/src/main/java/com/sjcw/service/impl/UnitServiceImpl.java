package com.sjcw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjcw.entity.Unit;
import com.sjcw.entity.User;
import com.sjcw.mapper.UnitMapper;
import com.sjcw.mapper.UserMapper;
import com.sjcw.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements UnitService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Unit> selectUnitTree() {
        List<Unit> allUnits = baseMapper.selectUnitTree();
        return buildTree(allUnits);
    }

    @Override
    public List<Unit> selectUnitList(Unit unit) {
        return baseMapper.selectUnitList(unit);
    }

    @Override
    public boolean saveUnit(Unit unit) {
        unit.setCreatedTime(LocalDateTime.now());
        unit.setUpdatedTime(LocalDateTime.now());
        
        if (unit.getParentId() != null && !unit.getParentId().trim().isEmpty()) {
            Unit parent = getById(unit.getParentId());
            if (parent != null) {
                unit.setUnitLevel(parent.getUnitLevel() + 1);
                unit.setUnitPath(parent.getUnitPath() + parent.getUnitId() + "/");
            } else {
                throw new RuntimeException("父单位不存在");
            }
        } else {
            unit.setParentId(null);
            unit.setUnitLevel(1);
            unit.setUnitPath("/");
        }
        
        return save(unit);
    }

    @Override
    public boolean updateUnit(Unit unit) {
        unit.setUpdatedTime(LocalDateTime.now());
        
        if (unit.getParentId() != null && unit.getParentId().trim().isEmpty()) {
            unit.setParentId(null);
        }
        
        boolean result = updateById(unit);
        if (!result) {
            throw new RuntimeException("更新单位失败：单位不存在或数据未变更");
        }
        return result;
    }

    @Override
    public boolean removeUnit(String unitId) {
        LambdaQueryWrapper<Unit> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Unit::getParentId, unitId);
        long childCount = count(childWrapper);
        if (childCount > 0) {
            throw new RuntimeException("删除失败：该单位下存在子单位");
        }

        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUnitId, unitId);
        long userCount = userMapper.selectCount(userWrapper);
        if (userCount > 0) {
            throw new RuntimeException("删除失败：该单位下存在用户");
        }

        return removeById(unitId);
    }

    private List<Unit> buildTree(List<Unit> units) {
        Map<String, Unit> unitMap = new HashMap<>();
        List<Unit> rootUnits = new ArrayList<>();

        for (Unit unit : units) {
            unit.setChildren(new ArrayList<>());
            unitMap.put(unit.getUnitId(), unit);
        }

        for (Unit unit : units) {
            if (unit.getParentId() == null || unit.getParentId().isEmpty()) {
                rootUnits.add(unit);
            } else {
                Unit parent = unitMap.get(unit.getParentId());
                if (parent != null) {
                    parent.getChildren().add(unit);
                }
            }
        }

        return rootUnits;
    }
}
