package br.com.pupposoft.poc.monitoriamento.produto.config.logger;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public FilterRegistrationBean<CustomRequestLoggingFilter> loggingFilterRegistration() {
        final FilterRegistrationBean<CustomRequestLoggingFilter> registration = new FilterRegistrationBean<>(logFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public CustomRequestLoggingFilter logFilter() {
        final CustomRequestLoggingFilter filter = new CustomRequestLoggingFilter();
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setIncludeQueryString(true);
        filter.setMaxPayloadLength(10000);
        filter.setAfterMessagePrefix("Request DATA: ");
        filter.setBeforeMessagePrefix("Response DATA: ");
        return filter;
    }

}
