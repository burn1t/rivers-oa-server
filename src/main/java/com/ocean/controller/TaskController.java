package com.ocean.controller;

import com.ocean.exception.BusinessException;
import com.ocean.query.TaskCompleteQuery;
import com.ocean.query.TaskQueryInfo;
import com.ocean.service.TaskService;
import com.ocean.utils.ResultModel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping("/list/waiting")
    public ResultModel getWaitingTaskList(@RequestBody TaskQueryInfo taskQueryInfo) {
        try {
            Map<String, Object> result = taskService.getWaitingTaskList(taskQueryInfo);
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("获取待办任务列表失败: " + e.getMessage());
            return ResultModel.error("获取待办任务列表失败，" + e.getMessage());
        }
    }

    @PostMapping("/claim/{taskId}")
    public ResultModel claimTask(@PathVariable String taskId) {
        try {
            taskService.claimTask(taskId);
            return ResultModel.success("签收任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModel.error("签收任务失败，" + e.getMessage());
        }
    }

    @GetMapping("/next/{taskId}")
    public ResultModel nextTaskNodeList(@PathVariable String taskId) {
        try {
            List<Map<String, Object>> nextTaskNodeInfo = taskService.getNextTaskNodeInfo(taskId);
            return ResultModel.success("获取后续节点信息成功", nextTaskNodeInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModel.error("获取后续节点信息失败，" + e.getMessage());
        }
    }

    @PostMapping("/complete")
    public ResultModel completeTask(@RequestBody TaskCompleteQuery taskCompleteQuery) {
        try {
            boolean result = taskService.completeTask(taskCompleteQuery);
            if (result) return ResultModel.success("审批任务成功");
            else return ResultModel.error("审批任务失败");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultModel.error("审批任务失败，" + e.getMessage());
        }
    }

    @PostMapping("/turn/{taskId}/{assignee}")
    public ResultModel turnTask(@PathVariable String taskId,
                                @PathVariable String assignee) {
        try {
            taskService.turnTask(taskId, assignee);
            return ResultModel.success("转办任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultModel.error("转办任务失败，" + e.getMessage());
        }
    }

    @PostMapping("/list/back/{taskId}")
    public ResultModel getBackTaskHistoryNodeList(@PathVariable String taskId) {
        try {
            List<Map<String, Object>> backHistoryTaskNodeList = taskService.getBackHistoryTaskNode(taskId);
            return ResultModel.success(backHistoryTaskNodeList);
        } catch (NullPointerException | BusinessException e) {
            return ResultModel.error(e.getMessage());
        }
    }

    @PostMapping("/back/{taskId}/{targetTaskId}")
    public ResultModel backToTargetTask(@PathVariable String taskId,
                                        @PathVariable String targetTaskId) {
        try {
            taskService.backTargetTask(taskId, targetTaskId);
            return ResultModel.success("驳回审批成功");
        } catch (Exception e) {
            return ResultModel.error("驳回审批失败 " + e.getMessage());
        }
    }

    @PostMapping("/list/completed")
    public ResultModel completedTaskList(@RequestBody TaskQueryInfo taskQueryInfo) {
        log.info(String.valueOf(taskQueryInfo.getCurrent()));
        try {
            Map<String, Object> completedTaskList = taskService.getCompletedTaskList(taskQueryInfo);
            return ResultModel.success(completedTaskList);
        } catch (NullPointerException | BusinessException e) {
            return ResultModel.error(e.getMessage());
        }
    }
}
