package com.ocean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 *  1-病假  2-事假  3-年假  4-婚假
 *  5-产假  6-丧假  7-探亲  8-调休
 *  9-其他
 */
@Getter
@AllArgsConstructor
public enum LeaveTypeEnum {

    SickLeave(1, "病假"),
    AffairsLeave(2, "事假"),
    AnnualLeave(3, "年假"),
    MarriageLeave(4, "婚假"),
    MaternityLeave(5, "产假"),
    BereavementLeave(6, "丧假"),
    FamilyVisit(7, "探亲"),
    AdjustmentLeave(8, "调休"),
    OTHER(9, "其他"), ;

    private final Integer code;
    private final String desc;

    /**
     * 根据 code 返回枚举值
     * @return LeaveTypeEnum
     */
    public static LeaveTypeEnum  getEumByCode(Integer code){
        if(code == null) return null;
        for(LeaveTypeEnum typeEnum: LeaveTypeEnum.values()) {
            if(Objects.equals(typeEnum.getCode(), code))
                return typeEnum;
        }
        return null;
    }

}
