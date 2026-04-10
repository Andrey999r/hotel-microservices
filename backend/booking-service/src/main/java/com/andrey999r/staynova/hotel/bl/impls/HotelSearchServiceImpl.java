package com.andrey999r.staynova.hotel.bl.impls;

import com.andrey999r.staynova.hotel.bl.HotelSearchService;
import com.andrey999r.staynova.hotel.bl.dto.HotelDto;
import com.andrey999r.staynova.hotel.dal.HotelRatingRepository;
import com.andrey999r.staynova.hotel.dal.HotelRepository;
import com.andrey999r.staynova.hotel.dal.elastic.HotelDocument;
import com.andrey999r.staynova.hotel.dal.elastic.HotelSearchRepository;
import com.andrey999r.staynova.hotel.dal.entities.HotelEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelSearchServiceImpl implements HotelSearchService {

  private final HotelSearchRepository hotelSearchRepository;
  private final HotelRepository hotelRepository;
  private final HotelRatingRepository hotelRatingRepository;

  @Override
  public void index(HotelEntity hotel) {
    hotelSearchRepository.save(toDocument(hotel));
    log.debug("Indexed hotel id={} in Elasticsearch", hotel.getId());
  }

  @Override
  public void delete(Long hotelId) {
    hotelSearchRepository.deleteById(hotelId);
    log.debug("Deleted hotel id={} from Elasticsearch", hotelId);
  }

  @Override
  public void syncAll() {
    List<HotelEntity> all = hotelRepository.findAll();
    List<HotelDocument> docs = all.stream().map(this::toDocument).toList();
    hotelSearchRepository.saveAll(docs);
    log.info("Synced {} hotels to Elasticsearch", docs.size());
  }

  @Override
  public List<HotelDto> search(String query) {
    List<HotelDocument> docs = hotelSearchRepository.fuzzySearch(query);
    return docs.stream().map(this::docToDto).toList();
  }

  private HotelDocument toDocument(HotelEntity h) {
    HotelDocument doc = new HotelDocument();
    doc.setId(h.getId());
    doc.setName(h.getName());
    doc.setDescription(h.getDescription());
    doc.setAddress(h.getAddress());
    doc.setCountry(h.getCountry() != null ? h.getCountry().name() : null);
    return doc;
  }

  private HotelDto docToDto(HotelDocument doc) {
    return hotelRepository
        .findById(doc.getId())
        .map(
            h ->
                new HotelDto(
                    h.getId(),
                    h.getName(),
                    h.getDescription(),
                    h.getAddress(),
                    h.getMainPhotoUrl(),
                    h.getGalleryUrl(),
                    hotelRatingRepository.getAverageRating(h.getId()),
                    hotelRatingRepository.countByHotelId(h.getId()),
                    h.getCountry(),
                    List.of()))
        .orElse(null);
  }
}
