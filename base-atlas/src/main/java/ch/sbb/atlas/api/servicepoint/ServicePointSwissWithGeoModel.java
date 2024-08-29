package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
@Schema(name = "servicePointSwissWithGeo")
public class ServicePointSwissWithGeoModel {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
      + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String sloid;

  @Schema(description = """
      This ID helps identify versions of a service point in the use case front end and/or update.
      This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a service point.
      To do this, use the sloid or number in combination with the data range (valid from/valid until).
      """,
      accessMode = AccessMode.READ_ONLY, example = "1")
  private List<Detail> details;

  @AllArgsConstructor
  @Data
  public static class Detail {

    @Schema(description = """
        This ID helps identify versions of a service point in the use case front end and/or update.
        This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a service point.
        To do this, use the sloid or number in combination with the data range (valid from/valid until).
        """,
        accessMode = AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(description = "Valid from")
    @NotNull
    private LocalDate validFrom;

  }

}
