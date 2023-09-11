package ch.sbb.atlas.servicepointdirectory.migration.trafficpoints;

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
public class TrafficPointAtlasCsvModel {

  private String sloid;

  private Integer numberShort;

  private Integer uicCountryCode;

  private Integer number;

  private String validFrom;

  private String validTo;

  private String designation;

  private String designationOperational;

  private Double length;

  private Double boardingAreaHeight;

  private Double compassDirection;

  private String parentSloid;

  private String trafficPointElementType;

  private Double lv95East;

  private Double lv95North;

  private Double wgs84East;

  private Double wgs84North;

  private Double wgs84WebEast;

  private Double wgs84WebNorth;

  private Double height;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime creationDate;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime editionDate;

  private String parentSloidServicePoint;

  private String designationOfficial;

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

}
