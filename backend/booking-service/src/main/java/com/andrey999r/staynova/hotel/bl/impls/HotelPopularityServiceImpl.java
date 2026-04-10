package com.andrey999r.staynova.hotel.bl.impls;

import com.andrey999r.staynova.hotel.bl.HotelPopularityService;
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
public class HotelPopularityServiceImpl implements HotelPopularityService {

  private static final String VIEWS_KEY = "hotel:views";
  private static final String LAST_VIEW_KEY = "hotel:last_view";
  private static final String POPULARITY_KEY = "hotel:popularity";

  private final RedisTemplate<String, String> popularityRedisTemplate;

  @Override
  public void init(Long hotelId) {
    popularityRedisTemplate.opsForZSet().addIfAbsent(POPULARITY_KEY, hotelId.toString(), 0);
    popularityRedisTemplate.opsForZSet().addIfAbsent(VIEWS_KEY, hotelId.toString(), 0);
  }

  @Override
  public void increment(Long hotelId) {
    popularityRedisTemplate.opsForZSet().incrementScore(VIEWS_KEY, hotelId.toString(), 1);
    popularityRedisTemplate
        .opsForHash()
        .put(LAST_VIEW_KEY, hotelId.toString(), String.valueOf(System.currentTimeMillis()));
    recalculate(hotelId);
  }

  @Override
  public void delete(Long hotelId) {
    popularityRedisTemplate.opsForZSet().remove(POPULARITY_KEY, hotelId.toString());
    popularityRedisTemplate.opsForZSet().remove(VIEWS_KEY, hotelId.toString());
    popularityRedisTemplate.opsForHash().delete(LAST_VIEW_KEY, hotelId.toString());
  }

  @Override
  public List<Long> getTopHotelIds(int page, int size) {
    long start = (long) page * size;
    long end = start + size - 1;
    Set<String> ids = popularityRedisTemplate.opsForZSet().reverseRange(POPULARITY_KEY, start, end);
    if (ids == null) {
      return List.of();
    }
    return ids.stream().map(Long::parseLong).toList();
  }

  @Override
  public double getScore(Long hotelId) {
    Double score = popularityRedisTemplate.opsForZSet().score(POPULARITY_KEY, hotelId.toString());
    return score != null ? score : 0.0;
  }

  private void recalculate(Long hotelId) {
    Double views = popularityRedisTemplate.opsForZSet().score(VIEWS_KEY, hotelId.toString());
    String lastViewStr =
        (String) popularityRedisTemplate.opsForHash().get(LAST_VIEW_KEY, hotelId.toString());

    if (views == null || lastViewStr == null) {
      return;
    }

    double hoursAgo = (System.currentTimeMillis() - Long.parseLong(lastViewStr)) / 3_600_000.0;
    double score = views / Math.pow(hoursAgo + 2, 1.5);
    popularityRedisTemplate.opsForZSet().add(POPULARITY_KEY, hotelId.toString(), score);
  }

  @Scheduled(fixedRate = 3_600_000)
  public void recalculateAll() {
    Set<String> hotelIds = popularityRedisTemplate.opsForZSet().range(POPULARITY_KEY, 0, -1);
    if (hotelIds == null) {
      return;
    }
    hotelIds.forEach(id -> recalculate(Long.parseLong(id)));
    log.info("Recalculated popularity for {} hotels", hotelIds.size());
  }
}
