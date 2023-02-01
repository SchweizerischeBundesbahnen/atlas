package ch.sbb.atlas.api.bodi;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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
@Schema(name = "Company")
public class CompanyModel {

  @Schema(description = "Company UIC Code")
  private Long uicCode;

  @Schema(description = "Company Name")
  private String name;

  @Schema(description = "Company URL")
  private String url;

  @Schema(description = "Validity Start Date")
  private LocalDate startValidity;

  @Schema(description = "Validity End Date")
  private LocalDate endValidity;

  @Schema(description = "Company short name")
  private String shortName;

  @Schema(description = "Free text")
  private String freeText;

  @Schema(description = "Country Code (ISO)")
  private String countryCodeIso;

}
