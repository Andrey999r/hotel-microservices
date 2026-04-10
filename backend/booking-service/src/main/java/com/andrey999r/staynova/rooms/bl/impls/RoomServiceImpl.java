package com.andrey999r.staynova.rooms.bl.impls;

import com.andrey999r.staynova.general.Mapper;
import com.andrey999r.staynova.general.exception.custom.room.InvalidPhotoIdException;
import com.andrey999r.staynova.general.exception.custom.room.InvalidRoomIdException;
import com.andrey999r.staynova.general.exception.custom.room.InvalidSearchQueryException;
import com.andrey999r.staynova.general.exception.custom.room.RoomNotFoundException;
import com.andrey999r.staynova.rooms.api.dto.PopularFilterDto;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.api.dto.RoomFilterDto;
import com.andrey999r.staynova.rooms.bl.PopularityService;
import com.andrey999r.staynova.rooms.bl.RoomSearchService;
import com.andrey999r.staynova.rooms.bl.RoomService;
import com.andrey999r.staynova.rooms.dal.RoomRepository;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
  private final RoomRepository roomRepository;
  private final Mapper mapper;
  private final RoomSearchService roomSearchService;
  private final PopularityService popularityService;

  public RoomEntity getRoomEntityById(Long roomId) {
    log.debug("Fetching room entity by id={}", roomId);
    return roomRepository
        .findById(roomId)
        .orElseThrow(
            () -> {
              log.warn("Room entity not found: id={}", roomId);
              return new RoomNotFoundException(roomId);
            });
  }

  public RoomDto getRoomById(Long roomId) {
    if (roomId < 1) {
      log.warn("Invalid room id requested: {}", roomId);
      throw new InvalidRoomIdException();
    }
    log.info("Getting room by id={}", roomId);
    RoomEntity roomEntity =
        roomRepository
            .findById(roomId)
            .orElseThrow(
                () -> {
                  log.warn("Room not found: id={}", roomId);
                  return new RoomNotFoundException(roomId);
                });
    popularityService.increment(roomId);
    return mapper.roomToDomain(roomEntity);
  }

  public String getPhotoById(long roomId, long photoId) {
    if (roomId < 1 || photoId < 1) {
      log.warn("Invalid roomId={} or photoId={}", roomId, photoId);
      throw new InvalidRoomIdException();
    }
    log.info("Getting photo photoId={} for room roomId={}", photoId, roomId);
    RoomEntity room =
        roomRepository
            .findById(roomId)
            .orElseThrow(
                () -> {
                  log.warn("Room not found: id={}", roomId);
                  return new RoomNotFoundException(roomId);
                });
    if (photoId == 1) {
      return room.getMainPhotoUrl();
    }
    List<String> photoUrls = room.getPhotoUrls();
    int index = (int) (photoId - 2);
    if (index < 0 || index >= photoUrls.size()) {
      log.warn(
          "Invalid photoId={} for room roomId={}, available={}", photoId, roomId, photoUrls.size());
      throw new InvalidPhotoIdException();
    }
    return photoUrls.get(index);
  }

  public List<RoomDto> getAllRoomsByFilter(RoomFilterDto filter) {
    log.info("Getting rooms by filter: {}", filter);
    Integer pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
    Integer pageSize = filter.pageSize() != null ? filter.pageSize() : 10;

    if (filter.sortByPrice() == null || filter.sortByPrice().isBlank()) {
      List<Long> popularIds = popularityService.getTopRoomIds(pageNumber, pageSize);

      if (!popularIds.isEmpty()) {
        Map<Long, RoomDto> roomMap =
            roomRepository
                .findAllByFilter(
                    filter.roomId(),
                    filter.availabilityStatus() != null ? filter.availabilityStatus().name() : null,
                    filter.title(),
                    filter.description(),
                    filter.price(),
                    Pageable.unpaged())
                .stream()
                .collect(Collectors.toMap(RoomEntity::getId, mapper::roomToDomain));

        List<RoomDto> sorted =
            popularIds.stream().map(roomMap::get).filter(Objects::nonNull).toList();

        List<Long> sortedIds = sorted.stream().map(RoomDto::id).toList();
        List<RoomDto> rest =
            roomMap.values().stream().filter(r -> !sortedIds.contains(r.id())).toList();

        List<RoomDto> result = new ArrayList<>(sorted);
        result.addAll(rest);
        List<RoomDto> page = result.stream().limit(pageSize).toList();
        log.info("Returning {} rooms sorted by popularity", page.size());
        return page;
      }
    }

    Sort sort = Sort.unsorted();
    if ("desc".equalsIgnoreCase(filter.sortByPrice())) {
      sort = Sort.by(Sort.Direction.DESC, "price");
    } else if ("asc".equalsIgnoreCase(filter.sortByPrice())) {
      sort = Sort.by(Sort.Direction.ASC, "price");
    }

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    List<RoomEntity> allRooms =
        roomRepository.findAllByFilter(
            filter.roomId(),
            filter.availabilityStatus() != null ? filter.availabilityStatus().name() : null,
            filter.title(),
            filter.description(),
            filter.price(),
            pageable);
    List<RoomDto> result = allRooms.stream().map(mapper::roomToDomain).toList();
    log.info("Returning {} rooms", result.size());
    return result;
  }

  public List<RoomDto> search(String query) {
    if (query == null || query.isBlank() || query.length() >= 200) {
      log.warn("Invalid search query: '{}'", query);
      throw new InvalidSearchQueryException();
    }
    log.info("Searching rooms by query='{}'", query);
    List<RoomDto> results = roomSearchService.search(query);
    log.info("Search returned {} results for query='{}'", results.size(), query);
    return results;
  }

  public List<RoomDto> getAllPopularByFilter(PopularFilterDto filter) {
    int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
    int pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
    log.info("Getting popular rooms page={}, size={}", pageNumber, pageSize);

    List<Long> roomIds = popularityService.getTopRoomIds(pageNumber, pageSize);
    if (roomIds.isEmpty()) {
      log.info("No popular rooms found");
      return List.of();
    }

    Map<Long, RoomDto> roomMap =
        roomRepository.findAllById(roomIds).stream()
            .collect(Collectors.toMap(RoomEntity::getId, mapper::roomToDomain));

    List<RoomDto> result = roomIds.stream().map(roomMap::get).filter(Objects::nonNull).toList();
    log.info("Returning {} popular rooms", result.size());
    return result;
  }

  @PostConstruct
  private void syncExistingRooms() {
    log.info("Syncing existing rooms to popularity Redis...");
    List<RoomEntity> allRooms = roomRepository.findAll();
    for (RoomEntity room : allRooms) {
      popularityService.init(room.getId());
    }
    log.info("Synced {} rooms to Redis popularity store", allRooms.size());
  }
}
