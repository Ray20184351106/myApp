package com.mes.common.security.annotation;

/**
 * 权限验证逻辑类型
 */
public enum Logical {
    /**
     * 必须具有所有的权限
     */
    AND,

    /**
     * 只需具有其中一个权限
     */
    OR
}
