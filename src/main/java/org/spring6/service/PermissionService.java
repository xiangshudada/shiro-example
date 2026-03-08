package org.spring6.service;


import org.spring6.entity.Permission;
import org.spring6.entity.Role;
import org.spring6.mapper.PermissionMapper;
import org.spring6.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description:
 * @Version: 1.0
 */
@Service
public class PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    public Set<Permission> findPermsByRoleSet(Set<Integer> roleIdSet) {
        return permissionMapper.findPermsByRoleSet(roleIdSet);
    }
}
