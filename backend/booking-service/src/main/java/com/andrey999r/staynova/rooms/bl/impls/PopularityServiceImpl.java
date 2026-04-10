package com.andrey999r.staynova.rooms.bl.impls;

import com.andrey999r.staynova.rooms.bl.PopularityService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularityServiceImpl implements PopularityService {

  private static final String VIEWS_KEY = "room:views";
  private static final String LAST_VIEW_KEY = "room:last_view";
  private static final String POPULARITY_KEY = "room:popularity";

  private final RedisTemplate<String, String> popularityRedisTemplate;

  public void init(Long roomId) {
    popularityRedisTemplate.opsForZSet().addIfAbsent(POPULARITY_KEY, roomId.toString(), 0);
    popularityRedisTemplate.opsForZSet().addIfAbsent(VIEWS_KEY, roomId.toString(), 0);
  }

  public void increment(Long roomId) {
    popularityRedisTemplate.opsForZSet().incrementScore(VIEWS_KEY, roomId.toString(), 1);
    popularityRedisTemplate
        .opsForHash()
        .put(LAST_VIEW_KEY, roomId.toString(), String.valueOf(System.currentTimeMillis()));
    recalculate(roomId);
  }

  public void delete(Long roomId) {
    popularityRedisTemplate.opsForZSet().remove(POPULARITY_KEY, roomId.toString());
    popularityRedisTemplate.opsForZSet().remove(VIEWS_KEY, roomId.toString());
    popularityRedisTemplate.opsForHash().delete(LAST_VIEW_KEY, roomId.toString());
  }

  public List<Long> getTopRoomIds(int page, int size) {
    long start = (long) page * size;
    long end = start + size - 1;
    Set<String> ids = popularityRedisTemplate.opsForZSet().reverseRange(POPULARITY_KEY, start, end);
    if (ids == null) return List.of();
    return ids.stream().map(Long::parseLong).toList();
  }

  private void recalculate(Long roomId) {
    Double views = popularityRedisTemplate.opsForZSet().score(VIEWS_KEY, roomId.toString());
    String lastViewStr =
        (String) popularityRedisTemplate.opsForHash().get(LAST_VIEW_KEY, roomId.toString());

    if (views == null || lastViewStr == null) return;

    double hoursAgo = (System.currentTimeMillis() - Long.parseLong(lastViewStr)) / 3_600_000.0;
    double score = views / Math.pow(hoursAgo + 2, 1.5);

    popularityRedisTemplate.opsForZSet().add(POPULARITY_KEY, roomId.toString(), score);
  }

  @Scheduled(fixedRate = 3_600_000) // every hour
  public void recalculateAll() {
    Set<String> roomIds = popularityRedisTemplate.opsForZSet().range(POPULARITY_KEY, 0, -1);
    if (roomIds == null) return;
    roomIds.forEach(id -> recalculate(Long.parseLong(id)));
    log.info("Recalculated popularity for {} rooms", roomIds.size());
  }
}
