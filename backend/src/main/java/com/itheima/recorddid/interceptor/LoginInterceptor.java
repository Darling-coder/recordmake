package com.itheima.recorddid.interceptor;

import com.itheima.recorddid.common.JwtUtil;
import com.itheima.recorddid.common.UserContext;
import com.itheima.recorddid.entity.User;
import com.itheima.recorddid.exception.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // ✅ 必须要加的 3 行代码：如果是预检请求 OPTIONS，直接放行，千万别去查 token！
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 1. 获取请求头token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        String token = authHeader.substring(7);

        try {
            // 2. 解析token
            Claims claims = JwtUtil.parseToken(token);
            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);

            // 3. 封装用户存入线程上下文
            User loginUser = new User();
            loginUser.setId(userId);
            loginUser.setRole(role);
            UserContext.setLoginUser(loginUser);
        } catch (Exception e) {
            throw new BusinessException(401, "登录令牌失效，请重新登录");
        }
        return true;
    }

    // 请求结束清除线程数据，防止内存泄漏
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}