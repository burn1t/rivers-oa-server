package com.ocean.service;

import com.ocean.query.ProcessInstanceQuery;
import com.ocean.query.StartupProcessInstanceQuery;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ProcessInstanceService {

    int startupProcessInstance(StartupProcessInstanceQuery startupQuery);

    Integer cancelProcessInstance(String business,
                                  String processInstanceId,
                                  String reason);

    Integer deleteProcessInstance(String processInstanceId, String reason);

    String getFormName(String processInstanceId);

    /**
     * @param processInstanceId 流程实例 id
     * @return List
     */
    List<Map<String, Object>> getTaskHistoryInfoList(String processInstanceId) throws Exception;

    void getProcessHistoryImg(String processInstanceId, HttpServletResponse response) throws Exception;

    Map<String, Object> getRunningProcessInstanceList(ProcessInstanceQuery processInstanceQuery);

    boolean updateProcessInstanceState(String processInstanceId);

    int deleteRunningProcessInstance(String processInstanceId);

    Map<String, Object> getFinishProcessInstanceList(ProcessInstanceQuery processInstanceQuery);

    int deleteFinishProcessInstanceHistory(String processInstanceId);
}
