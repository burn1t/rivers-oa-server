package com.ocean.service;

import com.ocean.query.ModelAddQuery;
import com.ocean.query.ModelQuery;
import com.ocean.utils.ResultModel;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ModelService {
    /**
     * 新增模型
     */
    void addModel(ModelAddQuery modelAddQuery);
    /**
     * 分页查询模型列表
     */
    Map<String, Object> getModelList(ModelQuery modelQuery);
    /**
     * 部署流程定义
     */
    void deployModel(String modelId);
    /**
     * 导出模型 ZIP
     */
    void exportZip(String modelId, HttpServletResponse response);
    /**
     * 删除模型
     */
    void deleteModel(String modelId);
}
