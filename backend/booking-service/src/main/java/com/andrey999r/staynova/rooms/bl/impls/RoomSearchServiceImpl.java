package com.andrey999r.staynova.rooms.bl.impls;

import com.andrey999r.staynova.availability.dal.AvailabilityStatus;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.bl.RoomSearchService;
import com.andrey999r.staynova.rooms.dal.Currency;
import com.andrey999r.staynova.rooms.dal.RoomRepository;
import com.andrey999r.staynova.rooms.dal.RoomType;
import com.andrey999r.staynova.rooms.dal.elastic.RoomDocument;
import com.andrey999r.staynova.rooms.dal.elastic.RoomSearchRepository;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomSearchServiceImpl implements RoomSearchService {

  private final RoomSearchRepository roomSearchRepository;
  private final RoomRepository roomRepository;

  @Override
  public void syncAll() {
    List<RoomEntity> all = roomRepository.findAll();
    List<RoomDocument> docs = all.stream().map(this::toDocument).toList();
    roomSearchRepository.saveAll(docs);
    log.info("Synced {} rooms to Elasticsearch", docs.size());
  }

  @Override
  public void index(RoomEntity room) {
    roomSearchRepository.save(toDocument(room));
    log.debug("Indexed room id={} in Elasticsearch", room.getId());
  }

  @Override
  public void delete(Long roomId) {
    roomSearchRepository.deleteById(roomId);
    log.debug("Deleted room id={} from Elasticsearch", roomId);
  }

  @Override
  public List<RoomDto> search(String query) {
    log.debug("Fuzzy ES search for rooms query='{}'", query);
    List<RoomDocument> docs = roomSearchRepository.fuzzySearch(query);
    return docs.stream().map(this::docToDto).toList();
  }

  private RoomDocument toDocument(RoomEntity room) {
    RoomDocument doc = new RoomDocument();
    doc.setId(room.getId());
    doc.setTitle(room.getTitle());
    doc.setDescription(room.getDescription());
    doc.setPrice(room.getPrice());
    doc.setMainPhotoUrl(room.getMainPhotoUrl());
    if (room.getAvailabilityStatus() != null)
      doc.setAvailabilityStatus(room.getAvailabilityStatus().name());
    if (room.getRoomType() != null) doc.setRoomType(room.getRoomType().name());
    if (room.getHotel() != null) doc.setHotelId(room.getHotel().getId());
    return doc;
  }

  private RoomDto docToDto(RoomDocument doc) {
    return new RoomDto(
        doc.getId(),
        doc.getRoomType() != null ? RoomType.valueOf(doc.getRoomType()) : null,
        doc.getAvailabilityStatus() != null
            ? AvailabilityStatus.valueOf(doc.getAvailabilityStatus())
            : null,
        doc.getMainPhotoUrl(),
        List.of(),
        null,
        doc.getTitle(),
        doc.getPrice(),
        Currency.RUB,
        null,
        doc.getHotelId());
  }
}
