package ch.sbb.atlas.servicepointdirectory.module.servicepoint.globalid;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Schema(name = "GlobalIdUpdate")
public class GlobalIdUpdateModel {

  @Size(max = AtlasFieldLengths.LENGTH_128)
  @Schema(description = "Official Global-ID linking a foreign stop to its national reference system. Only writable for "
      + "German (Didok country code 11, 80) and Austrian (12, 81) stops and must then start with 'de:' respectively "
      + "'at:'. May be empty. Examples: de:05770:1282, at:42:9379")
  private String globalId;

}
