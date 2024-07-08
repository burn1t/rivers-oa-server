package com.ocean.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ocean.exception.BusinessException;
import com.ocean.query.ModelAddQuery;
import com.ocean.query.ModelQuery;
import com.ocean.service.ModelService;
import com.ocean.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ModelServiceImpl extends ActivitiService implements ModelService {

    @Override
    public void addModel(ModelAddQuery modelAddQuery) {
        /* 初始空模型 */
        Model model = repositoryService.newModel();
        model.setName(modelAddQuery.getName());
        model.setKey(modelAddQuery.getKey());
        model.setVersion(-1);
        /* 保存流程定义模型的基本数据信息 */
        // 封装模型 JSON 对象
        ObjectNode objectNode  = objectMapper.createObjectNode();
        objectNode.put(ModelDataJsonConstants.MODEL_NAME, modelAddQuery.getName());
        objectNode.put(ModelDataJsonConstants.MODEL_REVISION, 0);
        objectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, modelAddQuery.getDescription());
        model.setMetaInfo(objectNode.toString());
        repositoryService.saveModel(model);// act_re_model
        /* 保存流程定义模型的二进制数据 */
        // 封装模型对象二进制数据 JSON 对象
        ObjectNode editorNode = objectMapper.createObjectNode();
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "https://b3mn.org/stencilset/bpmn2.0#");
        editorNode.replace("stencilset", stencilSetNode);
        ObjectNode propertiesNode = objectMapper.createObjectNode();
        propertiesNode.put("process_id", modelAddQuery.getKey());
        editorNode.replace("properties", propertiesNode);
        repositoryService.addModelEditorSource(model.getId(),
                editorNode.toString().getBytes(StandardCharsets.UTF_8));// act_ge_bytearray
    }

    @Override
    public Map<String, Object> getModelList(ModelQuery modelQuery) {
        org.activiti.engine.repository.ModelQuery query = repositoryService.createModelQuery();
        if (!StringUtils.isEmpty(modelQuery.getName()))
            query.modelNameLike("%" + modelQuery.getName() + "%");
        if (!StringUtils.isEmpty(modelQuery.getKey()))
            query.modelKey(modelQuery.getKey());
        List<Model> modelList = query.orderByCreateTime()
                .desc().listPage(modelQuery.getFirstResult(), modelQuery.getSize());
        long total = query.count();
        // 数据转换: modelList ---> Map<String, Object> 存入 List
        List<Map<String, Object>> records = new ArrayList<>();
        for (Model model : modelList) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", model.getId());
            data.put("key", model.getKey());
            data.put("name", model.getName());
            data.put("version", model.getVersion());
            // JSON ---> String
            String desc = JSONObject.parseObject(model.getMetaInfo())
                    .getString(ModelDataJsonConstants.MODEL_DESCRIPTION);
            data.put("description", desc);
            data.put("createDate", DateUtils.format(model.getCreateTime()));
            data.put("updateDate", DateUtils.format(model.getLastUpdateTime()));
            records.add(data);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);
        return result;
    }

    @Override
    public void deployModel(String modelId) {
        /* 获取流程定义模型 JSON 字节码 */
        byte[] jsonBytes = repositoryService.getModelEditorSource(modelId);
        if(ArrayUtils.isEmpty(jsonBytes)) throw new BusinessException("模型数据为空，请先设计流程定义模型，再进行部署");
        // JSON 字节码 ---> XML 字节码，bpmn 2.0 规范流程模型的描述是 XML 格式
        byte[] xmlBytes;
        try {
            xmlBytes = bpmnJsonXmlBytes(jsonBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(ArrayUtils.isEmpty(xmlBytes)) throw new BusinessException("数据模型不符合要求，请至少设计一条主线流程");
        /* 获取流程定义模型的图片字节码 */
        byte[] pngBytes = repositoryService.getModelEditorSourceExtra(modelId);
        Model model = repositoryService.getModel(modelId);
        // XML 资源的名称：act_ge_bytearray 表 name_ 字段
        String processXmlName = model.getName() + ".bpmn20.xml";
        // 图片资源名称：act_ge_bytearray 表 name_ 字段
        String processPngName = model.getName() + "." + model.getKey() + ".png";

        /* 部署流程定义 */
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .addString(processXmlName, new String(xmlBytes, StandardCharsets.UTF_8))
                .addBytes(processPngName, pngBytes)
                .deploy();
        // 更新部署 ID 至流程定义模型数据表
        model.setDeploymentId(deployment.getId());
        repositoryService.saveModel(model);
    }

    @Override
    public void exportZip(String modelId, HttpServletResponse response) {
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(response.getOutputStream());
            String zipName = "模型不存在";
            Model model = repositoryService.getModel(modelId);
            if(model != null) {
                zipName = model.getName() + "." + model.getKey() + ".zip";// 压缩包名
                // 流程定义模型的 JSON 字节码
                byte[] bpmnJsonBytes = repositoryService.getModelEditorSource(modelId);
                byte[] xmlBytes = bpmnJsonXmlBytes(bpmnJsonBytes);// JSON 字节码 ---> XML 字节码
                if(xmlBytes != null) {
                    // 1.XML添加至压缩包
                    zipOutputStream.putNextEntry(new ZipEntry(model.getName() + ".bpmn20.xml"));
                    zipOutputStream.write(xmlBytes);
                    // 流程定义模型的图片字节码
                    byte[] pngBytes = repositoryService.getModelEditorSourceExtra(modelId);
                    if(pngBytes != null) {
                        // 2.PNG 添加至压缩包
                        zipOutputStream.putNextEntry(
                                new ZipEntry(model.getName() + "." + model.getKey() + ".png"));
                        zipOutputStream.write(pngBytes);
                    }
                } else zipName = "模型数据为空，请先设计流程定义模型";
            }
            // 确保浏览器不解析 ZIP，将其作为二进制数据流处理
            response.setContentType("application/octet-stream");
            response.addHeader("Access-Control-Expose-Headers","Content-Disposition");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(zipName,"UTF-8"));
            response.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if(zipOutputStream != null) {
                try {
                    zipOutputStream.closeEntry();
                    zipOutputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    @Override
    public void deleteModel(String modelId) {
        repositoryService.deleteModel(modelId);
    }

    private byte[] bpmnJsonXmlBytes(byte[] jsonBytes) throws IOException {
        if(jsonBytes == null) return null;
        JsonNode jsonNode = objectMapper.readTree(jsonBytes);
        // JSON 字节码 ---> BpmnModel 对象
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
        // BpmnModel 对象 ---> XML 字节码
        if(bpmnModel.getProcesses().size() == 0) return null;
        return new BpmnXMLConverter().convertToXML(bpmnModel);
    }
}