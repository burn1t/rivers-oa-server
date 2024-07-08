package com.ocean.entity;

import com.ocean.utils.DateUtils;
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
public class ProcessConfig implements Serializable {

    private String id;

    private String processKey;

    private String businessRoute;

    private String formName;

    private Date createDate;

    private Date updateDate;

    public String getCreateDateStr() {
        if(createDate == null) {
            return "";
        }
        return DateUtils.format(createDate);
    }

    public String getUpdateDateStr() {
        if(updateDate == null) {
            return "";
        }
        return DateUtils.format(updateDate);
    }

}
