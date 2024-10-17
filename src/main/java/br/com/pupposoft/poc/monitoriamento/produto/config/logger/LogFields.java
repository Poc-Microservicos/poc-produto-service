package br.com.pupposoft.poc.monitoriamento.produto.config.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogFields {
  
  CLASS("class"),
  ELAPSED_TIME("elapsed_time"),
  ERROR_CODE("error_code"),
  ERROR_MESSAGE("error_message"),
  EXCEPTION("exception"),
  EXEC_END("exec_end"),
  EXEC_START("exec_start"),
  FEIGN_CLIENT_ENDPOINT("feign_client_endpoint"),
  METHOD("method"),
  REQUEST_PARAMS("request_params"),
  STATUS_CODE("status_code"),
  USER_AGENT("user_agent"),
  TRACE_ID("trace_id"),
  PARAMS("params");

  private String key;
}
