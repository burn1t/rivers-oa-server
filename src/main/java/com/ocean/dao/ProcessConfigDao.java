package com.ocean.dao;

import com.ocean.entity.ProcessConfig;
import org.apache.ibatis.annotations.Param;

public interface ProcessConfigDao {

    ProcessConfig selectProcessConfigByProcessKey(String processKey);

    ProcessConfig selectProcessConfigByBusinessRoute(String businessRoute);

    Integer deleteProcessConfigByProcessKey(String processKey);

    Integer saveOrUpdateByProcessKey(@Param("processKey") String processKey,
                                     @Param("businessRoute") String businessRoute,
                                     @Param("formName") String formName);

}
