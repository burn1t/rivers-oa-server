package com.ocean.service.impl;

import com.ocean.dao.BusinessStatusDao;
import com.ocean.entity.BusinessStatus;
import com.ocean.enums.BusinessStatusEnum;
import com.ocean.service.BusinessStatusService;
import com.ocean.utils.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BusinessStatusServiceImpl implements BusinessStatusService {

    private final BusinessStatusDao businessStatusDao;

    @Autowired
    public BusinessStatusServiceImpl(BusinessStatusDao businessStatusDao) {
        this.businessStatusDao = businessStatusDao;
    }

    @Override
    public Integer addBusinessStatus(String businessKey) {

        BusinessStatus businessStatus = new BusinessStatus();
        businessStatus.setBusinessKey(businessKey);
        businessStatus.setStatus(BusinessStatusEnum.WAIT.getCode());

        return businessStatusDao.insertByBusinessKey(businessStatus);
    }


    @Override
    public Integer updateBusinessStatus(String businessKey, Integer statusEnum) {
        return businessStatusDao.updateState(businessKey, statusEnum, "");
    }


}
