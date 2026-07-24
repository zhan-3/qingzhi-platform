package com.zhan.qingzhiplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果类
 * code: 1成功 0失败
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Integer code;//1成功 2失败
    private String msg;//错误信息
    private Object data;//数据

    public static Result success(){
        Result result = new Result();
        result.code = 1;
        result.msg = "success";
        return result;
    }

    public static Result success(Object data){
        Result result = success();
        result.data = data;
        return  result;
    }

    public static Result error(String msg){
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }



}
