package com.ocean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum BusinessStatusEnum {

    CANCEL(0, "已撤回"),// 未分配办理人
    WAIT(1, "待提交"),
    PROCESS(2, "处理中"),
    FINISH(3, "已完成"),
    INVALID(4, "已作废"),// 即删除运行时流程实例
    DELETE(5, "已删除");// 删除历史流程实例

    private final Integer code;
    private final String desc;

    public static BusinessStatusEnum getEumByCode(Integer code){
        if(code == null) return null;
        for(BusinessStatusEnum typeEnum: BusinessStatusEnum.values()) {
            if(Objects.equals(typeEnum.getCode(), code)) {
                return typeEnum;
            }
        }
        return null;
    }

}
