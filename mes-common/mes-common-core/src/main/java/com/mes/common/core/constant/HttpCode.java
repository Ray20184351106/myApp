package com.mes.common.core.constant;

/**
 * HTTP 状态码
 */
public class HttpCode {
    /** 成功 */
    public static final int SUCCESS = 200;
    /** 对象创建成功 */
    public static final int CREATED = 201;
    /** 请求已被接受 */
    public static final int ACCEPTED = 202;
    /** 资源已成功删除 */
    public static final int NO_CONTENT = 204;
    /** 参数错误 */
    public static final int BAD_REQUEST = 400;
    /** 未授权 */
    public static final int UNAUTHORIZED = 401;
    /** 禁止访问 */
    public static final int FORBIDDEN = 403;
    /** 资源未找到 */
    public static final int NOT_FOUND = 404;
    /** 不允许该方法 */
    public static final int METHOD_NOT_ALLOWED = 405;
    /** 参数验证失败 */
    public static final int UNPROCESSABLE_ENTITY = 422;
    /** 服务器内部错误 */
    public static final int INTERNAL_SERVER_ERROR = 500;
    /** 网关错误 */
    public static final int BAD_GATEWAY = 502;
    /** 服务不可用 */
    public static final int SERVICE_UNAVAILABLE = 503;
    /** 网关超时 */
    public static final int GATEWAY_TIMEOUT = 504;
}
