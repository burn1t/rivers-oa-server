package com.ocean.service;

import com.ocean.query.TaskCompleteQuery;
import com.ocean.query.TaskQueryInfo;

import java.util.List;
import java.util.Map;

public interface TaskService {

    Map<String, Object> getWaitingTaskList(TaskQueryInfo taskQueryInfo);

    void claimTask(String taskId);

    List<Map<String, Object>> getNextTaskNodeInfo(String taskId);

    boolean completeTask(TaskCompleteQuery taskCompleteQuery);

    void turnTask(String taskId, String assignee);

    List<Map<String, Object>> getBackHistoryTaskNode(String taskId);

    void backTargetTask(String taskId, String targetTaskId);

    Map<String, Object> getCompletedTaskList(TaskQueryInfo taskQueryInfo);
}
