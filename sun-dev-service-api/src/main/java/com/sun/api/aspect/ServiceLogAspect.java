package com.sun.api.aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    final static Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /*
    aop通知
    1.前置通知
    2.后置通知
    3.环绕通知
    4.异常通知
    5.最终通知
     */

    @Around("execution(* com.sun.*.service.impl..*.*(..))")
    public Object recordTimeOfService(ProceedingJoinPoint joinPoint) throws Throwable {

        logger.info("==== 开始执行 ｛｝.{} ====", joinPoint.getTarget().getClass(),
                                               joinPoint.getSignature().getName());

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        if (totalTime > 3000) {
            logger.error("当前执行耗时：｛｝", totalTime);
        } else if (totalTime > 2000) {
            logger.warn("当前执行耗时：｛｝", totalTime);
        } else {
            logger.info("当前执行耗时：｛｝", totalTime);
        }
        return result;
    }

}
