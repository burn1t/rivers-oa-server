package com.ocean.controller;

import com.ocean.query.ProcessInstanceQuery;
import com.ocean.query.StartupProcessInstanceQuery;
import com.ocean.service.ProcessInstanceService;
import com.ocean.utils.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instance")
@Slf4j
public class ProcessInstanceController {

    private final ProcessInstanceService instanceService;

    public ProcessInstanceController(ProcessInstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @PostMapping("/startup")
    public ResultModel startupProcessInstance(@RequestBody StartupProcessInstanceQuery startupQuery) {
        int result = instanceService.startupProcessInstance(startupQuery);
        if (result > 0) return ResultModel.success("提交申请与启动流程实例成功");
        else return ResultModel.error("提交申请失败");
    }

    @DeleteMapping("/{businessKey}/{processInstanceId}/{message}")
    public ResultModel cancelProcessInstance(@PathVariable("businessKey") String businessKey,
                                             @PathVariable("processInstanceId") String processInstanceId,
                                             @PathVariable("message") String reason) {

        log.info("business key: {} --- process instance key: {} --- message: {}",
                businessKey, processInstanceId, reason);

        Integer result = instanceService.cancelProcessInstance(businessKey, processInstanceId, reason);
        if (result > 0)  return ResultModel.success("撤回请求成功");
        return ResultModel.error("撤回请求失败");
    }

    @GetMapping("/formname/{processInstanceId}")
    public ResultModel getFormName(@PathVariable String processInstanceId) {
        Map<String, Object> result = new HashMap<>();
        String formName = instanceService.getFormName(processInstanceId);
        result.put("formName", formName);
        return ResultModel.success(result);
    }

    @GetMapping("/list/task/history/{processInstanceId}")
    public ResultModel getTaskHistoryInfoList(@PathVariable String processInstanceId) {


        try {
            List<Map<String, Object>> result = instanceService.getTaskHistoryInfoList(processInstanceId);
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("获取流程实例的办理历史节点信息失败：" + e.getMessage());
            return ResultModel.error("获取流程实例的办理历史节点信息失败，" + e.getMessage());
        }

    }

    @GetMapping("/img/history/{processInstanceId}")
    public void getProcessHistoryImg(@PathVariable String processInstanceId,
                                     HttpServletResponse response) {
        System.out.println("getHistoryProcessImg --- Process instance id: " + processInstanceId);
        log.info("getHistoryProcessImg --- Process instance id: {}", processInstanceId);
        try {
            instanceService.getProcessHistoryImg(processInstanceId, response);
        } catch (Exception e) {
            log.error("获取流程审批历史图失败：" + e.getMessage());
        }
    }

    @PostMapping("/list/running")
    public ResultModel getRunningProcessInstanceList(@RequestBody ProcessInstanceQuery processInstanceQuery) {
        Map<String, Object> result = instanceService.getRunningProcessInstanceList(processInstanceQuery);
        return ResultModel.success(result);
    }

    @PutMapping("/state/{processInstanceId}")
    public ResultModel processInstanceState(@PathVariable String processInstanceId) {
        try {
            boolean result = instanceService.updateProcessInstanceState(processInstanceId);
            if (result)
                return ResultModel.success("变更流程实例状态成功");
            else
                return ResultModel.error("变更流程实例状态失败");
        } catch (Exception e) {
            log.error("变更流程实例状态失败，" + e.getMessage());
            return ResultModel.error("变更流程实例状态失败，" + e.getMessage());
        }
    }

    @DeleteMapping("/{processInstanceId}")
    public ResultModel deleteRunningProcessInstance(@PathVariable String processInstanceId) {
        try {
            int result = instanceService.deleteRunningProcessInstance(processInstanceId);
            if (result > 0)
                return ResultModel.success("作废流程实例成功");
            else
                return ResultModel.error("作废流程实例失败");
        } catch (Exception e) {
            log.error("作废流程实例失败：" + e.getMessage());
            return ResultModel.error("作废流程实例失败，" + e.getMessage());
        }
    }

    @PostMapping("/list/finish")
    public ResultModel getFinishProcessInstanceList(@RequestBody ProcessInstanceQuery processInstanceQuery) {
        Map<String, Object> result =  instanceService.getFinishProcessInstanceList(processInstanceQuery);
        return ResultModel.success(result);
    }

    @DeleteMapping("/history/{processInstanceId}")
    public ResultModel deleteProcessInstanceHistory(@PathVariable String processInstanceId) {
        try {
            int result = instanceService.deleteFinishProcessInstanceHistory(processInstanceId);
            log.info("result: " + result);
            if (result > 0)
                return ResultModel.success("删除历流程实例历史审批记录成功");
            else
                return ResultModel.error("删除历流程实例历史审批记录失败");
        } catch (Exception e) {
            log.error("删除历流程实例历史审批记录失败：" + e.getMessage());
            return ResultModel.error("删除历流程实例历史审批记录，" + e.getMessage());
        }
    }

}
