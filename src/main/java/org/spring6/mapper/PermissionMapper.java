package org.spring6.mapper;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.spring6.entity.Permission;

import java.util.Set;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description:
 * @Version: 1.0
 */

public interface PermissionMapper {
    @Select("select * from t_permission where role_id in (#{roleIdSet})")
    Set<Permission> findPermsByRoleSet(@Param("roleIdSet") Set<Integer> roleIdSet);
}
