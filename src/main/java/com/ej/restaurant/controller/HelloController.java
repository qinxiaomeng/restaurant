package com.ej.restaurant.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "消息", description = "消息操作 API", position = 100, protocols = "http")
@RestController
@RequestMapping("/hello")
public class HelloController {

    @ApiOperation(
            value = "消息列表",
            notes = "完整的消息内容列表",
            produces="application/json, application/xml",
            consumes="application/json, application/xml",
            response = String.class)
    @GetMapping("withoutParam")
    public String sayHello(){
        return "say hello without param";
    }
}
