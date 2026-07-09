package com.sjcw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjcw.entity.Template;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TemplateMapper extends BaseMapper<Template> {

    List<Template> selectTemplateList(Template template);
}
