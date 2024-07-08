package com.ocean.service;

import com.ocean.enums.BusinessStatusEnum;

public interface BusinessStatusService {

    /**
     * 新增数据
     * - 状态：待提交 - 1
     * @param businessKey 业务 ID
     * @return {@link Integer}
     */
    Integer addBusinessStatus(String businessKey);

    /*
    * 更新数据
    * */
    Integer updateBusinessStatus(String businessKey, Integer statusEnum);
}
