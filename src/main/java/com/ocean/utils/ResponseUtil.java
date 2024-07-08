package com.ocean.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocean.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void out(HttpServletResponse response, ResultModel resultModel) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            String json = objectMapper.writeValueAsString(resultModel);
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BaseResponse<?> success() {
        return new BaseResponse<>(20000, "success");
    }

    public static BaseResponse<?> success(String information) {
        return new BaseResponse<>(20000, information);
    }

    public static <T> BaseResponse<T> success(T result) {
        return new BaseResponse<>(20000, "success", result);
    }

    public static <T> BaseResponse<T> success(String information, T result) {
        return new BaseResponse<>(20000, information, result);
    }

    public static BaseResponse<?> error() {
        return new BaseResponse<>(5000, "系统内部异常");
    }

    public static BaseResponse<?> error(String information) {
        return new BaseResponse<>(5000, information);
    }

    public static BaseResponse<?> error(int code, String information) {
        return new BaseResponse<>(code, information);
    }
}
