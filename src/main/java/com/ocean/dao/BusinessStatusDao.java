package com.ocean.dao;

import com.ocean.entity.BusinessStatus;
import org.apache.ibatis.annotations.Param;

public interface BusinessStatusDao {

    Integer insertByBusinessKey(BusinessStatus businessStatus);

    Integer updateState(@Param("businessKey") String businessKey,
                        @Param("status") Integer status,
                        @Param("processInstanceId") String processInstanceId);
    BusinessStatus getStatusByBusinessKey(String businessKey);
}
