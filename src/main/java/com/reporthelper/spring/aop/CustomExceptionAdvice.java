package com.reporthelper.spring.aop;

import com.reporthelper.resp.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局异常处理器
 *
 * @author Myron Miao
 * @date 2018-12-28
 **/

@Slf4j
@ResponseBody
@ControllerAdvice
public class CustomExceptionAdvice extends ExceptionAdvice {
    /**
     * 401 - Unauthorized
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseResult handleUnauthorizedException(final UnauthorizedException e) {
        log.error("没有权限", e);
        return ResponseResult.error("401", "对不起!您没有权限,访问拒绝", e.toString());
    }
}