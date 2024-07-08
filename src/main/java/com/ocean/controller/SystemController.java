package com.ocean.controller;

import com.ocean.utils.ResultModel;
import com.ocean.utils.UserUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class SystemController {

    @GetMapping("/info")
    public ResultModel info() {
//        return SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getName();
        return ResultModel.success("获取当前系统用户名成功", UserUtils.getUsername());
    }

}
