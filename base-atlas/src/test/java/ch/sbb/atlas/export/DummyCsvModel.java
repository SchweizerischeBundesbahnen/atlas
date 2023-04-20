package ch.sbb.atlas.export;

import java.time.LocalDate;
import lombok.Data;

@Data
public class DummyCsvModel {

  private final String value;
  private final LocalDate dateValue;
}
