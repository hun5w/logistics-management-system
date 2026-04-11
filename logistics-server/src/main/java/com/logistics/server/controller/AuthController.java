package com.logistics.server.controller;

import com.logistics.server.entity.User;
import com.logistics.server.mapper.UserMapper;
import com.logistics.server.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> response = new HashMap<>();
        User user = userMapper.findByUsername(loginUser.getUsername());

        boolean passwordValid = false;
        if (user != null) {
            String storedPassword = user.getPassword();
            String rawPassword = loginUser.getPassword();
            passwordValid = PasswordUtil.matches(rawPassword, storedPassword) || rawPassword.equals(storedPassword);
            if (passwordValid && !PasswordUtil.isBcryptHash(storedPassword)) {
                userMapper.updateUserPassword(user.getId(), PasswordUtil.encode(rawPassword));
            }
        }

        if (user != null && passwordValid) {
            if (user.getStatus() == 0) {
                response.put("code", 403);
                response.put("msg", "账号已被禁用，请联系管理员");
                return response;
            }
            user.setPassword(null);
            response.put("code", 200);
            response.put("msg", "登录成功");
            response.put("data", user);
        } else {
            response.put("code", 401);
            response.put("msg", "用户名或密码错误");
        }
        return response;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User newUser) {
        Map<String, Object> response = new HashMap<>();

        // 1. 安全校验：禁止通过接口注册 ADMIN 或 WAREHOUSE
        String regRole = newUser.getRole();
        if ("ADMIN".equalsIgnoreCase(regRole) || "WAREHOUSE".equalsIgnoreCase(regRole)) {
            response.put("code", 403);
            response.put("msg", "内部角色需由管理员分配，禁止自主注册");
            return response;
        }

        // 2. 默认值与规范化
        if (regRole == null || regRole.isEmpty()) newUser.setRole("USER");
        newUser.setStatus(1);
        if (newUser.getNickname() == null) newUser.setNickname("用户_" + newUser.getUsername());

        // 3. 查重校验
        if (userMapper.findByUsername(newUser.getUsername()) != null) {
            response.put("code", 400);
            response.put("msg", "账号已存在");
            return response;
        }
        if (newUser.getPhone() != null && userMapper.findByPhone(newUser.getPhone()) != null) {
            response.put("code", 400);
            response.put("msg", "该手机号已被绑定");
            return response;
        }

        newUser.setPassword(PasswordUtil.encode(newUser.getPassword()));

        int result = userMapper.insertUser(newUser);
        if (result > 0) {
            response.put("code", 200);
            response.put("msg", "注册成功");
        } else {
            response.put("code", 500);
            response.put("msg", "注册失败，请联系系统支持");
        }
        return response;
    }
}