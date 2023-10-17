package ch.sbb.exportservice.model;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
public class TrafficPointVersionCsvModel {

  private String sloid;

  private Integer numberShort;

  private Integer uicCountryCode;

  private Integer number;

  private Integer checkDigit;

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

  private Double height;

  private String creationDate;

  private String editionDate;

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
