package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.model.DateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "")
public class SublineShorteningRequest {

  DateRange mainlineValidity;
  List<String> sublinesToShort;

}
