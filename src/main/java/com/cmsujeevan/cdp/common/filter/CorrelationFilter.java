package com.cmsujeevan.cdp.common.filter;

import com.cmsujeevan.cdp.common.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static com.cmsujeevan.cdp.common.util.Util.jobIdSupplier;

/**
 * HTTP Request Correlation Servlet Filter
 */
@Component
@Order(1)
@Slf4j
public class CorrelationFilter implements Filter {

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var correlationId = httpRequest.getHeader(Constants.CORRELATION_ID);
        log.info("ip address: {}", httpRequest.getRemoteAddr());
        if (ObjectUtils.isEmpty(correlationId)) {
            log.debug(
                    "No Correlation ID found in the HTTP request. Generating a new ID.");
            correlationId = UUID.randomUUID().toString();
        } else if (!correlationId.matches(Constants.CORRELATIONID_REGEX)) {
            var newCorrelationId = UUID.randomUUID().toString();
            log.debug(
                    "Invalid Correlation ID {} received in HTTP request. Generating a new ID {}",
                    correlationId, newCorrelationId);
            correlationId = newCorrelationId;
        }

        log.debug("{\"service\":\"{}\", \"process\":\"{}\", \"SYY-Correlation-ID\":\"{}\"}",
                     "cdp-bulk-api", "RequestReceived", correlationId);

        MDC.put(Constants.MDC_CORRELATION_ID, correlationId);
        prepareLogContext(httpRequest);

        log.debug("MDC Context has been set for the request");
        ((HttpServletResponse) response).setHeader(Constants.CORRELATION_ID, correlationId);
        try {
            chain.doFilter(httpRequest, response);
        } finally {
            log.debug("Clearing the MDC Context for the request");
            MDC.remove(Constants.MDC_CORRELATION_ID);
            MDC.remove(Constants.JOB_ID);
        }
    }

    private void prepareLogContext(HttpServletRequest servletRequest) throws IOException {

        //check the request API is a bulk job related API
        if (isABulkAPI(servletRequest.getRequestURI())) {

            //generate job id for create job API
            if (servletRequest.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
                String jobId = jobIdSupplier.get();
                MDC.put(Constants.JOB_ID, jobId);
                servletRequest.setAttribute(Constants.JOB_ID, jobId);
            }
            //if the job id presents in request path, add it to logging context
            if (servletRequest.getRequestURI().contains(Constants.JOB_ID_PREFIX_NAME)) {
                Arrays.stream(servletRequest.getRequestURI().split(Constants.PATH_SLASH))
                        .filter(s -> s.contains(Constants.JOB_ID_PREFIX_NAME))
                        .findAny()
                        .ifPresent(jobId -> {
                            MDC.put(Constants.JOB_ID, jobId);
                        });
            }
        }
    }

    private boolean isABulkAPI(String url) {
        String bulkApiPAth = Constants.PATH_SLASH + appName + Constants.PATH_SLASH + Constants.BATCH_API_PREFIX;
        return url.contains(bulkApiPAth);
    }

}
