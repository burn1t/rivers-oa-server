package com.ocean.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;

import java.io.Serializable;
import java.util.List;

public class DeleteExecutionCommand implements Command<String>, Serializable {

    private final String executionId;

    public DeleteExecutionCommand(String executionId) {
        this.executionId = executionId;
    }

    @Override
    public String execute(CommandContext commandContext) {
        /* 删除同一个并行区间的已执行的执行数据 */
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        // 获取当前执行数据
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        // 通过当前执行数据的父执行，查询所有子执行数据
        List<ExecutionEntity> allChildrenExecution =
                executionEntityManager.collectChildren(executionEntity.getParent());
        // 删除已执行的执行数据 is_active_=0
        allChildrenExecution.forEach(item -> { if(!item.isActive()) executionEntityManager.delete(item); });
        return null;
    }
}
