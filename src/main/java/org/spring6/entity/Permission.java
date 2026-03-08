package org.spring6.entity;


/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description: 权限表
 * @Version: 1.0
 */

public class Permission {

    private Integer id;

    private String permissionname;

    private Integer roleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermissionname() {
        return permissionname;
    }

    public void setPermissionname(String permissionname) {
        this.permissionname = permissionname;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
