package com.salarypayoutsystem.employeeservice.springboot.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Limit: 2 requests per minute per IP for sensitive endpoints
        Bandwidth limit = Bandwidth.classic(2, Refill.greedy(2, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Only rate limit the batch generation endpoint
        if (request.getRequestURI().equals("/api/salaries/generate-all") && request.getMethod().equalsIgnoreCase("POST")) {
            String ip = request.getRemoteAddr();
            Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

            if (bucket.tryConsume(1)) {
                return true;
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many requests. Please wait before generating batch salaries again.\"}");
                return false;
            }
        }
        return true;
    }
}
