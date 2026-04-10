package com.andrey999r.staynova.hotel.dal.elastic;

import java.util.List;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HotelSearchRepository extends ElasticsearchRepository<HotelDocument, Long> {

  @Query(
      """
      {
        "multi_match": {
          "query": "?0",
          "fields": ["name^3", "description", "address", "country"],
          "fuzziness": "2",
          "prefix_length": 0,
          "max_expansions": 50,
          "operator": "OR"
        }
      }
      """)
  List<HotelDocument> fuzzySearch(String query);
}
