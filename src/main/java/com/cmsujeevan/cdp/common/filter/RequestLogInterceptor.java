package com.cmsujeevan.cdp.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

@Component
@Slf4j
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Pattern BREAKING_PATTERN_1 = Pattern.compile("[\n]");
    private static final Pattern BREAKING_PATTERN_2 = Pattern.compile("[\r]");
    private static final Pattern BREAKING_PATTERN_3 = Pattern.compile("[\t]");
    private static final String REPLACE_STRING = "_";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {
        String method = replaceBreakingPattern(request.getMethod());
        String uriPath = replaceBreakingPattern(request.getRequestURI());
        String params = replaceBreakingPattern(request.getParameterMap().toString());
        log.info("request received: method - {}, path - {}, params -{}", method,
                uriPath, params);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex) {
        String method = replaceBreakingPattern(request.getMethod());
        String uriPath = replaceBreakingPattern(request.getRequestURI());
        log.info("request completed: method - {}, path - {}", method, uriPath);
    }

    private String replaceBreakingPattern(String input) {
        var newString = BREAKING_PATTERN_1.matcher(input).replaceAll(REPLACE_STRING);
        var newString1 = BREAKING_PATTERN_2.matcher(newString).replaceAll(REPLACE_STRING);
        return BREAKING_PATTERN_3.matcher(newString1).replaceAll(REPLACE_STRING);
    }
}

