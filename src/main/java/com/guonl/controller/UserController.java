package com.guonl.controller;

import com.guonl.domain.User;
import com.guonl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by guonl
 * Date: 2019-12-03 16:41
 * Description:
 */
@Slf4j
@Controller
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api-login")
    public ResponseEntity login(String name,String password){
        UsernamePasswordToken token = new UsernamePasswordToken(name, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            return ResponseEntity.ok("认证通过！！");
        } catch (Exception e) {
            return ResponseEntity.ok("认证失败！！");
        }
    }

    @GetMapping("/init")
    public ResponseEntity init(Integer id,String name,String password){
        User user = userService.init(id,name, password);
        if(user != null){
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.ok("添加失败！！");
    }

    @GetMapping("/index")
    public ResponseEntity index(){
        return ResponseEntity.ok("登录成功！！");
    }

    @ResponseBody
    @GetMapping("/unauth")
    public String unauth() {
        return "error/unauth";
    }

    @ResponseBody
    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @ResponseBody
    @GetMapping("/logout")
    public String logout() {
        return "登出成功……";
    }

}
