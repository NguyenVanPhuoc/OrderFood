package com.example.lesson3.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Object handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        if (isJsonRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra. Vui lòng thử lại."));
        }
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "Có lỗi xảy ra. Vui lòng thử lại.");
        return mav;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Invalid argument: {}", ex.getMessage());
        if (isJsonRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Yêu cầu không hợp lệ: " + ex.getMessage()));
        }
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "Yêu cầu không hợp lệ. Vui lòng kiểm tra lại.");
        return mav;
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String contentType = request.getContentType();
        return (accept != null && accept.contains("application/json"))
                || (contentType != null && contentType.contains("application/json"));
    }
}

