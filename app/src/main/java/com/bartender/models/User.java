package com.bartender.models;

import java.util.Objects;

/**
 * Author: Velina Ilieva
 */
public class User
{
    private String id;
    private String email;
    private String barId;
    private int roleId;

    public User() {}

    public User(String id, String email, String barId, int roleId) {
        this.id = id;
        this.email = email;
        this.barId = barId;
        this.roleId = roleId;
    }

    public User(String id, String email, int roleId) {
        this.id = id;
        this.email = email;
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBarId() {
        return barId;
    }

    public void setBarId(String barId) {
        this.barId = barId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return roleId == user.roleId &&
                id.equals(user.id) &&
                email.equals(user.email) &&
                Objects.equals(barId, user.barId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", barId='" + barId + '\'' +
                ", roleId=" + roleId +
                '}';
    }
}
