package com.ocean.oaworkflowservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivitiService {

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public RepositoryService repositoryService;

    @Autowired
    public RuntimeService runtimeService;

    @Autowired
    public TaskService taskService;

    @Autowired
    public HistoryService historyService;

    /**
     * 用户要用于角色 ACTIVITI_USER
     * */
    @Autowired
    public ProcessRuntime processRuntime;

    @Autowired
    public TaskRuntime taskRuntime;


}
