package com.andrey999r.staynova.filters;

import com.andrey999r.staynova.properties.ReCaptchaProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RecaptchaGatewayFilterFactory
    extends AbstractGatewayFilterFactory<RecaptchaGatewayFilterFactory.Config> {

  private final WebClient webClient;
  private final ReCaptchaProperties recaptchaProperties;

  public RecaptchaGatewayFilterFactory(
      WebClient.Builder webClientBuilder, ReCaptchaProperties recaptchaProperties) {
    super(Config.class);
    this.webClient = webClientBuilder.build();
    this.recaptchaProperties = recaptchaProperties;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String token = exchange.getRequest().getHeaders().getFirst("X-Recaptcha-Token");
      if (token == null) {
        token = exchange.getRequest().getQueryParams().getFirst("g-recaptcha-response");
      }

      if (token == null) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange
            .getResponse()
            .writeWith(
                Mono.just(
                    exchange
                        .getResponse()
                        .bufferFactory()
                        .wrap("{\"error\":\"Missing recaptcha token\"}".getBytes())));
      }

      return verifyToken(token)
          .flatMap(
              verified -> {
                if (verified) {
                  return chain.filter(exchange);
                }
                log.warn("Recaptcha verification failed");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return exchange
                    .getResponse()
                    .writeWith(
                        Mono.just(
                            exchange
                                .getResponse()
                                .bufferFactory()
                                .wrap(
                                    "{\"error\":\"Recaptcha verification failed\"}"
                                        .getBytes())));
              });
    };
  }

  private Mono<Boolean> verifyToken(String token) {
    return webClient
        .post()
        .uri(recaptchaProperties.getUrl())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(buildFormBody(recaptchaProperties.getSecret(), token))
        .retrieve()
        .bodyToMono(RecaptchaResponse.class)
        .map(
            response ->
                response.success() && response.score() >= recaptchaProperties.getScoreThreshold())
        .onErrorResume(
            ex -> {
              log.error("Recaptcha verification request failed: {}", ex.getMessage());
              return Mono.just(false);
            });
  }

  private MultiValueMap<String, String> buildFormBody(String secret, String token) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("secret", secret);
    body.add("response", token);
    return body;
  }

  public static class Config {}

  private record RecaptchaResponse(boolean success, float score, String action) {}
}
