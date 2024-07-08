package com.ocean.oaworkflowservice;

import com.ocean.dao.LeaveDao;
import com.ocean.entity.Leave;
import com.ocean.query.LeaveQuery;
import com.ocean.service.impl.LeaveServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class LeaveTest {

    @Autowired
    private LeaveDao leaveDao;

    @Autowired
    private LeaveServiceImpl leaveService;

    @Test
    void insert() {
        Leave leave = new Leave();
        leave.setUsername("admin");
        leave.setId("122");
        leave.setLeaveType(1);
        try {
            System.out.println(leaveService.addLeave(leave));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void id() {
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
    }

    @Test
    void getLeaveListDao() {
        LeaveQuery leaveQuery = new LeaveQuery("admin", "1", 1);
        leaveQuery.setCurrent(0);
        leaveQuery.setSize(5);
        leaveQuery.setOffset(leaveQuery.getFirstResult());
//        System.out.println(leaveQuery);

        System.out.println(leaveDao.getLeaveAndStatusList(leaveQuery));
    }

    @Test
    void getLeaveListService() {
        LeaveQuery leaveQuery = new LeaveQuery("admin", "1", 1);
        leaveQuery.setCurrent(0);
        leaveQuery.setSize(5);
        leaveQuery.setOffset(leaveQuery.getFirstResult());
        System.out.println(leaveQuery);

        System.out.println(leaveService.getLeaveList(leaveQuery));
    }

    @Test
    void getLeaveById() {
//        System.out.println(leaveDao.getLeaveById("bb277f35ca644c7ebad171b9d19d82f7"));
        System.out.println(leaveService.getLeave("bb277f35ca644c7ebad171b9d19d82f7"));
    }

    @Test
    void updateLeaveById() {
        Leave leave = new Leave();
        leave.setId("7218c3a857054352a13a0f6536d46a48");
        leave.setContactPhone("123213123");
        leaveDao.updateLeaveById(leave);
    }

}
