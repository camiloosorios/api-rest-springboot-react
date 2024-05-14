package com.bosorio.rest.api.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ProductIdIsNumberInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().matches("/api/products/[a-zA-Z]+")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Id no valido");

            PrintWriter writer = response.getWriter();
            writer.write(new ObjectMapper().writeValueAsString(errorResponse));
            writer.flush();
            return false;
        }
        return true;
    }

}
