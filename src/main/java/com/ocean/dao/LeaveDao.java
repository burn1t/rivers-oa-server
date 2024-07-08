package com.ocean.dao;

import com.ocean.entity.Leave;
import com.ocean.query.LeaveQuery;

import java.util.List;

public interface LeaveDao {

    List<Leave> getLeaveAndStatusList(LeaveQuery leaveQuery);

    Leave getLeaveById(String id);

    Integer insertLeave(Leave leave);

    Integer updateLeaveById(Leave leave);

}
