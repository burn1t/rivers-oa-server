package com.ocean.service;

import com.ocean.entity.Leave;
import com.ocean.query.LeaveQuery;

import java.util.Map;


public interface LeaveService {
    int addLeave(Leave leave);

    Map<String, Object> getLeaveList(LeaveQuery leaveQuery);

    Leave getLeave(String id);

    int updateLeave(Leave leave);
}
