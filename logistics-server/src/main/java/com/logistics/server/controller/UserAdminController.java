package com.logistics.server.controller;

import com.logistics.server.entity.AdminOpLog;
import com.logistics.server.entity.User;
import com.logistics.server.mapper.AdminOpLogMapper;
import com.logistics.server.mapper.UserMapper;
import com.logistics.server.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserAdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminOpLogMapper adminOpLogMapper;

    @GetMapping("/all")
    public Map<String, Object> getAllUsers(@RequestHeader(value = "User-Role", required = false) String role) {
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可查看用户列表");
            return res;
        }
        res.put("code", 200);
        List<User> users = userMapper.findAllUsers();
        users.forEach(user -> user.setPassword(null));
        res.put("data", users);
        return res;
    }

    @PostMapping("/internal-create")
    public Map<String, Object> createInternalUser(@RequestHeader(value = "User-Role", required = false) String role,
                                                  @RequestHeader(value = "User-Name", required = false) String operatorUsername,
                                                  @RequestBody User newUser) {
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可创建内部账号");
            return res;
        }

        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()
                || newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            res.put("code", 400);
            res.put("msg", "账号和密码不能为空");
            return res;
        }

        List<String> allowedRoles = Arrays.asList("ADMIN", "WAREHOUSE", "COURIER", "USER");
        String targetRole = (newUser.getRole() == null || newUser.getRole().trim().isEmpty()) ? "USER" : newUser.getRole().trim().toUpperCase();
        if (!allowedRoles.contains(targetRole)) {
            res.put("code", 400);
            res.put("msg", "角色不合法");
            return res;
        }

        if (userMapper.findByUsername(newUser.getUsername().trim()) != null) {
            res.put("code", 400);
            res.put("msg", "账号已存在");
            return res;
        }

        if (newUser.getPhone() != null && !newUser.getPhone().trim().isEmpty()
                && userMapper.findByPhone(newUser.getPhone().trim()) != null) {
            res.put("code", 400);
            res.put("msg", "手机号已绑定其他账号");
            return res;
        }

        newUser.setUsername(newUser.getUsername().trim());
        newUser.setRole(targetRole);
        newUser.setStatus(newUser.getStatus() == null ? 1 : newUser.getStatus());
        if (newUser.getNickname() == null || newUser.getNickname().trim().isEmpty()) {
            newUser.setNickname("用户_" + newUser.getUsername());
        }
        if (newUser.getPhone() != null && newUser.getPhone().trim().isEmpty()) {
            newUser.setPhone(null);
        }
        newUser.setPassword(PasswordUtil.encode(newUser.getPassword()));

        int result = userMapper.insertUser(newUser);
        if (result > 0) {
            User createdUser = userMapper.findByUsername(newUser.getUsername());
            saveAdminLog(operatorUsername, createdUser == null ? null : createdUser.getId(),
                    "CREATE_USER", "创建内部账号: " + newUser.getUsername() + " (" + newUser.getRole() + ")");
            res.put("code", 200);
            res.put("msg", "创建成功");
        } else {
            res.put("code", 500);
            res.put("msg", "创建失败");
        }
        return res;
    }

    @PutMapping("/role")
    public Map<String, Object> updateUserRole(@RequestHeader(value = "User-Role", required = false) String role,
                                              @RequestHeader(value = "User-Name", required = false) String operatorUsername,
                                              @RequestParam Long id,
                                              @RequestParam String targetRole) {
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可修改角色");
            return res;
        }

        List<String> allowedRoles = Arrays.asList("ADMIN", "WAREHOUSE", "COURIER", "USER");
        String normalizedRole = targetRole == null ? "" : targetRole.trim().toUpperCase();
        if (!allowedRoles.contains(normalizedRole)) {
            res.put("code", 400);
            res.put("msg", "目标角色不合法");
            return res;
        }

        if (operatorUsername != null && !operatorUsername.trim().isEmpty()) {
            User self = userMapper.findByUsername(operatorUsername.trim());
            if (self != null && self.getId().equals(id) && !"ADMIN".equals(normalizedRole)) {
                res.put("code", 400);
                res.put("msg", "不允许将当前登录管理员降级");
                return res;
            }
        }

        int result = userMapper.updateUserRole(id, normalizedRole);
        if (result > 0) {
            saveAdminLog(operatorUsername, id, "UPDATE_ROLE", "修改用户角色为: " + normalizedRole);
        }
        res.put("code", result > 0 ? 200 : 400);
        res.put("msg", result > 0 ? "角色更新成功" : "角色更新失败");
        return res;
    }

    @PutMapping("/status")
    public Map<String, Object> updateUserStatus(@RequestHeader(value = "User-Role", required = false) String role,
                                                @RequestHeader(value = "User-Name", required = false) String operatorUsername,
                                                @RequestParam Long id,
                                                @RequestParam Integer status) {
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可修改状态");
            return res;
        }

        if (status == null || (status != 0 && status != 1)) {
            res.put("code", 400);
            res.put("msg", "状态仅支持 0(禁用) 或 1(正常)");
            return res;
        }

        if (operatorUsername != null && !operatorUsername.trim().isEmpty()) {
            User self = userMapper.findByUsername(operatorUsername.trim());
            if (self != null && self.getId().equals(id) && status == 0) {
                res.put("code", 400);
                res.put("msg", "不允许禁用当前登录管理员账号");
                return res;
            }
        }

        int result = userMapper.updateUserStatus(id, status);
        if (result > 0) {
            saveAdminLog(operatorUsername, id, "UPDATE_STATUS", "修改账号状态为: " + (status == 1 ? "正常" : "禁用"));
        }
        res.put("code", result > 0 ? 200 : 400);
        res.put("msg", result > 0 ? "状态更新成功" : "状态更新失败");
        return res;
    }

    @GetMapping("/op-logs")
    public Map<String, Object> getOpLogs(@RequestHeader(value = "User-Role", required = false) String role) {
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可查看操作日志");
            return res;
        }
        res.put("code", 200);
        res.put("data", adminOpLogMapper.findRecentLogs());
        return res;
    }

    @GetMapping("/disabled")
    public Map<String, Object> getDisabledUsers(@RequestHeader(value = "User-Role", required = false) String role) {
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可查看禁用账号");
            return res;
        }

        List<User> disabledUsers = userMapper.findByStatus(0);
        disabledUsers.forEach(user -> user.setPassword(null));
        res.put("code", 200);
        res.put("data", disabledUsers);
        return res;
    }

    @GetMapping("/role-change-stats")
    public Map<String, Object> getRoleChangeStats(@RequestHeader(value = "User-Role", required = false) String role) {
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可查看角色变更统计");
            return res;
        }

        List<AdminOpLog> allLogs = adminOpLogMapper.findRecentLogs();
        List<AdminOpLog> roleLogs = allLogs.stream()
                .filter(log -> "UPDATE_ROLE".equals(log.getAction()))
                .sorted(Comparator.comparing(AdminOpLog::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        Map<String, Long> roleCounter = new LinkedHashMap<>();
        roleCounter.put("ADMIN", 0L);
        roleCounter.put("WAREHOUSE", 0L);
        roleCounter.put("COURIER", 0L);
        roleCounter.put("USER", 0L);

        for (AdminOpLog log : roleLogs) {
            String detail = log.getDetail();
            String targetRole = extractRoleFromDetail(detail);
            if (targetRole != null && roleCounter.containsKey(targetRole)) {
                roleCounter.put(targetRole, roleCounter.get(targetRole) + 1);
            }
        }

        List<Map<String, Object>> distribution = new ArrayList<>();
        for (Map.Entry<String, Long> entry : roleCounter.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("role", entry.getKey());
            item.put("count", entry.getValue());
            distribution.add(item);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalRoleChanges", roleLogs.size());
        data.put("distribution", distribution);
        data.put("recent", roleLogs.stream().limit(20).collect(Collectors.toList()));

        res.put("code", 200);
        res.put("data", data);
        return res;
    }

    @PutMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestHeader(value = "User-Role", required = false) String role,
                                             @RequestHeader(value = "User-Name", required = false) String operatorUsername,
                                             @RequestParam Long id,
                                             @RequestParam(required = false) String newPassword) {
        Map<String, Object> res = new HashMap<>();
        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可重置密码");
            return res;
        }

        User targetUser = userMapper.findById(id);
        if (targetUser == null) {
            res.put("code", 404);
            res.put("msg", "目标用户不存在");
            return res;
        }

        String rawPassword = (newPassword == null || newPassword.trim().isEmpty()) ? "123456" : newPassword.trim();
        int result = userMapper.updateUserPassword(id, PasswordUtil.encode(rawPassword));
        if (result > 0) {
            saveAdminLog(operatorUsername, id, "RESET_PASSWORD", "重置账号密码: " + targetUser.getUsername());
            res.put("code", 200);
            res.put("msg", "密码重置成功");
        } else {
            res.put("code", 500);
            res.put("msg", "密码重置失败");
        }
        return res;
    }

    @GetMapping("/op-logs/export")
    public ResponseEntity<byte[]> exportOpLogs(@RequestHeader(value = "User-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403)
                    .body("{\"code\":403,\"msg\":\"仅管理员可导出日志\"}".getBytes(StandardCharsets.UTF_8));
        }

        List<AdminOpLog> logs = adminOpLogMapper.findRecentLogs();
        StringBuilder csv = new StringBuilder();
        csv.append("id,create_time,operator_username,action,target_user_id,detail\n");
        for (AdminOpLog log : logs) {
            csv.append(safeCsv(log.getId()))
                    .append(',')
                    .append(safeCsv(log.getCreateTime()))
                    .append(',')
                    .append(safeCsv(log.getOperatorUsername()))
                    .append(',')
                    .append(safeCsv(log.getAction()))
                    .append(',')
                    .append(safeCsv(log.getTargetUserId()))
                    .append(',')
                    .append(safeCsv(log.getDetail()))
                    .append('\n');
        }

        byte[] bytes = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admin-op-logs.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(bytes);
    }

    private String safeCsv(Object value) {
        if (value == null) {
            return "\"\"";
        }
        String text = String.valueOf(value).replace("\"", "\"\"");
        return "\"" + text + "\"";
    }

    private String extractRoleFromDetail(String detail) {
        if (detail == null) {
            return null;
        }
        if (detail.contains("ADMIN")) {
            return "ADMIN";
        }
        if (detail.contains("WAREHOUSE")) {
            return "WAREHOUSE";
        }
        if (detail.contains("COURIER")) {
            return "COURIER";
        }
        if (detail.contains("USER")) {
            return "USER";
        }
        return null;
    }

    private void saveAdminLog(String operatorUsername, Long targetUserId, String action, String detail) {
        AdminOpLog log = new AdminOpLog();
        log.setOperatorUsername((operatorUsername == null || operatorUsername.trim().isEmpty()) ? "ADMIN" : operatorUsername.trim());
        log.setTargetUserId(targetUserId);
        log.setAction(action);
        log.setDetail(detail);
        adminOpLogMapper.insertLog(log);
    }
}