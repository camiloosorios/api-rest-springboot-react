package com.bosorio.rest.api.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductHasBodyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("PUT") && request.getContentLength() == 0) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            List<Map<String, Object>> errorsList = new ArrayList<>();

            Map<String, Object> nameError = new HashMap<>();
            nameError.put("type", "field");
            nameError.put("msg", "El nombre no puede ser vacío");
            nameError.put("path", "name");
            nameError.put("location", "body");

            Map<String, Object> priceError = new HashMap<>();
            priceError.put("type", "field");
            priceError.put("msg", "El precio no puede ser vacío");
            priceError.put("path", "price");
            priceError.put("location", "body");

            Map<String, Object> availabilityError = new HashMap<>();
            availabilityError.put("type", "field");
            availabilityError.put("msg", "Valor para disponibilidad no válido");
            availabilityError.put("path", "availability");
            availabilityError.put("location", "body");

            errorsList.add(nameError);
            errorsList.add(priceError);
            errorsList.add(availabilityError);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errors", errorsList);

            PrintWriter writer = response.getWriter();
            writer.write(new ObjectMapper().writeValueAsString(errorResponse));
            writer.flush();
        }
        return true;
    }
}
