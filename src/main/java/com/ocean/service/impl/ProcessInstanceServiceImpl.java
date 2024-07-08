package com.ocean.service.impl;

import com.ocean.activiti.image.CustomProcessDiagramGenerator;
import com.ocean.dao.BusinessStatusDao;
import com.ocean.dao.ProcessConfigDao;
import com.ocean.entity.BusinessStatus;
import com.ocean.entity.ProcessConfig;
import com.ocean.enums.BusinessStatusEnum;
import com.ocean.exception.BusinessException;
import com.ocean.query.ProcessInstanceQuery;
import com.ocean.query.StartupProcessInstanceQuery;
import com.ocean.service.ProcessInstanceService;
import com.ocean.utils.DateUtils;
import com.ocean.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.impl.identity.Authentication;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProcessInstanceServiceImpl extends ActivitiService implements ProcessInstanceService {

    private final ProcessConfigDao processConfigDao;

    private final BusinessStatusDao businessStatusDao;

    public ProcessInstanceServiceImpl(ProcessConfigDao processConfigDao, BusinessStatusDao businessStatusDao) {
        this.processConfigDao = processConfigDao;
        this.businessStatusDao = businessStatusDao;
    }

    @Override
    public int startupProcessInstance(StartupProcessInstanceQuery startupQuery) {
        String businessRoute = startupQuery.getBusinessRoute();
        String businessKey = startupQuery.getBusinessKey();
        List<String> assignees = startupQuery.getAssignees();
        Map<String, Object> variables = startupQuery.getVariables();
        String processKey;
        String processInstanceId;
        // 获取流程配置信息
        Authentication.setAuthenticatedUserId(UserUtils.getUsername());
        ProcessConfig processConfig = processConfigDao.selectProcessConfigByBusinessRoute(businessRoute);
        processKey = processConfig.getProcessKey();
        variables.put("formName", processConfig.getFormName());// 流程变量添加，用于后续查询历史审批记录
        if ("leave".equals(businessRoute)) {
            variables.put("entity.duration", variables.get("entity"));
        }
        // 启动流程实例
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(processKey, businessKey, variables);
        processInstanceId = processInstance.getProcessInstanceId();
        runtimeService.setProcessInstanceName(processInstanceId, processInstance.getProcessDefinitionName());
        // 设置任务办理人
        List<Task> taskList = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        for (Task task : taskList) {
            String taskId = task.getId();
            // 单个办理人，设置为办理人
            if (assignees.size() == 1) taskService.setAssignee(taskId, assignees.get(0));
            // 多个办理人，设置为候选人
            else for (String assignee : assignees) {taskService.addCandidateUser(taskId, assignee);}
        }
        // 更新业务状态；状态变更为办理中、添加 processInstance Id
        return businessStatusDao.updateState(businessKey,
                BusinessStatusEnum.PROCESS.getCode(),
                processInstanceId);
    }

    @Override
    public Integer cancelProcessInstance(String businessKey, String processInstanceId, String reason) {
        String username = UserUtils.getUsername();
        // 删除流程实例
        runtimeService.deleteProcessInstance(processInstanceId,
                username + "撤回当前申请：" + reason);
        // 删除历史记录
        historyService.deleteHistoricProcessInstance(processInstanceId);
        historyService.deleteHistoricTaskInstance(processInstanceId);
        // 更新业务状态
        return businessStatusDao.updateState(businessKey,
                BusinessStatusEnum.CANCEL.getCode(),
                "");
    }

    /**
     * 删除流程实例
     */
    @Override
    public Integer deleteProcessInstance(String processInstanceId, String reason) {
        // 删除流程实例
        runtimeService.deleteProcessInstance(processInstanceId, reason);
        // 获取任务对象
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .singleResult();
        String businessKey = historicProcessInstance.getBusinessKey();
        return businessStatusDao.updateState(businessKey, BusinessStatusEnum.CANCEL.getCode(), "");
    }

    @Override
    public String getFormName(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .includeProcessVariables()
                .processInstanceId(processInstanceId)
                .singleResult();
        return (String) historicProcessInstance.getProcessVariables().get("formName");
    }

    /**
     * 获取流程实例的各节点历史信息
     */
    @Override
    public List<Map<String, Object>> getTaskHistoryInfoList(String processInstanceId) {
        List<Map<String, Object>> records = new ArrayList<>();
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list();
        for (HistoricTaskInstance historicTask : list) {
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", historicTask.getId());
            data.put("taskName", historicTask.getName());
            data.put("processInstanceId", historicTask.getProcessInstanceId());
            data.put("startTime", DateUtils.format(historicTask.getStartTime()));
            data.put("endTime", DateUtils.format(historicTask.getEndTime()));
            data.put("status", historicTask.getEndTime() == null ? "待处理" : "已处理");
            data.put("assignee", historicTask.getAssignee());
            data.put("description", historicTask.getDescription());
            String message = historicTask.getDeleteReason();// 撤回理由
            if (StringUtils.isEmpty(message)) {
                // 根据节点 id 获取对应节点
                List<Comment> taskComments = taskService.getTaskComments(historicTask.getId());
                message = taskComments.stream()
                        .map(Comment::getFullMessage)
                        .collect(Collectors.joining("。"));
            }
            data.put("message", message);
            records.add(data);
        }
        return records;
    }

    @Override
    public void getProcessHistoryImg(String processInstanceId, HttpServletResponse response) {
        InputStream inputStream = null;
        try {
            // 查询流程实例历史数据
            HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            // 查询流程实例已执行节点
            List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceStartTime()
                    .desc()
                    .list();
            // 提取已执行执行节点 id
            List<String> highLightedActivityIdList = activityInstanceList.stream()
                    .map(HistoricActivityInstance::getActivityId)
                    .collect(Collectors.toList());
            // 查询流程实例正在运行节点
            List<Execution> runningActivityInstanceList = runtimeService.createExecutionQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            // 提取正在执行节点 id
            List<String> runningActivitiIdList = new ArrayList<>();
            for (Execution execution : runningActivityInstanceList) {
                if (StringUtils.isNotEmpty(execution.getActivityId()))
                    runningActivitiIdList.add(execution.getActivityId());
            }
            /* 获取历史流程图 */
            // 获取流程定义 Model，实例化流程图生成器
            BpmnModel bpmnModel = repositoryService.getBpmnModel(instance.getProcessDefinitionId());
            CustomProcessDiagramGenerator generator = new CustomProcessDiagramGenerator();
            // 获取高亮连线
            List<String> highLightedFlows = generator.getHighLightedFlows(bpmnModel, activityInstanceList);
            // 生成历史流程图
            inputStream = generator.generateDiagramCustom(bpmnModel, highLightedActivityIdList,
                    runningActivitiIdList, highLightedFlows,
                    "宋体", "微软雅黑", "黑体");
            /* 响应图片 */
            response.setContentType("image/svg+xml");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    @Override
    public Map<String, Object> getRunningProcessInstanceList(ProcessInstanceQuery processInstanceQuery) {
        String processName = processInstanceQuery.getProcessName();
        String proposer = processInstanceQuery.getProposer();
        org.activiti.engine.runtime.ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        if (StringUtils.isNotEmpty(processName))
            query.processInstanceNameLikeIgnoreCase(processName);
        if (StringUtils.isNotEmpty(proposer))
            query.startedBy(proposer);

        List<ProcessInstance> instanceList =
                query.listPage(processInstanceQuery.getFirstResult(), processInstanceQuery.getSize());
        long total = query.count();

        List<Map<String, Object>> records = new ArrayList<>();
        for (ProcessInstance instance : instanceList) {
            Map<String, Object> data = new HashMap<>();
            data.put("processInstanceId", instance.getProcessInstanceId());
            data.put("processInstanceName", instance.getName());
            data.put("processKey", instance.getProcessDefinitionKey());
            data.put("version", instance.getProcessDefinitionVersion());
            data.put("proposer", instance.getStartUserId());
            data.put("processStatus", instance.isSuspended());
            data.put("businessKey", instance.getBusinessKey());
            data.put("startTime", DateUtils.format(instance.getStartTime()));

            // 查询当前实例的当前任务
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).list();
            String currentTaskInfo = ""; // 当前任务
            for (Task task : taskList) {
                currentTaskInfo += String.format("[%s] 办理任务 [%s]", task.getAssignee(), task.getName());
            }
            data.put("currentTaskInfo", currentTaskInfo);
            records.add(data);
        }

        records.sort((former, latter) ->
                ((String) former.get("startTime")).compareTo((String) latter.get("startTime")));

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);
        return result;
    }


    @Override
    public boolean updateProcessInstanceState(String processInstanceId) {
        log.info(processInstanceId);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (Objects.isNull(processInstance)) throw new BusinessException("当前流程实例不存在");
        if (processInstance.isSuspended())
            runtimeService.activateProcessInstanceById(processInstanceId);
        else
            runtimeService.suspendProcessInstanceById(processInstanceId);
        ProcessInstance updateProcessInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        return processInstance.isSuspended() != updateProcessInstance.isSuspended();
    }

    @Override
    public int deleteRunningProcessInstance(String processInstanceId) {
        int result = 0;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (Objects.isNull(processInstance)) throw new BusinessException("当前流程实例不存在");
        String message = String.format("[%s] 作废当前流程实例", UserUtils.getUsername());

        runtimeService.deleteProcessInstance(processInstanceId, message);
        ProcessInstance existProcessInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (Objects.isNull(existProcessInstance))
            result = businessStatusDao.updateState(processInstance.getBusinessKey(),
                    BusinessStatusEnum.INVALID.getCode(), "");
        return result;
    }

    @Override
    public Map<String, Object> getFinishProcessInstanceList(ProcessInstanceQuery processInstanceQuery) {
        log.info(String.valueOf(processInstanceQuery));
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .finished().orderByProcessInstanceEndTime().desc();

        if (StringUtils.isNotEmpty(processInstanceQuery.getProcessName()))
            query.processInstanceNameLikeIgnoreCase(processInstanceQuery.getProcessName());

        if (StringUtils.isNotEmpty(processInstanceQuery.getProposer()))
            query.startedBy(processInstanceQuery.getProposer());

        List<HistoricProcessInstance> instanceList =
                query.listPage(processInstanceQuery.getFirstResult(), processInstanceQuery.getSize());
        long total = query.count();

        List<Map<String, Object>> records = new ArrayList<>();
        for (HistoricProcessInstance instance : instanceList) {
            Map<String, Object> data = new HashMap<>();
            data.put("processInstanceId", instance.getId());
            data.put("processInstanceName", instance.getName());
            data.put("processKey", instance.getProcessDefinitionKey());
            data.put("version", instance.getProcessDefinitionVersion());
            data.put("proposer", instance.getStartUserId());
            data.put("businessKey", instance.getBusinessKey());
            data.put("startTime", DateUtils.format(instance.getStartTime()));
            data.put("endTime", DateUtils.format(instance.getEndTime()));
            data.put("deleteReason", instance.getDeleteReason());
          BusinessStatus businessStatus = businessStatusDao.getStatusByBusinessKey(instance.getBusinessKey());
          if (!Objects.isNull(businessStatus))
              data.put("status", businessStatus.getStatus());
          records.add(data);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);
        return result;
    }

    @Override
    public int deleteFinishProcessInstanceHistory(String processInstanceId) {
        log.info(processInstanceId);
        int result = 0;
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        historyService.deleteHistoricProcessInstance(processInstanceId);
        historyService.deleteHistoricTaskInstance(processInstanceId);

        HistoricProcessInstance existProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (Objects.isNull(existProcessInstance))
            result = businessStatusDao.updateState(instance.getBusinessKey(),
                    BusinessStatusEnum.DELETE.getCode(), "");
        return result;
    }


}
