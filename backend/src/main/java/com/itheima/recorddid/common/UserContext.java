package com.itheima.recorddid.common;

import com.itheima.recorddid.entity.User;

/**
 * 用户登录上下文工具类：基于ThreadLocal存储当前登录用户，全局任意地方获取
 */
public class UserContext {
    // ThreadLocal 线程本地存储，每个请求独立存储用户信息
    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 存入当前登录用户
     */
    public static void setLoginUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前登录用户
     */
    public static User getLoginUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 清除线程数据（拦截器请求结束必须调用，防止内存泄漏）
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }

    /**
     * 快捷获取当前登录用户ID（新增，解决报错）
     */
    public static Long getUserId() {
        User loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getId();
    }
}