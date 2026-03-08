package org.spring6.entity;


/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description: 用户角色表
 * @Version: 1.0
 */

public class UserRole {
    private Integer id;

    private String userId;

    private String roleId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
