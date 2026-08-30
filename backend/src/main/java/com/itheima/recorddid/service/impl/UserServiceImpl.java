package com.itheima.recorddid.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.recorddid.common.JwtUtil;
import com.itheima.recorddid.entity.User;
import com.itheima.recorddid.exception.BusinessException;
import com.itheima.recorddid.mapper.UserMapper;
import com.itheima.recorddid.service.UserService;
import com.itheima.recorddid.vo.LoginVO;
import com.itheima.recorddid.vo.RegisterVO;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Resource
    private JwtUtil jwtUtil;

    @Override
    public Map<String,Object> login(LoginVO loginVO) {
        // 1. 根据账号查询用户
        User user = getByUsername(loginVO.getUsername());
        if (user == null) {
            throw new BusinessException(50001,"账号不存在");
        }
        // 2. 校验密码
        if (!BCrypt.checkpw(loginVO.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "密码错误");
        }
        // 3. 生成JWT返回
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        map.put("role",user.getRole());
        map.put("userId",user.getId());
        return map;
    }

    @Override
    public void register(RegisterVO registerVO) {
        // ========新增：校验角色只能是seller / buyer========
        String role = registerVO.getRole();
        if (!"seller".equals(role) && !"buyer".equals(role)) {
            throw new BusinessException(400, "角色参数非法，仅允许seller(卖家)、buyer(买家)");
        }

        // 1. 判断账号是否已存在
        User existUser = getByUsername(registerVO.getUsername());
        if (existUser != null) {
            throw new BusinessException(50003,"该账号已被注册");
        }
        // 2. 封装用户数据
        User user = new User();
        user.setUsername(registerVO.getUsername());
        // 密码加密存储（把原来 passwordEncoder.encode 改成 BCrypt.hashpw）
        user.setPassword(BCrypt.hashpw(registerVO.getPassword(), BCrypt.gensalt()));
        user.setNickName(registerVO.getNickName());
        user.setPhone(registerVO.getPhone());
        user.setAddress(registerVO.getAddress());
        user.setRole(registerVO.getRole());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setDeleted(0);
        // 3. 插入数据库
        save(user);
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .eq(User::getDeleted, 0);
        return getOne(wrapper);
    }
}