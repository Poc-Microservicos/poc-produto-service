package br.com.pupposoft.poc.monitoriamento.produto.config.logger;

import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.CLASS;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.ELAPSED_TIME;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.EXEC_END;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.EXEC_START;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.METHOD;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.REQUEST_PARAMS;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.STATUS_CODE;
import static br.com.pupposoft.poc.monitoriamento.produto.config.logger.LogFields.USER_AGENT;
import static net.logstash.logback.argument.StructuredArguments.kv;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingRequestServiceInterceptor implements HandlerInterceptor {

    private static final String HEADER_USER_AGENT = "User-Agent";

    //@Autowired
    //private Tracer tracer;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) throws Exception {
        request.setAttribute(EXEC_START.getKey(), LocalDateTime.now());
        //request.setAttribute(TRACE_ID.getKey(), tracer.currentSpan().context().traceIdString());
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final Exception ex) {   
        try {
        	
        	if(handler == null) {
        		log.info("handler intecepted is null. Ignoring logs for it.");
        	} else if(handler instanceof HandlerMethod handlerMethod) {
        		final LocalDateTime execStart = (LocalDateTime) request.getAttribute(EXEC_START.getKey());
        		logData(request, response, handlerMethod, execStart);        		
  
        		
        	} else {
        		log.info("handler intecepted is not a HandlerMethod. Ignoring logs for it. {}", handler);
        	}
        	
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void logData(final HttpServletRequest request, final HttpServletResponse response, final HandlerMethod handler,
            final LocalDateTime execStart) {
        final LocalDateTime execEnd = LocalDateTime.now();
        log.info("Service elapsed time for "
        		+ "{}, "
        		+ "{}, "
        		+ "{}, "
        		+ "{}, "
        		+ "{}, "
        		+ "{}, "
        		+ "{}, "
        		+ "{}",
                kv(METHOD.getKey(), handler.getMethod().getName()),
                kv(USER_AGENT.getKey(), request.getHeader(HEADER_USER_AGENT)),
                kv(CLASS.getKey(), handler.getBeanType().getSimpleName()),
                kv(REQUEST_PARAMS.getKey(), getRequestParams(request)),
                kv(EXEC_START.getKey(), execStart.toString()),
                kv(EXEC_END.getKey(), execEnd.toString()),
                kv(ELAPSED_TIME.getKey(), millisecondsBetween(execStart, execEnd)),
                kv(STATUS_CODE.getKey(), response.getStatus()));
    }

    private String getRequestParams(final HttpServletRequest request) {
        final Map<String, String[]> parameterMap = request.getParameterMap();
        return parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    private long millisecondsBetween(final LocalDateTime execStart, final LocalDateTime execEnd) {
        return Duration.between(execStart, execEnd).toMillis();
    }

}
