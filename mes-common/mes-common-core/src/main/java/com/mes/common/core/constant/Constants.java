package com.mes.common.core.constant;

/**
 * 系统常量
 */
public class Constants {

    /** UTF-8 编码 */
    public static final String UTF8 = "UTF-8";

    /** 成功标记 */
    public static final int SUCCESS = 200;

    /** 失败标记 */
    public static final int FAIL = 500;

    /** 登录成功 */
    public static final String LOGIN_SUCCESS = "Success";

    /** 注销 */
    public static final String LOGOUT = "Logout";

    /** 登录失败 */
    public static final String LOGIN_FAIL = "Error";

    /** 验证码有效期（分钟） */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /** 令牌 */
    public static final String TOKEN = "token";

    /** 令牌前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 令牌前缀 */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /** 用户ID */
    public static final String JWT_USERID = "userid";

    /** 用户名称 */
    public static final String JWT_USERNAME = "sub";

    /** 用户头像 */
    public static final String JWT_AVATAR = "avatar";

    /** 创建时间 */
    public static final String JWT_CREATED = "created";

    /** 用户权限 */
    public static final String JWT_AUTHORITIES = "authorities";

    /** 资源映射路径 前缀 */
    public static final String RESOURCE_PREFIX = "/profile";

    /** RMI 远程方法调用 */
    public static final String LOOKUP_RMI = "rmi:";

    /** LDAP 远程方法调用 */
    public static final String LOOKUP_LDAP = "ldap:";

    /** LDAPS 远程方法调用 */
    public static final String LOOKUP_LDAPS = "ldaps:";

    /** 定时任务白名单配置（仅允许登录的地址） */
    public static final String[] TASK_WHITELIST = { "127.0.0.1", "::1", "localhost" };
}
