package com.ocean.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ToString
public class ResultModel implements Serializable {

    private Integer code;// 状态码

    private String information;

    private Object result;

    public ResultModel() {}

    public static ResultModel resultModel = new ResultModel();

    public static ResultModel success() {
        resultModel.setCode(20000);
        resultModel.setInformation("success");
        resultModel.setResult(null);
        return resultModel;
    }

    public static ResultModel success(String information) {
        resultModel.setCode(20000);
        resultModel.setInformation(information);
        resultModel.setResult(null);
        return resultModel;
    }

    public static ResultModel success(Object result) {
        resultModel.setCode(20000);
        resultModel.setInformation("success");
        resultModel.setResult(result);
        return resultModel;
    }

    public static ResultModel success(String information, Object result) {
        resultModel.setCode(20000);
        resultModel.setInformation(information);
        resultModel.setResult(result);
        return resultModel;
    }

    public static ResultModel error() {
        resultModel.setCode(500);
        resultModel.setInformation("error");
        resultModel.setResult(null);
        return resultModel;
    }

    public static ResultModel error(String information) {
        resultModel.setCode(500);
        resultModel.setInformation(information);
        resultModel.setResult(null);
        return resultModel;
    }

    public static ResultModel error(int code, String information) {
        resultModel.setCode(code);
        resultModel.setInformation(information);
        resultModel.setResult(null);
        return resultModel;
    }
}
