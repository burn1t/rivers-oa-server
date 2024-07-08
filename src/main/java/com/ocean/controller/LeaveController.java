package com.ocean.controller;


import com.ocean.entity.Leave;
import com.ocean.query.LeaveQuery;
import com.ocean.service.LeaveService;
import com.ocean.service.impl.LeaveServiceImpl;
import com.ocean.utils.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/leave")
@Slf4j
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    public ResultModel addLeave(@RequestBody Leave leave) {
        try {
            int result = leaveService.addLeave(leave);
            if (result > 0) return ResultModel.success("新增请假请求成功");
            else return ResultModel.error("新增请假请求失败");
        } catch (Exception e) {
            log.error("新增请假请求失败: " + e.getMessage());
            return ResultModel.error("新增请假请求失败，" + e.getMessage());
        }
    }

    @PostMapping("/list")
    public ResultModel getLeaveList(@RequestBody LeaveQuery leaveQuery) {
        Map<String, Object> result;
        result = leaveService.getLeaveList(leaveQuery);
        return ResultModel.success(result);
    }

    @GetMapping("/{id}")
    public ResultModel getLeave(@PathVariable("id") String id) {
        Leave leave = leaveService.getLeave(id);
        return ResultModel.success(leave);
    }

    @PutMapping("/update")
    public ResultModel updateLeave(@RequestBody Leave leave) {
        int result = leaveService.updateLeave(leave);
        if (result > 0) return ResultModel.success("编辑请假请求成功");
        else return ResultModel.error("编辑请假请求失败");
    }

}
