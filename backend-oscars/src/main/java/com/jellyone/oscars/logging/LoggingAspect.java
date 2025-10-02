package com.jellyone.oscars.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.jellyone.oscars.service..*) || within(com.jellyone.oscars.client..*)")
    public void serviceAndClientLayer() {
    }

    @Around("serviceAndClientLayer()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("Enter: {} args={}", method, Arrays.toString(args));
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long took = System.currentTimeMillis() - start;
            log.info("Exit: {} took={}ms", method, took);
            return result;
        } catch (Throwable ex) {
            long took = System.currentTimeMillis() - start;
            log.error("Exception in {} took={}ms", method, took, ex);
            throw ex;
        }
    }

    @AfterThrowing(pointcut = "serviceAndClientLayer()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        log.error(ex.getMessage());
    }
}


