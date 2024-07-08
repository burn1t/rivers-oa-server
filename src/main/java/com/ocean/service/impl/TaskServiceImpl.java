package com.ocean.service.impl;

import com.ocean.cmd.DeleteExecutionCommand;
import com.ocean.cmd.DeleteTaskCommand;
import com.ocean.dao.BusinessStatusDao;
import com.ocean.enums.BusinessStatusEnum;
import com.ocean.exception.BusinessException;
import com.ocean.query.TaskCompleteQuery;
import com.ocean.query.TaskQueryInfo;
import com.ocean.service.ProcessInstanceService;
import com.ocean.service.TaskService;
import com.ocean.utils.DataFilterUtils;
import com.ocean.utils.DateUtils;
import com.ocean.utils.ResultModel;
import com.ocean.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.bpmn.model.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TaskServiceImpl extends ActivitiService implements TaskService {
    private final ProcessInstanceService processInstanceService;

    private final BusinessStatusDao businessStatusDao;

    public TaskServiceImpl(ProcessInstanceService processInstanceService, BusinessStatusDao businessStatusDao) {
        this.processInstanceService = processInstanceService;
        this.businessStatusDao = businessStatusDao;
    }

    @Override
    public Map<String, Object> getWaitingTaskList(TaskQueryInfo taskQueryInfo) {
        String assignee = UserUtils.getUsername();// 当前办理人
        String taskName = taskQueryInfo.getTaskName();
        TaskQuery query = taskService.createTaskQuery()
                .taskCandidateOrAssigned(assignee) // 候选人或者办理人
                .orderByTaskCreateTime()
                .asc();
        if (!StringUtils.isEmpty(taskName))
            query.taskNameLikeIgnoreCase("%" + taskName + "%");
        List<Task> taskList = query.listPage(
                taskQueryInfo.getFirstResult(),
                taskQueryInfo.getSize());
        long total = query.count();

        List<Map<String, Object>> records = new ArrayList<>();
        for (Task task : taskList) {
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", task.getId());
            data.put("taskName", task.getName());// 任务名称，当前节点名称
//            data.put("processStatus", task.isSuspended() ? "已暂停" : "已启动");// 流程状态
            data.put("processStatus", task.isSuspended());
            data.put("taskCreateTime", DateUtils.format(task.getCreateTime()));// 任务开始日期
            data.put("processInstanceId", task.getProcessInstanceId());
            data.put("executionId", task.getExecutionId());// 执行 id
            data.put("processDefinitionId", task.getProcessDefinitionId());
            data.put("taskAssignee", task.getAssignee());// 任务办理人，候选人没有值，办理才有值
            // 查询流程实例
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            data.put("processName", processInstance.getProcessDefinitionName());
            data.put("version", processInstance.getProcessDefinitionVersion());
            data.put("proposer", processInstance.getStartUserId());// 发起者
            data.put("businessKey", processInstance.getBusinessKey());

            records.add(data);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);
        return result;
    }

    @Override
    public void claimTask(String taskId) {
        if (taskId == null) throw new BusinessException("无法获取任务节点 id");
        String username = UserUtils.getUsername();
        taskService.claim(taskId, username);
    }


    @Override
    public List<Map<String, Object>> getNextTaskNodeInfo(String taskId) {
        /* 获取结构上当前节点的信息 */
        // 获取当前模型
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) throw new BusinessException("任务不存在");
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 获取当前节点，根据任务节点 id
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());

        // 下一个节点为一个或多个节点
        List<Map<String, Object>> nextNodes = new ArrayList<>();
        getNextNodes(flowElement, nextNodes);
        return nextNodes;
    }

    @Override
    public boolean completeTask(TaskCompleteQuery taskCompleteQuery) {
        log.info(String.valueOf(taskCompleteQuery));
        String taskId = taskCompleteQuery.getTaskId();
        String message = taskCompleteQuery.getMessage();
        // 获取当前节点
        org.activiti.api.task.model.Task task = taskRuntime.task(taskId);
        if (task == null) throw new BusinessException("任务不存在");
        String processInstanceId = task.getProcessInstanceId();
        Map<String, List<String>> assigneeMap = taskCompleteQuery.getAssigneeMap();
        /* 添加处理意见 */
        taskService.addComment(taskId, processInstanceId, message);
        /*完成任务*/
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId).build());
        /* 查询下一个任务节点 */
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        // 判断下一个节点是否没有任务，表示到达结束节点
        if (CollectionUtils.isEmpty(taskList)) {
            // 通过流程实例获取业务 id
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            String businessKey = historicProcessInstance.getBusinessKey();
            // 更新业务状态
            Integer result = businessStatusDao.updateState(businessKey,
                    BusinessStatusEnum.FINISH.getCode(),
                    "");
            if (result > 0) return true;
        }

        /* 设置候选人 */
        // 未分配处理人或候选人，结束并删除流程实例
        if (assigneeMap.isEmpty()) {
            int result = processInstanceService
                    .deleteProcessInstance(processInstanceId, "审批节点未分配审批人，流程直接中断取消");
            if (result > 0) throw new BusinessException("审批节点未分配审批人，流程直接中断取消");
        }
        // 分配候选人
        for (Task item : taskList) {
            if (StringUtils.isNotEmpty(item.getAssignee())) continue;
            // 根据任务节点获取对应候选人
            List<String> assigneesList = taskCompleteQuery.getAssignees(item.getTaskDefinitionKey());
            String[] assignees = new String[0];
            if (assigneesList == null) {
                Integer result = processInstanceService
                        .deleteProcessInstance(processInstanceId, "审批节点未分配审批人，流程直接中断取消");
                if (result > 0) throw new BusinessException("审批节点未分配审批人，流程直接中断取消");
            } else
                assignees = assigneesList.toArray(new String[0]);
            if (assignees.length == 1)
                taskService.setAssignee(item.getId(), assignees[0]);
            else {
                for (String assignee : assignees)
                    taskService.addCandidateUser(item.getId(), assignee);
            }
        }
        return true;
    }

    @Override
    public void turnTask(String taskId, String assignee) {
        org.activiti.api.task.model.Task task = taskRuntime.task(taskId);
        // 转办
        taskService.setAssignee(taskId, assignee);
        // 办理意见
        String message = String.format("%s 转办任务 [%s] 至 %s",
                UserUtils.getUsername(),
                task.getName(),
                assignee);
        taskService.addComment(taskId, task.getProcessInstanceId(), message);
    }

    /**
     * 获取已处理的历史节点
     */
    @Override
    public List<Map<String, Object>> getBackHistoryTaskNode(String taskId) {
        log.info(taskId);
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(UserUtils.getUsername())
                .singleResult();
        if (task == null) throw new BusinessException("未查询到任务");

        try {
            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .finished()
                    .orderByHistoricTaskInstanceEndTime()
                    .asc()
                    .list();
            List<Map<String, Object>> records = new ArrayList<>();
            for (HistoricTaskInstance item : historicTaskInstanceList) {
                Map<String, Object> data = new HashMap<>();
                data.put("activityId", item.getTaskDefinitionKey());
                data.put("activityName", item.getName());
                records.add(data);
            }
            // 去重 by activityName
            return DataFilterUtils.listDistinctByMapValue(records, "activityName");
        } catch (NullPointerException e) {
            throw new NullPointerException("查询驳回节点失败");
        }
    }

    /**
     * 驳回至指定的任务节点
     */
    @Override
    public void backTargetTask(String taskId, String targetTaskId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(UserUtils.getUsername())
                .singleResult();
        log.info(String.valueOf(task));
        if (task == null) throw new BusinessException("当前任务不存在");
        String processDefinitionId = task.getProcessDefinitionId();
        String processInstanceId = task.getProcessInstanceId();
        String taskDefinitionKey = task.getTaskDefinitionKey();

        /* 当前节点出口改至目标节点 */
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        // 1.当前节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(taskDefinitionKey);
        // 当前节点出口连线
        List<SequenceFlow> outgoingFlows = currentFlowNode.getOutgoingFlows();
        List<SequenceFlow> tempFlows = new ArrayList<>(outgoingFlows);// 存储当前节点出口连线
        outgoingFlows.clear();
        // 2.目标节点
        FlowNode targetFlowNode = (FlowNode) bpmnModel.getFlowElement(targetTaskId);
        // 目标节点入口连线
        List<SequenceFlow> incomingFlows = targetFlowNode.getIncomingFlows();
        // 存储所有当前节点指向目标节点的出口连线
        List<SequenceFlow> allTargetOutgoingFlow = new ArrayList<>();
        for (SequenceFlow incomingFlow : incomingFlows) {
            // 找到入口连线的源头（获取目标节点的父节点）
            FlowNode source = (FlowNode) incomingFlow.getSourceFlowElement();
            List<SequenceFlow> sequenceFlows;
            // 并行网关: 获取目标节点的父节点（并行网关）的所有出口
            if (source instanceof ParallelGateway)
                sequenceFlows = source.getOutgoingFlows();
            else // 其他类型父节点： 则获取目标节点的入口连线
                sequenceFlows = targetFlowNode.getIncomingFlows();
            allTargetOutgoingFlow.addAll(sequenceFlows);
        }

        // 3.当前节点出口指向目标节点
        currentFlowNode.setOutgoingFlows(allTargetOutgoingFlow);

        /* 完成当前任务，流程流向目标节点 */
        // 根据当前节点的流程实例 id，获取同一并行任务区间的未完成任务（包括当前任务）
        List<Task> uncompletedTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        uncompletedTaskList.forEach(item -> {
            // 当前节点
            if (taskId.equals(item.getId())) {
                String message = String.format(
                        "%s 驳回任务 %s => %s", UserUtils.getUsername(), task.getName(), targetFlowNode.getName());
                taskService.addComment(item.getId(), processInstanceId, message);
                // 完成当前任务，当前节点流向目标节点，产生目标节点的任务数据
                taskService.complete(taskId);
                DeleteExecutionCommand deleteExecutionCommand = new DeleteExecutionCommand(item.getExecutionId());
                managementService.executeCommand(deleteExecutionCommand);
                System.out.println();
                // 非当前节点
            } else {
                // 删除其他未完成的并行任务
                DeleteTaskCommand deleteTaskCommand = new DeleteTaskCommand(item.getId());
                managementService.executeCommand(deleteTaskCommand);
            }
        });

        /* 恢复当前节点的原出口指向 */
        currentFlowNode.setOutgoingFlows(tempFlows);

        /* 设置目标节点历史办理人 */
        List<Task> targetTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        targetTaskList.forEach(item -> {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .taskName(item.getName())
                    .finished()
                    .orderByHistoricTaskInstanceStartTime()
                    .asc()
                    .listPage(0, 1)
                    .get(0);
            taskService.claim(item.getId(), historicTaskInstance.getAssignee());
        });
    }

    /**
     * 获取已完成的任务节点
     */
    @Override
    public Map<String, Object> getCompletedTaskList(TaskQueryInfo taskQueryInfo) {
        String taskName = taskQueryInfo.getTaskName();
        String assignee = UserUtils.getUsername();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
//                .taskAssignee(assignee) // 候选人或者办理人
                .orderByTaskCreateTime()
                .desc()
                .finished();

        if (!StringUtils.isEmpty(taskName))
            query.taskNameLikeIgnoreCase("%" + taskName + "%");
        // 获取结果
        List<HistoricTaskInstance> taskList =
                query.listPage(taskQueryInfo.getFirstResult(), taskQueryInfo.getSize());
        // 总记录数量
        long total = query.count();
        log.info(String.valueOf(query.count()));

        // 数据处理
        List<Map<String, Object>> records = new ArrayList<>();
        for (HistoricTaskInstance task : taskList) {
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", task.getId());
            data.put("taskName", task.getName());// 任务名称，当前节点名称
            data.put("taskCreateTime", DateUtils.format(task.getCreateTime()));// 任务开始日期
            data.put("taskEndTime", DateUtils.format(task.getEndTime()));
            data.put("processInstanceId", task.getProcessInstanceId());
            data.put("processDefinitionId", task.getProcessDefinitionId());

            // 查询流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();
            data.put("processName", historicProcessInstance.getProcessDefinitionName());
            data.put("version", historicProcessInstance.getProcessDefinitionVersion());
            data.put("proposer", historicProcessInstance.getStartUserId());// 发起者
            data.put("businessKey", historicProcessInstance.getBusinessKey());

            records.add(data);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);

        return result;


    }

    /*
     * 判断当前节点的下一节点是人工任务的集合
     * */
    public void getNextNodes(FlowElement flowElement, List<Map<String, Object>> nextNodes) {
        // 获取当前节点的连线信息
        List<SequenceFlow> outgoingFlows = ((FlowNode) flowElement).getOutgoingFlows();

        for (SequenceFlow outgoingFlow : outgoingFlows) {
            // 下一节点的目标元素
            FlowElement nextFlowElement = outgoingFlow.getTargetFlowElement();
            if (nextFlowElement instanceof UserTask) {
                // 用户任务，则获取响应给前端设置办理人或者候选人
                Map<String, Object> node = new HashMap<>();
                node.put("id", nextFlowElement.getId());
                node.put("nodeName", nextFlowElement.getName());
                nextNodes.add(node);
            } else if (nextFlowElement instanceof ParallelGateway // 并行网关
                    || nextFlowElement instanceof ExclusiveGateway) { // 排他网关
                getNextNodes(nextFlowElement, nextNodes);
            } else if (nextFlowElement instanceof EndEvent) { // 结束节点
                break;
            }
        }
    }
}
