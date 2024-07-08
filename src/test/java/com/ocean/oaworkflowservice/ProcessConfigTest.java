package com.ocean.oaworkflowservice;

import com.ocean.dao.ProcessConfigDao;
import com.ocean.entity.ProcessConfig;
import com.ocean.service.ProcessConfigService;
import com.ocean.service.impl.ProcessConfigServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProcessConfigTest {


    private final ProcessConfigDao configDao;

    @Autowired
    public ProcessConfigTest(ProcessConfigDao configDao) {
        this.configDao = configDao;
    }

//    @Autowired
//    private ProcessConfigDao processConfigDao;
//
//    @Autowired
//    private ProcessConfigService processConfigService;
//
//    @Test
//    void getProcessConfig() {
//        System.out.println(processConfigService.getProcessConfig("1"));
//    }
//
//    @Test
//    void deleteProcessConfig() {
//        System.out.println(processConfigService.deleteProcessConfig("1"));
//    }
//
//    @Test
//    void saveOrUpdateProcessConfig() {
//        ProcessConfig processConfig = new ProcessConfig();
//        processConfig.setProcessKey("testProcess");
//        processConfig.setBusinessRoute("1");
//        processConfig.setFormName("1");
//        System.out.println(processConfigService.saveOrUpdateProcessConfig(processConfig));
//    }

    @Test
    void getProcessConfigByBusinessRoute_Dao() {
        System.out.println(configDao.selectProcessConfigByBusinessRoute("leave"));
    }

}
