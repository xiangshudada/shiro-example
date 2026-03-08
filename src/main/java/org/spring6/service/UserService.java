package org.spring6.service;


import org.apache.shiro.crypto.hash.Md5Hash;
import org.spring6.entity.User;
import org.spring6.mapper.UserMapper;
import org.spring6.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description: 用户登录、认证服务
 * @Version: 1.0
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    /**
     * 默认角色ID，新用户注册时分配（可选，0表示不分配）
     */
    @Value("${shiro.register.default-role-id:0}")
    private Integer defaultRoleId;

    public User findByUserName(String name) {
        return userMapper.findByUserName(name);
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 注册结果：SUCCESS-成功，USERNAME_EXISTS-用户名已存在，FAIL-失败
     */
    public String register(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return "FAIL";
        }
        if (findByUserName(username) != null) {
            return "USERNAME_EXISTS";
        }
        String salt = UUID.randomUUID().toString().replace("-", "");
        String encryptedPassword = new Md5Hash(password, salt, 1).toHex();
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setSalt(salt);
        int rows = userMapper.insert(user);
        if (rows > 0 && defaultRoleId != null && defaultRoleId > 0) {
            userRoleMapper.insertUserRole(user.getId(), defaultRoleId);
        }
        return rows > 0 ? "SUCCESS" : "FAIL";
    }
}
