package br.com.pupposoft.poc.monitoriamento.produto.config.logger;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {
	
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        String maskedMessage = maskSensitiveData(message);
        super.afterRequest(request, maskedMessage);
    }

    @Override
    protected String getMessagePayload(HttpServletRequest request) {
    	String messagePayload = super.getMessagePayload(request);
    	return messagePayload;
    }
    
    private String maskSensitiveData(String message) {
        return message.replaceAll("(\"password\":\")(.*?)(\")", "$1****$3")
        		.replaceAll("(\"senha\":\")(.*?)(\")", "$1****$3");
    }

}

