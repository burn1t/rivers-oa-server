package com.ocean.service;

import com.ocean.entity.ProcessConfig;
import com.ocean.utils.ResultModel;

public interface ProcessConfigService {

    /**
     * 根据流程定义标识获取流程配置数据
     * @param processKey 过程关键
     * @return {@link ProcessConfig}
     */
    ProcessConfig getProcessConfigByProcessKey(String processKey);

    /**
     * 保存或更新流程配置
     * @param processConfig 流程配置
     * @return {@link Integer}
     */
    Integer saveOrUpdateProcessConfig(ProcessConfig processConfig);

    /**
     * 根据流程定义标识删除流程配置数据
     * @param processKey 过程关键
     * @return {@link ResultModel}
     */
    Integer deleteProcessConfig(String processKey);




}
