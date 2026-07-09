package com.sjcw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjcw.entity.Template;

import java.util.List;

public interface TemplateService extends IService<Template> {

    List<Template> selectTemplateList(Template template);
}
