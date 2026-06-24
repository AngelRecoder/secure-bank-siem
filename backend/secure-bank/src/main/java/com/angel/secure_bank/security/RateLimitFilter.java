package com.angel.secure_bank.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void  doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)

            throws ServletException, IOException {


        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, this::newBucket);


        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":429,\"message\":\"Too many requests, try again later\"}"
            );
        }
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(20, io.github.bucket4j.Refill.greedy(20, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}

// Cada IP tiene su propio balde de 20 tokens que se recargan a razón de
// 20 tokens por minuto. Si alguien hace más de 20 peticiones en un minuto
// desde la misma IP, las siguientes se rechazan con código 429.
// Guardamos los baldes en un mapa en memoria, una por IP, mientras el servidor sigue corriendo.
// Esto protege contra ataques de fuerza bruta a nivel de red, no solo por usuario.