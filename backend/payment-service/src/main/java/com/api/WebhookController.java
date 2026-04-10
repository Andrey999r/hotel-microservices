package com.api;

import com.api.dto.WebhookDto;
import com.business.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<Void> handleWebhook(@Valid @RequestBody WebhookDto webhookDto) {
    log.info("Received webhook for sessionId={}", webhookDto.sessionId());
    paymentService.processWebhook(webhookDto);
    return ResponseEntity.ok().build();
  }
}
