package com.ej.restaurant.controller;

import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public abstract class BaseController {

    protected void validate(BindingResult result){
        if(result.hasErrors()){
            List<FieldError> errorList = result.getFieldErrors();
            errorList.stream().forEach(item -> Assert.isTrue(false, item.getDefaultMessage()));
        }
    }
}
