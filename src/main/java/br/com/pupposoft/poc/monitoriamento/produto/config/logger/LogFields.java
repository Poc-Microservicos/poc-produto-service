package br.com.pupposoft.poc.monitoriamento.produto.config.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogFields {
	TRACE_ID("trace_id"),

	EXEC_START("exec_start"),
	EXEC_END("exec_end"),
	ELAPSED_TIME("elapsed_time"),

	ERROR_MESSAGE("error_message"),
	EXCEPTION("exception"),

	CLASS("class"),
	CLASS_METHOD("class_method"),

	REQUEST_PARAMS("request_params"),
	REQUEST_HEADERS("request_headers"),
	REQUEST_PATH("request_path"),
	REQUEST_BODY("request_params"),
	REQUEST_HTTP_METHOD("request_http_method"),
	REQUEST_USER_AGENT("request_user_agent"),

	RESPONSE_STATUS_CODE("response_status_code"),
	RESPONSE_HEADERS("response_headers"),
	RESPONSE_BODY("response_body"),
	;

	private String key;
}
