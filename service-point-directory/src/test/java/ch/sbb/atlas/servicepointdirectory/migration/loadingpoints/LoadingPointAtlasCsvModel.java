package ch.sbb.atlas.servicepointdirectory.migration.loadingpoints;

import ch.sbb.atlas.imports.servicepoint.deserializer.LocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
public class LoadingPointAtlasCsvModel {

  private Integer number;

  private String designation;

  private String designationLong;

  private Boolean connectionPoint;

  private String validFrom;

  private String validTo;

  private Integer servicePointNumber;

  private String parentSloidServicePoint;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime creationDate;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime editionDate;

  private String servicePointBusinessOrganisation;

  private Integer servicePointBusinessOrganisationNumber;

  private String servicePointBusinessOrganisationAbbreviationDe;

  private String servicePointBusinessOrganisationAbbreviationFr;

  private String servicePointBusinessOrganisationAbbreviationIt;

  private String servicePointBusinessOrganisationAbbreviationEn;

  private String servicePointBusinessOrganisationDescriptionDe;

  private String servicePointBusinessOrganisationDescriptionFr;

  private String servicePointBusinessOrganisationDescriptionIt;

  private String servicePointBusinessOrganisationDescriptionEn;

  public String getServicePointNumberAndLoadingPointNumberKey() {
    return getServicePointNumber() + "-" + getNumber();
  }

}
