package com.scuop.routeservice.util;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {
    @ExceptionHandler
    public SaResult doException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return SaResult.error(ex.getMessage());
    }
}
