package br.com.pupposoft.poc.monitoriamento.produto.config.logger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import brave.Tracer;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(LoggingRequestServiceInterceptor.class)
public class LoggingAutoConfiguration implements WebMvcConfigurer {

	private final Tracer tracer;
	
	@Override
	@ConditionalOnMissingClass
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(getLoggingInterceptor());
	}

	@Bean
	public LoggingRequestServiceInterceptor getLoggingInterceptor() {
		return new LoggingRequestServiceInterceptor(tracer);
	}

}
