package com.ocean.common;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


/**
 * @author Floyd
 * @date 2024/07/07
 */
@Data
@ToString
public class BaseResponse<T> implements Serializable {

    private int code;// 状态码

    private String information;

    private T result;

    public BaseResponse(int code, String information, T result) {
        this.code = code;
        this.information = information;
        this.result = result;
    }

    public BaseResponse(int code, String information) {
        this.code = code;
        this.information = information;
        this.result = null;
    }

//    public BaseResponse(int code, T result) {
//        this.code = code;
//        this.information = "";
//        this.result = result;
//    }
}
