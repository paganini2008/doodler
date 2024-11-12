package com.github.doodler.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: ApiResult
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@ApiModel(description = "Unified Restful API Response Object")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    @ApiModelProperty("1:success 0:failure")
    private int code;

    @ApiModelProperty("Return message")
    private String msg;

    @ApiModelProperty("Return value")
    private T data;

    @ApiModelProperty("Time consuming")
    private long elapsed;

    @ApiModelProperty("Request path")
    private String requestPath;

    @JsonIgnore
    public boolean ifPresent() {
        return code == 1;
    }

    public static <T> ApiResult<T> ok() {
        return ok("ok", null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return ok("ok", data);
    }

    public static <T> ApiResult<T> ok(String msg, T data) {
        ApiResult<T> rs = new ApiResult<T>();
        rs.setCode(1);
        rs.setMsg(msg);
        rs.setData(data);
        return rs;
    }

    public static <T> ApiResult<T> failed() {
        return failed("failed");
    }

    public static <T> ApiResult<T> failed(String msg) {
        return failed(msg, null);
    }

    public static <T> ApiResult<T> failed(String msg, T data) {
        return failed(msg, 0, data);
    }

    public static <T> ApiResult<T> failed(String msg, int code) {
        return failed(msg, code, null);
    }

    public static <T> ApiResult<T> failed(String msg, int code, T data) {
        ApiResult<T> rs = new ApiResult<T>();
        rs.setCode(code);
        rs.setMsg(msg);
        rs.setData(data);
        return rs;
    }
}