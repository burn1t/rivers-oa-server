package com.ocean.entity;

import com.ocean.enums.BusinessStatusEnum;
import com.ocean.enums.LeaveTypeEnum;
import com.ocean.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Leave implements Serializable {

    private String id;

    private String username;// 申请人用户名

    private String title;// 标题

    /*
    *   1-病假    2-事假    3-年假    4-婚假
    *   5-产假    6-丧假    7-探亲    8-调休
    *   9-其他
    * */
    private Integer leaveType;// 请假类型

    private String leaveReason;// 请假事由

    private String principal;// 应急工作委托人

    private String contactPhone;// 休息时间联系人电话

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;// 请假开始时间

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;// 请假结束时间

    private Double duration;//请假时长 天

    private Date createDate;//创建时间

    private Date updateDate;//更新时间

    private String processInstanceId;//流程实例 ID，不存在至表

    private Integer status;//业务状态，不存在至表

    /**
     * 使用 Enum 处理映射关系
     * Integer 映射 字符串。用于前端展示
     * */
    public String getLeaveTypeStr() {
        if (this.leaveType == null) return "";
        return LeaveTypeEnum.getEumByCode(this.leaveType).getDesc();
    }

    public String getStartDateStr() {
        if (startDate == null) return "";
        return DateUtils.format(startDate, "yyyy-MM-dd");
    }

    public String getEndDateStr() {
        if (endDate == null) return "";
        return DateUtils.format(endDate, "yyyy-MM-dd");
    }

    public String getCreateDateStr() {
        if (createDate == null) return "";
        return DateUtils.format(createDate);
    }

    public String getUpdateDateStr() {
        if (updateDate == null) return "";
        return DateUtils.format(updateDate);
    }

    public String getStatusStr() {
        if(this.status == null) return "";
        return BusinessStatusEnum.getEumByCode(this.status).getDesc();
    }

}
