package com.ocean.service.impl;

import com.ocean.dao.LeaveDao;
import com.ocean.entity.Leave;
import com.ocean.query.LeaveQuery;
import com.ocean.service.BusinessStatusService;
import com.ocean.service.LeaveService;
import com.ocean.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveDao leaveDao;

    private final BusinessStatusService businessStatusService;

    public LeaveServiceImpl(LeaveDao leaveDao, BusinessStatusService businessStatusService) {
        this.leaveDao = leaveDao;
        this.businessStatusService = businessStatusService;
    }

    @Override
    public int addLeave(Leave leave) {
        int result = 0;
        leave.setUsername(UserUtils.getUsername());
        leave.setId((UUID.randomUUID().toString().replace("-", "")));
        int flag = leaveDao.insertLeave(leave);
        if (flag > 0)  result = businessStatusService.addBusinessStatus(leave.getId());
        return result;
    }

    @Override
    public  Map<String, Object> getLeaveList(LeaveQuery leaveQuery) {
        if (StringUtils.isEmpty(leaveQuery.getUsername()))
            leaveQuery.setUsername(UserUtils.getUsername());
        leaveQuery.setOffset(leaveQuery.getFirstResult());
        List<Leave> leaveList = leaveDao.getLeaveAndStatusList(leaveQuery);
        int total = leaveList.size();

        Map<String, Object> result = new HashMap<>();
        result.put("records", leaveList);
        result.put("total", total);
        return result;
    }

    @Override
    public Leave getLeave(String id) {
        return leaveDao.getLeaveById(id);
    }

    @Override
    public int updateLeave(Leave leave) {
        return leaveDao.updateLeaveById(leave);
    }
}
