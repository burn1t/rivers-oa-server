package com.ocean.oaworkflowservice;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProcessInstanceTask extends ActivitiService {

    // 2f9aa079-33b3-11ef-91d9-00ff212ab656

    @Test
    public void deleteProcessInstance() {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        List<ProcessInstance> instanceList = query.listPage(0,
                5);
        System.out.println(instanceList);
        runtimeService.deleteProcessInstance("b600b5eb-3171-11ef-b9ca-00ff212ab656", "1");
        // 获取任务对象
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId("b600b5eb-3171-11ef-b9ca-00ff212ab656")
                        .singleResult();

    }
}
