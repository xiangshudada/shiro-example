package org.spring6.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.spring6.entity.Permission;
import org.spring6.entity.Role;
import org.spring6.entity.User;
import org.spring6.service.PermissionService;
import org.spring6.service.RoleService;
import org.spring6.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description: 自定义realm
 * @Version: 1.0
 */

@Component
@Slf4j
public class MyRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    /**
     * 设置密码匹配器
     */
    {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher("MD5");
        matcher.setHashIterations(1);
        matcher.setStoredCredentialsHexEncoded(true);
        this.setCredentialsMatcher(matcher);
    }


    @Autowired
    RoleService roleService;

    @Autowired
    PermissionService permissionService;

    /**
     * 授权
     * 
     * @param principals 认证用户信息
     * @return AuthorizationInfo 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("用户授权！");
        // 0. 判断是否认证
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {
            log.info("subject is null");
            return null;
        }
        if (!subject.isAuthenticated()) {
            log.info("用户未认证！");
            return null;
        }
        // 1. 获取认证用户的信息
        User user = (User) principals.getPrimaryPrincipal();
        log.info("授权用户信息:{}",user);
        // 2. 基于用户信息获取当前用户拥有的角色。
        Set<Role> roleSet = roleService.findRolesByUid(user.getId());
        Set<Integer> roleIdSet = new HashSet<>();
        Set<String> roleNameSet = new HashSet<>();
        for (Role role : roleSet) {
            roleIdSet.add(role.getId());
            roleNameSet.add(role.getRolename());
        }

        // 3. 基于用户拥有的角色查询权限信息
        Set<Permission> permSet = permissionService.findPermsByRoleSet(roleIdSet);
        Set<String> permNameSet = new HashSet<>();
        for (Permission permission : permSet) {
            permNameSet.add(permission.getPermissionname());
        }

        // 4. 声明AuthorizationInfo对象作为返回值，传入角色信息和权限信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roleNameSet);
        info.setStringPermissions(permNameSet);

        log.info("授权信息：角色信息：{}，权限信息：{}",roleNameSet,permNameSet);
        // 5. 返回
        return info;
    }

    /**
     * 认证
     * 
     * @param authenticationToken 认证令牌
     * @return AuthenticationInfo 认证信息
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        log.info("用户认证");
        // 1.获取用户名
        String userName = (String) authenticationToken.getPrincipal();
        log.info("认证用户名：{}",userName);
        // 2.如果用户名为空，直接返回认证失败
        if (StringUtils.isEmpty(userName)) {
            log.info("用户名不能为空！");
            return null;
        }
        // 3. 取出用户信息
        User user = userService.findByUserName(userName);
        log.info("认证用户信息：{}",user);
        // 5. 声明AuthenticationInfo对象，并填充用户信息
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), "ShiroRealm!!");
        // 设置盐！
        info.setCredentialsSalt(ByteSource.Util.bytes(user.getSalt()));
        // 6. 返回info
        return info;
    }
}
