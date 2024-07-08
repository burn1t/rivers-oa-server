package com.ocean.controller;

import com.ocean.entity.ProcessConfig;
import com.ocean.service.ProcessConfigService;
import com.ocean.utils.ResultModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process/config")
public class ProcessConfigController {

    private final ProcessConfigService processConfigService;

    public ProcessConfigController(ProcessConfigService processConfigService) {
        this.processConfigService = processConfigService;
    }

    @GetMapping("/{processKey}")
    public ResultModel getProcessConfig(@PathVariable String processKey) {
        ProcessConfig processConfig = processConfigService.getProcessConfigByProcessKey(processKey);
        return ResultModel.success(processConfig);
    }

    @PostMapping
    public ResultModel saveOrUpdate(@RequestBody ProcessConfig processConfig) {
        int result = processConfigService.saveOrUpdateProcessConfig(processConfig);
        if (result > 0)
            return ResultModel.success("保存或更新流程配置成功");
        else
            return ResultModel.error("保存或更新流程配置失败");
    }

}
