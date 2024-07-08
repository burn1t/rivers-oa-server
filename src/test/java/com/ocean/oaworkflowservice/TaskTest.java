package com.ocean.oaworkflowservice;

import com.ocean.exception.BusinessException;
import com.ocean.utils.DataFilterUtils;
import com.ocean.utils.UserUtils;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest
public class TaskTest extends ActivitiService {

    @Test
    void getWaitingTaskList() {
        String assignee = "Floyd";

        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned("Floyd") // 候选人或者办理人
//                .taskCandidateUser("Floyd")
//                .taskAssignee("Floyd")
                .orderByTaskCreateTime()
                .asc()
                .listPage(0, 5);

        System.out.println(tasks);
    }

    @Test
    void testaa() {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");


        System.out.println(list.toArray(new String[0])[2]);
    }

    @Test
    void claimTask() {
        taskService.claim("f24e1918-32f7-11ef-82c9-00ff212ab656", "Floyd");
    }

    @Test
    void complete() {
        taskService.complete("50c38275-3451-11ef-92a9-00ff212ab656");
        System.out.println();
    }

    @Test
    void d() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", 1);
        map1.put("s", 2);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", 1);
        map2.put("s", 1);
        list.add(map1);
        list.add(map2);


        List<Map<String, Object>> result = DataFilterUtils.listDistinctByMapValue(list, "name");
//        AtomicBoolean flag = new AtomicBoolean(true);
//        HashSet<Object> set = new HashSet<>();
//        list.forEach(item -> {
//            if (!set.contains(item.get("name"))) {
//                result.add(item);
//                set.add(item.get("name"));
//            }
//
//        });


        System.out.println(result);

    }

    @Test
    public void check() {
        Task task = taskService.createTaskQuery()
                .taskId("0c08b689-3553-11ef-bb9d-00ff212ab656")
                .taskAssignee("Floyd")
                .singleResult();
        System.out.println(task);
    }

    @Test
    public void aaa() {
        taskService.claim("3cb4952f-3553-11ef-bb9d-00ff212ab656", "Floyd");
    }

    @Test
    public void his() {
        String processInstanceId = "077d936d-355f-11ef-addd-00ff212ab656";
        String processDefinitionId = "leaveProcess:1:2274031e-3144-11ef-90bc-00ff212ab656";

        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .taskName("直接领导审批")
                .finished()
                .orderByHistoricTaskInstanceStartTime()
                .asc().list();

        list.forEach(i -> {
            System.out.println(i.getId());
            System.out.println(i.getName());
        });

        List<Task> targetTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        targetTaskList.forEach(item -> {
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .taskName(item.getName())
                    .finished()
                    .orderByHistoricTaskInstanceStartTime()
                    .asc().listPage(0, 1);

            taskService.claim(item.getId(), historicTaskInstances.get(0).getAssignee());
        });
        System.out.println(list);
        System.out.println(String.valueOf(targetTaskList));
    }




}
