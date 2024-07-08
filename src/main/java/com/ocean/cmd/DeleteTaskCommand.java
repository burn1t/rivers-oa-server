package com.ocean.cmd;


import com.ocean.utils.UserUtils;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;

public class DeleteTaskCommand extends NeedsActiveTaskCmd<String>{

    public DeleteTaskCommand(String taskId) {
        super(taskId);
    }

    @Override
    protected String execute(CommandContext commandContext, TaskEntity task) {
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        // 删除当前任务：不会把执行表中的 is_active_更新为0，会将任务数据更新到历史任务实例表中
        String message = String.format("%s任务被%s驳回", task.getName(), UserUtils.getUsername());
        taskEntityManager.deleteTask(task, message, false, false);
        return null;
    }
}
