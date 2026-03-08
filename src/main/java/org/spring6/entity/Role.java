package org.spring6.entity;


/**
 * @Author: zlpei
 * @CreateTime: 2026-03-06
 * @Description: 角色表
 * @Version: 1.0
 */

public class Role {

    private Integer id;

    private String rolename;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
}
