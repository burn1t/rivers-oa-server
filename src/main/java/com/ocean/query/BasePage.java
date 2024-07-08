package com.ocean.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

// 处理 Activiti 的分页，分页请求基础类
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BasePage {

    public int current;

    public int size;

    public int offset;

    public Integer getFirstResult() {
        if (current > 0)
            return (current - 1) * size;
        else
            return 0;
    }


}
