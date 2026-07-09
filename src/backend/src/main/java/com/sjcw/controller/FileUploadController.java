package com.sjcw.controller;

import com.sjcw.common.FileUploadUtil;
import com.sjcw.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String filePath = FileUploadUtil.upload(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", filePath);
        result.put("name", file.getOriginalFilename());
        return Result.success(result);
    }

    @DeleteMapping("/{path}")
    public Result<Void> delete(@PathVariable String path) {
        boolean deleted = FileUploadUtil.delete("/uploads/" + path);
        if (!deleted) {
            return Result.error("删除文件失败");
        }
        return Result.success();
    }
}