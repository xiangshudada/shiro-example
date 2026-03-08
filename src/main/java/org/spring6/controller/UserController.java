package org.spring6.controller;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.spring6.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description:
 * @Version: 1.0
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户注册接口
     *
     * @param username 用户名
     * @param password 密码
     * @return SUCCESS-成功，USERNAME_EXISTS-用户名已存在，FAIL-失败
     */
    @PostMapping("/register")
    public String register(String username, String password) {
        return userService.register(username, password);
    }

    @PostMapping("/login")
    public String login(String username,String password,String rememberMe){
        // 执行Shiro的认证操作
        //1. 直接基于SecurityUtils获取subject主体,不需要手动的将SecurityManager和SecurityUtils手动整合，Spring已经奥丁
        Subject subject = SecurityUtils.getSubject();

        //2. 发起认证
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(rememberMe != null && "on".equals(rememberMe));
            subject.login(token);
            return "SUCCESS";
        } catch (UnknownAccountException exception){
            return "username fail!!!";
        } catch (IncorrectCredentialsException exception){
            return "password fail!!!";
        } catch (AuthenticationException e) {
            return "donot know...!!!";
        }
    }

    @GetMapping("/test")
    public String Test(){
        return "hellow !";
    }

}
