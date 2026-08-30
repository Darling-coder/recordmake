package com.itheima.recorddid.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
@Data
public class RegisterVO {
    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    private String address;

    @NotBlank(message = "用户角色不能为空")
    @Pattern(regexp = "^(seller|buyer)$", message = "角色只能是seller卖家 / buyer买家")
    private String role;
}