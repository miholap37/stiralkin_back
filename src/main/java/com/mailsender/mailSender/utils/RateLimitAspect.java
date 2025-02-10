package com.mailsender.mailSender.utils;

import com.mailsender.mailSender.annotations.RateLimit;
import com.mailsender.mailSender.services.impl.EmailServiceImpl;
import io.github.bucket4j.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Aspect
@Component
public class RateLimitAspect {
    public static final String ERROR_MESSAGE = "Too many requests at endpoint %s from IP %s!";
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Around("@annotation(rateLimitAnnotation)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimitAnnotation) throws Throwable {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        String clientIp = getClientIp(requestAttributes.getRequest());

        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createNewBucket);

        synchronized (bucket) {
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                LOG.info("Remaining tokens for IP {}: {}", clientIp, probe.getRemainingTokens());
                return joinPoint.proceed();
            } else {
                String errorMessage = String.format(ERROR_MESSAGE, requestAttributes.getRequest().getRequestURI(), clientIp);
                throw new RateLimitExceededException(errorMessage);
            }
        }


    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    private Bucket createNewBucket(String key) {
        Bandwidth limit = Bandwidth.classic(2, Refill.intervally(2, Duration.ofMinutes(5)));
        return Bucket4j.builder().addLimit(limit).build();
    }
}