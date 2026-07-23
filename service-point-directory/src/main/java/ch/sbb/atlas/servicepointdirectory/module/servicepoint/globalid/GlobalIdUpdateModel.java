package ch.sbb.atlas.servicepointdirectory.module.servicepoint.globalid;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.validation.TrimmedNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_128)
  @TrimmedNotBlank
  @Schema(description = "Official Global-ID linking a foreign stop to its national reference system. Only writable for "
      + "German (Didok country code 11, 80) and Austrian (12, 81) stops and must then start with 'de:' respectively "
      + "'at:'. Examples: de:05770:1282, at:42:9379")
  private String globalId;

}
