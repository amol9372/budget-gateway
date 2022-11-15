package org.budget.tracker.gateway.interceptors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Aspect
public class AnnotationProcessor {

    @Around(value = "@annotation(ValidateHeader)")
    public Object checkUrl(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] methodArgs = joinPoint.getArgs();
        String header = (String) methodArgs[1];
//        if (urlOpt.isPresent()) {
//            final String url = urlOpt.get();
//            // if (isUrlValid(url)) {
//            return joinPoint.proceed();
//            //}
//        }
        return joinPoint.proceed();

        //throw new RuntimeException("The header is not valid");
    }

}
