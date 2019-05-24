package com.ej.restaurant.utils;

import com.ej.restaurant.enums.DLExceptionType;
import com.ej.restaurant.result.DLResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CommonExceptionHandler {

    Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DLResponseObject handleBindException(MethodArgumentNotValidException ex){
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("参数校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());

        return DLResponseObject.fromErrorType(DLExceptionType.COMMON_ILLEGAL_ARGUMENT);
    }

    @ExceptionHandler(BindException.class)
    public DLResponseObject handleBindException(BindException ex){
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("必填校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());
        return DLResponseObject.fromErrorType(DLExceptionType.COMMON_ILLEGAL_ARGUMENT);
    }

    @ExceptionHandler(DLException.class)
    public DLResponseObject exceptionHandler(DLException ex){
        return DLResponseObject.fromError(ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public DLResponseObject exceptionHandler(Exception e){
        log.error("unchecked exception:", e);
        return DLResponseObject.fromErrorType(DLExceptionType.COMMON_SERVER_ERROR);
    }
}
