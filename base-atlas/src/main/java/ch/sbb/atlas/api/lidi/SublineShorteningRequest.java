package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.model.DateRange;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SublineShorteningRequest {

  DateRange mainlineValidity;
  List<String> sublinesToShort;

}
