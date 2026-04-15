package com.springboot.constant;

/**
 * 用户常量
*
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 新认证体系登录态键
     */
    String AUTH_LOGIN_STATE = "auth_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "管理员";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    /**
     * 学生角色类型
     */
    String ROLE_TYPE_STUDENT = "学生";

    /**
     * 教师角色类型
     */
    String ROLE_TYPE_TEACHER = "教师";

    /**
     * 助教角色类型
     */
    String ROLE_TYPE_ASSISTANT = "助教";

    // endregion
}


