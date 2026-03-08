package org.spring6.controller;


import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/item")
public class ItemController {


    @GetMapping("/select")
    public String select(){
        return "item Select!!!";
    }

    @GetMapping("/delete")
    public String delete(){
        return "item Delete!!!";
    }

    @GetMapping("/update")
    @RequiresRoles(value = {"超级管理员","运营"})
    public String update(){
        return "item Update!!!";
    }

    @GetMapping("/insert")
    @RequiresRoles(value = {"超级管理员","运营"},logical = Logical.OR)
    public String insert(){
        return "item Update!!!";
    }

    //    @RequiresPermissions(value = "",logical = Logical.AND)

    @GetMapping("/rememberMe")
    public String rememberMe(){
        return "rememberMe!!!";
    }

    @GetMapping("/authentication")
    public String authentication(){
        return "authentication!!!";
    }
}
