package com.example.aspect;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限验证切面
 * 用于拦截带有权限注解的方法并进行权限校验
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final PermissionService permissionService;

    @Around("@annotation(com.example.annotation.RequirePermission) || @within(com.example.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 先检查方法级别的注解
        RequirePermission methodPermission = method.getAnnotation(RequirePermission.class);
        if (methodPermission != null) {
            checkPermissionAnnotation(methodPermission);
        }

        // 再检查类级别的注解
        RequirePermission classPermission = joinPoint.getTarget().getClass().getAnnotation(RequirePermission.class);
        if (classPermission != null && methodPermission == null) {
            checkPermissionAnnotation(classPermission);
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(com.example.annotation.RequireRole) || @within(com.example.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 先检查方法级别的注解
        RequireRole methodRole = method.getAnnotation(RequireRole.class);
        if (methodRole != null) {
            checkRoleAnnotation(methodRole);
        }

        // 再检查类级别的注解
        RequireRole classRole = joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
        if (classRole != null && methodRole == null) {
            checkRoleAnnotation(classRole);
        }

        return joinPoint.proceed();
    }

    private void checkPermissionAnnotation(RequirePermission annotation) {
        String[] permissions = annotation.value();
        boolean requireAll = annotation.requireAll();

        log.debug("权限校验 - 所需权限: {}, 要求全部: {}", permissions, requireAll);

        boolean hasPermission;
        if (requireAll) {
            hasPermission = permissionService.hasAllPermissions(permissions);
        } else {
            hasPermission = permissionService.hasAnyPermission(permissions);
        }

        if (!hasPermission) {
            String description = annotation.description().isEmpty() ? "权限不足" : annotation.description();
            log.warn("用户权限不足: 用户={}, 所需权限={}", permissionService.getCurrentUsername(), permissions);
            throw new BusinessException(ResultCode.PERMISSION_DENIED, description);
        }
    }

    private void checkRoleAnnotation(RequireRole annotation) {
        String[] roles = annotation.value();
        boolean requireAll = annotation.requireAll();

        log.debug("角色校验 - 所需角色: {}, 要求全部: {}", roles, requireAll);

        boolean hasRole;
        if (requireAll) {
            hasRole = permissionService.hasAllRoles(roles);
        } else {
            hasRole = permissionService.hasAnyRole(roles);
        }

        if (!hasRole) {
            String description = annotation.description().isEmpty() ? "角色权限不足" : annotation.description();
            log.warn("用户角色不足: 用户={}, 所需角色={}", permissionService.getCurrentUsername(), roles);
            throw new BusinessException(ResultCode.PERMISSION_DENIED, description);
        }
    }
}
