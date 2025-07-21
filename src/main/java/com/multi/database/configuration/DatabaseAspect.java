package com.multi.database.configuration;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class DatabaseAspect {

    @Around("@annotation(com.example.annotation.Database) || @within(com.example.annotation.Database)")
    public Object switchDatabase(ProceedingJoinPoint joinPoint) throws Throwable {
        String currentDatabase = DatabaseContextHolder.getCurrentDatabase();
        String targetDatabase = null;

        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // Check method level annotation first
            Database methodDatabase = method.getAnnotation(Database.class);
            if (methodDatabase != null) {
                targetDatabase = methodDatabase.value();
            } else {
                // Check class level annotation
                Database classDatabase = joinPoint.getTarget().getClass().getAnnotation(Database.class);
                if (classDatabase != null) {
                    targetDatabase = classDatabase.value();
                }
            }

            if (targetDatabase != null) {
                log.debug("Switching database context to: {}", targetDatabase);
                DatabaseContextHolder.setCurrentDatabase(targetDatabase);
            }

            return joinPoint.proceed();

        } finally {
            // Restore previous database context
            if (targetDatabase != null) {
                if (currentDatabase != null) {
                    DatabaseContextHolder.setCurrentDatabase(currentDatabase);
                } else {
                    DatabaseContextHolder.clear();
                }
            }
        }
    }
}
