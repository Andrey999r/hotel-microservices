package com.andrey999r.staynova.rooms.dal.elastic;

import java.util.List;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RoomSearchRepository extends ElasticsearchRepository<RoomDocument, Long> {

  List<RoomDocument> findByTitleContainingOrDescriptionContaining(String title, String description);

  @Query(
      """
      {
        "multi_match": {
          "query": "?0",
          "fields": ["title^2", "description"],
          "fuzziness": "AUTO",
          "operator": "OR"
        }
      }
      """)
  List<RoomDocument> fuzzySearch(String query);
}
