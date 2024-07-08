package com.ocean.controller;

import com.ocean.common.BaseResponse;
import com.ocean.utils.ResponseUtil;
import org.apache.xml.utils.res.XResources_ka;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestController {

    @GetMapping("/test")
    public BaseResponse<Integer> test() {
        return ResponseUtil.success(1);
    }
}
