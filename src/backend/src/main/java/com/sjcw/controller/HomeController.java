package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.service.RoleService;
import com.sjcw.service.TemplateService;
import com.sjcw.service.UnitService;
import com.sjcw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private TemplateService templateService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("userCount", userService.count());
        statistics.put("roleCount", roleService.count());
        statistics.put("unitCount", unitService.count());
        statistics.put("templateCount", templateService.count());
        return Result.success(statistics);
    }
}
