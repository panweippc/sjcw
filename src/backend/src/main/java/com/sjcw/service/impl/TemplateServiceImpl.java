package com.sjcw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjcw.entity.Template;
import com.sjcw.mapper.TemplateMapper;
import com.sjcw.service.TemplateService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {

    @Override
    public List<Template> selectTemplateList(Template template) {
        return baseMapper.selectTemplateList(template);
    }

    @Override
    public boolean save(Template entity) {
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());
        return super.save(entity);
    }

    @Override
    public boolean updateById(Template entity) {
        entity.setUpdatedTime(LocalDateTime.now());
        return super.updateById(entity);
    }
}
