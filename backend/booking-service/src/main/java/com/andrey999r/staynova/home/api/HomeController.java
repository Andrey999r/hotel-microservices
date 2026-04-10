package com.andrey999r.staynova.home.api;

import com.andrey999r.staynova.home.bl.HomeService;
import com.andrey999r.staynova.home.dto.HomeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

  private final HomeService homeService;

  @GetMapping
  public ResponseEntity<HomeDto> getHome() {
    log.info("GET /home");
    HomeDto home = homeService.getHome();
    log.info(
        "GET /home → banner='{}', popularRooms={}",
        home.banner().title(),
        home.popularRooms().size());
    return ResponseEntity.ok(home);
  }
}
