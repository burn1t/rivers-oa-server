package com.ocean.service;

import com.ocean.query.DefinitionQuery;
import com.ocean.utils.ResultModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ProcessDefinitionService {
    /**
     * 分页获取流程定义列表
     */
    Map<String, Object> getDefinitionList(DefinitionQuery definitionQuery);
    /**
     * 挂起或激活流程定义状态
     */
    Boolean updateProcessDefinitionState(String processDefinitionId);
    /**
     * 删除流程定义
     */
    Integer deleteDeployment(String deploymentId, String processKey);
    /**
     * 导出文件
     */
    void exportFile(String type, String processDefinitionId, HttpServletResponse response);
    /**
     * 部署文件
     */
    void deployByFile(MultipartFile file);

}
