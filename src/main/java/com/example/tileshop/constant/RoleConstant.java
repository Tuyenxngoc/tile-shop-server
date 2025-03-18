package com.example.tileshop.constant;

public enum RoleConstant {
    ROLE_ADMIN("Quản trị viên"),
    ROLE_USER("Người dùng");

    private final String roleName;

    RoleConstant(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
