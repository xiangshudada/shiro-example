package org.spring6.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description: 用户角色关联 Mapper
 * @Version: 1.0
 */

public interface UserRoleMapper {

    @Insert("insert into t_user_role (user_id, role_id) values (#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}
