package ch.sbb.importservice.service.bulk.reader;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataMappingError {

  private final String field;
  private final String errorValue;
  private final ExpectedType expectedType;

  public enum ExpectedType {
    INTEGER,
    DATE,
    ENUM,
    DOUBLE,
    BOOLEAN,
  }
}