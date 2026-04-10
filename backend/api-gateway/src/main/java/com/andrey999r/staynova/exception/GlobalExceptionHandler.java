package com.andrey999r.staynova.exception;

import com.andrey999r.staynova.api.dto.ExceptionDto;
import com.andrey999r.staynova.exception.custom.RateLimitExceededException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler implements WebExceptionHandler {

  private final ObjectMapper objectMapper;

  public GlobalExceptionHandler() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.disable(
        com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    HttpStatus status;
    String message;

    if (ex instanceof RateLimitExceededException rle) {
      status = rle.getStatus();
      message = rle.getMessage();
      log.warn("Rate limit exceeded: {}", message);
    } else if (ex instanceof BaseException bte) {
      status = bte.getStatus();
      message = bte.getMessage();
      log.warn("Token exception [{}]: {}", ex.getClass().getSimpleName(), message);
    } else if (ex instanceof MethodNotAllowedException mna) {
      status = HttpStatus.METHOD_NOT_ALLOWED;
      message = "Method not allowed: " + mna.getHttpMethod();
      log.warn("Method not allowed [{}]: {}", mna.getHttpMethod(), exchange.getRequest().getPath());
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      message = "Internal server error";
      log.error(
          "Unexpected gateway error [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    }

    exchange.getResponse().setStatusCode(status);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    ExceptionDto body =
        new ExceptionDto(message, ex.getClass().getSimpleName(), LocalDateTime.now());

    byte[] bytes;
    try {
      bytes = objectMapper.writeValueAsBytes(body);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize error response", e);
      return exchange.getResponse().setComplete();
    }

    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}
