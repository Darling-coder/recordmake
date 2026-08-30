package com.itheima.recorddid.controller;

import com.itheima.recorddid.common.Result;
import com.itheima.recorddid.service.UserService;
import com.itheima.recorddid.vo.LoginVO;
import com.itheima.recorddid.vo.RegisterVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@Validated @RequestBody LoginVO loginVO) {
        Map<String,Object> loginInfo = userService.login(loginVO);
        return Result.success(loginInfo);
    }

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterVO registerVO) {
        userService.register(registerVO);
        return Result.success(null);
    }
}