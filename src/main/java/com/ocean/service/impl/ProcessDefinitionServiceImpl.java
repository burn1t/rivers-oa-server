package com.ocean.service.impl;

import com.ocean.query.DefinitionQuery;
import com.ocean.entity.ProcessConfig;
import com.ocean.service.ProcessConfigService;
import com.ocean.service.ProcessDefinitionService;
import com.ocean.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class ProcessDefinitionServiceImpl extends ActivitiService implements ProcessDefinitionService {

    private final ProcessConfigService processConfigService;

    public ProcessDefinitionServiceImpl(ProcessConfigService processConfigService) {
        this.processConfigService = processConfigService;
    }

    @Override
    public Map<String, Object> getDefinitionList(DefinitionQuery definitionQuery) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        if (!StringUtils.isEmpty(definitionQuery.getName()))
            query.processDefinitionNameLike("%" + definitionQuery.getName() + "%");
        if (!StringUtils.isEmpty(definitionQuery.getKey()))
            query.processDefinitionKeyLike("%" + definitionQuery.getKey() + "%");
        List<ProcessDefinition> list = query.latestVersion().listPage(definitionQuery.getFirstResult(),
                definitionQuery.getSize());
        long total = query.count();

        List<Map<String, Object>> records = new ArrayList<>();
        for (ProcessDefinition definition : list) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", definition.getId());
            data.put("name", definition.getName());
            data.put("key", definition.getKey());
            data.put("version", definition.getVersion());
            data.put("state", definition.isSuspended());
            data.put("xmlName", definition.getResourceName());
            data.put("pngName", definition.getDiagramResourceName());
            data.put("deploymentId", definition.getDeploymentId());
            Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(definition.getDeploymentId())
                    .singleResult();
            data.put("deployDate", DateUtils.format(deployment.getDeploymentTime()));
            // 获取流程配置信息
            ProcessConfig processConfig = processConfigService.getProcessConfigByProcessKey(definition.getKey());
            if (processConfig != null) {
                data.put("businessRoute", processConfig.getBusinessRoute());
                data.put("formName", processConfig.getFormName());
            }
            records.add(data);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);
        return result;
    }

    @Override
    public Boolean updateProcessDefinitionState(String processDefinitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        if (definition.isSuspended())// true -> 挂起
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
        else
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
        return definition.isSuspended();
    }

    @Override
    public Integer deleteDeployment(String deploymentId, String processKey) {
        // 删除部署的流程定义
        repositoryService.deleteDeployment(deploymentId);
        // 查询 process key 对应的流程定义是否存在
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey).list();
        ProcessConfig processConfig = processConfigService.getProcessConfigByProcessKey(processKey);
        // process key 对应流程定义不存在，删除流程配置信息
        if (CollectionUtils.isEmpty(list) && !Objects.isNull(processConfig)) {
            return processConfigService.deleteProcessConfig(processKey);
        } else {
            return 1;
        }
    }

    @Override
    public void exportFile(String type, String definitionId, HttpServletResponse response) {
        try {
            ProcessDefinition processDefinition = repositoryService.getProcessDefinition(definitionId);
            String resourceName = "文件不存在";
            if ("xml".equals(type))
                resourceName = processDefinition.getResourceName();// XML 资源名
            else if ("png".equals(type))
                resourceName = processDefinition.getDiagramResourceName();// PNG 资源名
            // 资源输入流
            InputStream input = repositoryService.getResourceAsStream(
                    processDefinition.getDeploymentId(), resourceName);
            response.setContentType("application/octet-stream");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(resourceName, "UTF-8"));
            IOUtils.copy(input, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deployByFile(MultipartFile file) {
        String fullFilename = file.getOriginalFilename();// 文件名 + 后缀
        String suffix = null;// 后缀
        String filename = null;// 文件名
        if (fullFilename != null) {
            suffix = fullFilename.substring(fullFilename .lastIndexOf(".") + 1);
            filename = fullFilename.substring(0, fullFilename.indexOf("."));
        }
        InputStream input = null;
        try {
            input = file.getInputStream();
            DeploymentBuilder deployment = repositoryService.createDeployment();
            if ("zip".equals(suffix)) deployment.addZipInputStream(new ZipInputStream(input));
            else deployment.addInputStream(filename, input);// 要与资源名称对得上 bytearray
            deployment.name(filename);
            deployment.deploy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
