package ch.sbb.exportservice.aggregator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.batch.item.file.transform.LineAggregator;

public class ServicePointJsonItemAggregator<ServicePointVersion> implements LineAggregator<ServicePointVersion> {

  ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String aggregate(ServicePointVersion item) {
    objectMapper.registerModule(new JavaTimeModule());
    try {
      return objectMapper.writeValueAsString(item) + ",";
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
