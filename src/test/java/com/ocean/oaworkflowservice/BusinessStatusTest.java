package com.ocean.oaworkflowservice;

import com.ocean.dao.BusinessStatusDao;
import com.ocean.service.BusinessStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessStatusTest {

    @Autowired
    private BusinessStatusDao statusDao;

    @Autowired
    private BusinessStatusService businessStatusService;
//
//    @Autowired
//    private BusinessStatusService businessStatusService;
//
//    @Test
//    void insert() {
//        System.out.println(businessStatusService.addBusinessStatus("1111"));
//    }

    @Test
    void updateStateForStartupProcessInstance_dao() {
        statusDao.updateState("1e635cdb109d4e859a6fed2e0449157f",
                2, "ldadsada");
    }

    @Test
    void updateStateForStartupProcessInstance_ser() {
//        Integer r = businessStatusService.updateBusinessStatus("3627e47fd6c84f6c8ba66008dcee2bdd",
//                1);
//        System.out.println(r);
    }
}
