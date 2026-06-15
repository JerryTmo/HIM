package com.example.annotation;

import java.lang.annotation.*;

/**
 * 角色校验注解
 * 用于标记需要特定角色才能访问的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 角色编码列表，满足任一即可访问
     */
    String[] value() default {};

    /**
     * 是否需要所有角色都满足，默认只需任一
     */
    boolean requireAll() default false;

    /**
     * 角色描述（用于日志和文档）
     */
    String description() default "";
}
