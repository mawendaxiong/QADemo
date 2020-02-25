package com.wjj.demo.controller;

import com.wjj.demo.service.QAService;
import com.wjj.demo.vo.Person;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(description = "调试接口")
public class TestController {
    @Autowired
    private QAService service;

    @GetMapping("lookPerson")
    @ApiOperation("查看内存中的用户")
    public String lookPerson(){
        return service.lookPerson();
    }

    @GetMapping("lookSession")
    @ApiOperation("查看session中的用户")
    public String lookSession(HttpServletRequest request){
        Object account = request.getSession().getAttribute("account");
        if (account instanceof Person){
            Person account1 = (Person) account;
            return account1.getAccount()+"---"+account1.getName();
        }
        return "查询不到用户";
    }
}
