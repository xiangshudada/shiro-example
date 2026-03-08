package org.spring6.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.spring6.entity.User;

/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description:
 * @Version: 1.0
 */

public interface UserMapper {
    @Select("select * from t_user where username = #{username}")
    User findByUserName(@Param("username") String name);

    @Insert("insert into t_user (username, password, salt) values (#{username}, #{password}, #{salt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
