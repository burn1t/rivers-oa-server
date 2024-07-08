package com.ocean.service.impl;

import com.ocean.dao.ProcessConfigDao;
import com.ocean.entity.ProcessConfig;
import com.ocean.service.ProcessConfigService;
import org.springframework.stereotype.Service;

@Service("processConfigService")
public class ProcessConfigServiceImpl extends ActivitiService implements ProcessConfigService {

    private final ProcessConfigDao processConfigDao;

    public ProcessConfigServiceImpl(ProcessConfigDao processConfigDao) {
        this.processConfigDao = processConfigDao;
    }

    @Override
    public ProcessConfig getProcessConfigByProcessKey(String processKey) {
        return processConfigDao.selectProcessConfigByProcessKey(processKey);
    }

    @Override
    public Integer saveOrUpdateProcessConfig(ProcessConfig processConfig) {
        String processKey = processConfig.getProcessKey();
        String businessRoute = processConfig.getBusinessRoute();
        String formName = processConfig.getFormName();
        return processConfigDao.saveOrUpdateByProcessKey(processKey, businessRoute, formName);
    }

    @Override
    public Integer deleteProcessConfig(String processKey) {
        return processConfigDao.deleteProcessConfigByProcessKey(processKey);
    }


}
