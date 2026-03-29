package com.logistics.server.controller;

import com.logistics.server.entity.User;
import com.logistics.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 允许前端跨域
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> response = new HashMap<>();
        User user = userMapper.login(loginUser.getUsername(), loginUser.getPassword());

        if (user != null) {
            response.put("code", 200);
            response.put("msg", "登录成功");
            response.put("data", user); // 返回用户信息，包含 role
        } else {
            response.put("code", 401);
            response.put("msg", "用户名或密码错误");
        }
        return response;
    }
}