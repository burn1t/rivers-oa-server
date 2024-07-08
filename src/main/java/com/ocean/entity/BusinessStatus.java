package com.ocean.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BusinessStatus implements Serializable {

    private String businessKey;// 业务 ID

    private String processInstanceId;// 流程实例 ID

    /*
    *   0-已撤回   1-待提交   2-处理中
    *   3-已完成   4-已作废   5-已删除
    * */
    private Integer status;// 流程状态

    private Date createDate;// 创建时间

    private Date updateDate;// 更新时间

}
