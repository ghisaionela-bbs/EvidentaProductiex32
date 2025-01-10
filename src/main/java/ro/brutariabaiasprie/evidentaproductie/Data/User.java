package ro.brutariabaiasprie.evidentaproductie.Data;

import java.util.Objects;

public class  User {
    private int id;
    private int roleId;
    private String username;
    private String password;
    private Integer groupId;
    private int subgroupId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public int getSubgroupId() {
        return subgroupId;
    }

    public void setSubgroupId(int subgroupId) {
        this.subgroupId = subgroupId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", groupId=" + groupId +
                ", subgroupId=" + subgroupId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && roleId == user.roleId && subgroupId == user.subgroupId && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(groupId, user.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleId, username, password, groupId, subgroupId);
    }
}
