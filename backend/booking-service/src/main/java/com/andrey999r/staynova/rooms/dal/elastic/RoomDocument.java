package com.andrey999r.staynova.rooms.dal.elastic;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDocument {

  @Id private Long id;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String title;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String description;

  @Field(type = FieldType.Keyword)
  private String availabilityStatus;

  @Field(type = FieldType.Double)
  private BigDecimal price;

  @Field(type = FieldType.Keyword)
  private String roomType;

  private String mainPhotoUrl;

  @Field(type = FieldType.Long)
  private Long hotelId;
}
