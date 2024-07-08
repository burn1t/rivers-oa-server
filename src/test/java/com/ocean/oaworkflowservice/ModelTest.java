package com.ocean.oaworkflowservice;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.junit.jupiter.api.Test;

public class ModelTest extends ActivitiService{

    @Test
    public void aa() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //注意此时总经理审批还未结束
        repositoryService.deleteDeployment("1", true);

    }

}
