package br.com.pupposoft.poc.monitoriamento.produto.config.logger;

import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.CLASS;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.CLASS_METHOD;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.ELAPSED_TIME;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.EXEC_END;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.EXEC_START;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.REQUEST_BODY;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.REQUEST_HEADERS;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.REQUEST_HTTP_METHOD;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.REQUEST_PARAMS;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.REQUEST_PATH;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.REQUEST_USER_AGENT;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.RESPONSE_BODY;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.RESPONSE_HEADERS;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.RESPONSE_STATUS_CODE;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.TRACE_ID;
import static net.logstash.logback.argument.StructuredArguments.kv;

import java.io.BufferedReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import brave.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoggingRequestServiceInterceptor implements HandlerInterceptor {

    private static final String HEADER_USER_AGENT = "User-Agent";
    
    private final Tracer tracer;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) throws Exception {
        request.setAttribute(EXEC_START.getKey(), LocalDateTime.now());
        request.setAttribute(TRACE_ID.getKey(), tracer.currentSpan().context().traceIdString());
        logRequestData(request, handler);
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final Exception ex) {   
        try {
        	logResponseData(response);
        	if(handler == null) {
        		log.info("handler intecepted is null. Ignoring logs for it.");
        	} else if(handler instanceof HandlerMethod) {
        		final LocalDateTime execStart = (LocalDateTime) request.getAttribute(EXEC_START.getKey());
        		logElapsedTimeRequestExecution(execStart);        		
        		
        	} else {
        		log.info("handler intecepted is not a HandlerMethod. Ignoring logs for it. {}", handler);
        	}
        	
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void logRequestData(final HttpServletRequest request, final Object handler) {
    	log.info("Request Data: "
    			+ "{}, "
    			+ "{}, "
    			+ "{}, "
    			+ "{}, "
    			+ "{}, "
    			+ "{}",
    			kv(CLASS.getKey(), getClassName(handler)),
    			kv(CLASS_METHOD.getKey(), getClassMethodName(handler)),
    			kv(REQUEST_PARAMS.getKey(), getRequestParams(request)),
    			kv(REQUEST_HEADERS.getKey(), getRequestHeaders(request)),
    			kv(REQUEST_PATH.getKey(), getRequestPath(request)),
    			kv(REQUEST_BODY.getKey(), getRequestBody(request)),
    			kv(REQUEST_HTTP_METHOD.getKey(), getRequestHttpMethod(request)),
    			kv(REQUEST_USER_AGENT.getKey(), request.getHeader(HEADER_USER_AGENT)));
    }
    
    private void logResponseData(final HttpServletResponse response) {
    	log.info("Response Data: "
    			+ "{}, "
    			+ "{}, "
    			+ "{}",
    			kv(RESPONSE_STATUS_CODE.getKey(), getResponseStatusCode(response)),
    			kv(RESPONSE_HEADERS.getKey(), getResponseHeaders(response)),
    			kv(RESPONSE_BODY.getKey(), getResponseBody(response)));
    }
    
    private void logElapsedTimeRequestExecution(final LocalDateTime execStart) {
        final LocalDateTime execEnd = LocalDateTime.now();
        log.info("Service elapsed time (miliseconds) "
        		+ "{}, "
        		+ "{}, "
        		+ "{}",
                kv(EXEC_START.getKey(), execStart.toString()),
                kv(EXEC_END.getKey(), execEnd.toString()),
                kv(ELAPSED_TIME.getKey(), millisecondsBetween(execStart, execEnd)));
    }
    

    private String getRequestPath(final HttpServletRequest request) {
        return request.getRequestURI();
    }
    
    private String getRequestBody(final HttpServletRequest request) {
    	try {
    		String contentType = request.getContentType();
    		if(contentType != null && contentType.startsWith("text")) {
    			StringBuilder stringBuilder = new StringBuilder();
    			BufferedReader bufferedReader = request.getReader();
    			String line;
    			while ((line = bufferedReader.readLine()) != null) {
    				stringBuilder.append(line);
    			}
    			return stringBuilder.toString();
    		}
    		return "Request body not exists or is not 'string'";
			
		} catch (Exception e) {
			log.warn("Error to get request body: {}", e.getMessage());
			return "Error to get request body";
		}
    }
    
    private String getRequestHttpMethod(final HttpServletRequest request) {
        return request.getMethod();
    }
    
    private String getRequestParams(final HttpServletRequest request) {
        final Map<String, String[]> parameterMap = request.getParameterMap();
        return parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining(", "));
    }
    
    private String getRequestHeaders(final HttpServletRequest request) {
        
    	Enumeration<String> headerNames = request.getHeaderNames();
    	
    	ArrayList<String> headerNamesList = Collections.list(headerNames);
    	if(headerNamesList.isEmpty()) {
    		return "[]";
    	}
    	
    	final String result = headerNamesList.stream()
        .map(headerName -> headerName + ": " + request.getHeader(headerName))
        .collect(Collectors.joining(", "));
    	
        return "[" + result + "]";
    }

    private int getResponseStatusCode(final HttpServletResponse response) {
        return response.getStatus();
    }
    
    private String getResponseHeaders(final HttpServletResponse response) {
        Collection<String> headerNames = response.getHeaderNames();
        
        if(headerNames.isEmpty()) {
        	return "[]";
        }
        
        final String result = headerNames.stream()
        .map(headerName -> headerName + ": " + response.getHeader(headerName))
        .collect(Collectors.joining(", "));
        
        return "[" + result + "]";
    }
    
    private String getResponseBody(final HttpServletResponse response) {
    	//Desenvolver com CustomHttpServletResponseWrapper extends HttpServletResponseWrapper
    	return "'getResponseBody' not implemented yet";//NOSONAR
    }

    private long millisecondsBetween(final LocalDateTime execStart, final LocalDateTime execEnd) {
        return Duration.between(execStart, execEnd).toMillis();
    }
    
    private String getClassMethodName(final Object handler) {
    	if(handler instanceof HandlerMethod handlerMethod) {
    		return handlerMethod.getMethod().getName();
    	}
    	return null;
    }
    
    private String getClassName(final Object handler) {
    	if(handler instanceof HandlerMethod handlerMethod) {
    		return handlerMethod.getBeanType().getSimpleName();
    	}
    	return null;
    }

}
