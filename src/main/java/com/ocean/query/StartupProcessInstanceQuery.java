package com.ocean.query;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StartupProcessInstanceQuery {

    private String businessRoute;

    private String businessKey;

    private List<String> assignees;

    private Map<String, Object> variables;

    public Map<String, Object> getVariables() {
        return variables == null ? new HashMap<>() : variables;
    }
}
