package org.spring6.mapper;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.spring6.entity.Role;
import org.spring6.entity.User;

import java.util.Set;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description:
 * @Version: 1.0
 */

public interface RoleMapper {
    @Select("select r.* from t_role r, t_user_role ur where r.id = ur.role_id and ur.user_id = #{uid}")
    Set<Role> findRolesByUid(@Param("uid") Integer uid);
}
