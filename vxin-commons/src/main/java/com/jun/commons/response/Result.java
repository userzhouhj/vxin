package com.jun.commons.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 *
 * 状态码 200 成功
 *        500 系统错误
 *        301 用户名密码为空
 *        401 密码错误
 *        402 用户名重复
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result<T> {

    private Integer code;

    private String msg;

    private T data;

    public Result(Integer code,String msg,T data){

        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> Result<T> ok(String msg,T data){
        return new Result<>(200,msg,data);
    }

    public static <T> Result<T> ok(String msg){
        return new Result<>(200,msg,null);
    }

    public static <T> Result<T> fail(Integer code,String msg){
        return new Result<>(code,msg,null);
    }
}
