package com.andrey999r.staynova.hotel.dal.elastic;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelDocument {

  @Id private Long id;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String name;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String description;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String country;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String address;
}
