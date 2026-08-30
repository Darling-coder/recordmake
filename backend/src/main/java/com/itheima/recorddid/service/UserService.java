package com.itheima.recorddid.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.recorddid.entity.User;
import com.itheima.recorddid.vo.LoginVO;
import com.itheima.recorddid.vo.RegisterVO;

import java.util.Map;

public interface UserService extends IService<User> {
    /**
     * 用户登录，返回JWT令牌
     */
    Map<String,Object> login(LoginVO loginVO);

    /**
     * 用户注册
     */
    void register(RegisterVO registerVO);

    /**
     * 根据账号查询用户
     */
    User getByUsername(String username);
}