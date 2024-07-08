package com.ocean.query;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TaskCompleteQuery implements Serializable {

    private String taskId;

    private String message;// 审批意见

    private Map<String, List<String>> assigneeMap;// 后续节点审批人

    public String getMessage() {
        return StringUtils.isEmpty(message) ? "审批通过" : message;
    }

    public List<String> getAssignees(String key) {
        if (assigneeMap == null) return null;
        return assigneeMap.get(key);
    }

    public Map<String, List<String>> getAssigneeMap() {
        return assigneeMap;
    }

}
