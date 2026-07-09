package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.entity.Template;
import com.sjcw.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/list")
    public Result<List<Template>> list(Template template) {
        List<Template> templates = templateService.selectTemplateList(template);
        return Result.success(templates);
    }

    @GetMapping("/{id}")
    public Result<Template> getById(@PathVariable String id) {
        Template template = templateService.getById(id);
        return Result.success(template);
    }

    @PostMapping(consumes = "multipart/form-data")
    public Result<Void> save(
            @RequestParam("templateName") String templateName,
            @RequestParam("templateCode") String templateCode,
            @RequestParam(value = "templateType", required = false) String templateType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            @RequestParam(value = "isEnabled", defaultValue = "true") Boolean isEnabled) {
        
        Template template = new Template();
        template.setTemplateName(templateName);
        template.setTemplateCode(templateCode);
        template.setTemplateType(templateType);
        template.setDescription(description);
        template.setIsEnabled(isEnabled);
        
        if (attachment != null && !attachment.isEmpty()) {
            try {
                String filePath = com.sjcw.common.FileUploadUtil.upload(attachment);
                template.setAttachment(filePath);
            } catch (IOException e) {
                return Result.error("文件上传失败：" + e.getMessage());
            }
        }
        
        templateService.save(template);
        return Result.success();
    }

    @PutMapping(consumes = "multipart/form-data")
    public Result<Void> update(
            @RequestParam("templateId") String templateId,
            @RequestParam("templateName") String templateName,
            @RequestParam("templateCode") String templateCode,
            @RequestParam(value = "templateType", required = false) String templateType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            @RequestParam(value = "isEnabled", defaultValue = "true") Boolean isEnabled) {
        
        Template template = templateService.getById(templateId);
        if (template == null) {
            return Result.error("模板不存在");
        }
        
        template.setTemplateName(templateName);
        template.setTemplateCode(templateCode);
        template.setTemplateType(templateType);
        template.setDescription(description);
        template.setIsEnabled(isEnabled);
        
        if (attachment != null && !attachment.isEmpty()) {
            try {
                if (template.getAttachment() != null) {
                    com.sjcw.common.FileUploadUtil.delete(template.getAttachment());
                }
                String filePath = com.sjcw.common.FileUploadUtil.upload(attachment);
                template.setAttachment(filePath);
            } catch (IOException e) {
                return Result.error("文件上传失败：" + e.getMessage());
            }
        }
        
        templateService.updateById(template);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        Template template = templateService.getById(id);
        if (template != null && template.getAttachment() != null) {
            com.sjcw.common.FileUploadUtil.delete(template.getAttachment());
        }
        templateService.removeById(id);
        return Result.success();
    }
}
