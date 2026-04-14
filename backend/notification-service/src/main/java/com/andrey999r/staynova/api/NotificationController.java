package com.andrey999r.staynova.api;

import com.andrey999r.staynova.api.dto.NotificationResponseDto;
import com.andrey999r.staynova.bl.NotificationService;
import java.util.List;

import com.andrey999r.staynova.common.kafka.dto.DeviceSubscriptionDto;
import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

  private static final String PATH_GET_ALL = "";
  private static final String PATH_SUBSCRIBE = "/subscribe";
  private static final String PATH_UNSUBSCRIBE = "/unsubscribe";

  private final NotificationService notificationService;

  @GetMapping(PATH_GET_ALL)
  public ResponseEntity<List<NotificationResponseDto>> getAllNotifications() {
    log.info("Received request to get all notifications");
    List<NotificationResponseDto> result = notificationService.getAllNotifications();
    log.info("Returning {} notifications", result.size());
    return ResponseEntity.ok(result);
  }

  @PostMapping(PATH_SUBSCRIBE)
  public ResponseEntity<Void> subscribe(@RequestBody DeviceSubscriptionDto dto) {
    log.info("Received subscribe request for channel={}", dto.channelType());
    notificationService.subscribe(dto);
    log.info("Subscription created for channel={}", dto.channelType());
    return ResponseEntity.status(201).build();
  }

  @DeleteMapping(PATH_UNSUBSCRIBE)
  public ResponseEntity<Void> unsubscribe(@RequestParam ChannelType channelType) {
    log.info("Received unsubscribe request for channel={}", channelType);
    notificationService.unsubscribe(channelType);
    log.info("Unsubscribed from channel={}", channelType);
    return ResponseEntity.noContent().build();
  }
}
