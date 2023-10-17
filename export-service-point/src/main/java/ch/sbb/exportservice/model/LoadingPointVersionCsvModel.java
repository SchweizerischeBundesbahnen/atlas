package ch.sbb.exportservice.model;

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
public class LoadingPointVersionCsvModel {

  private Integer number;

  private Integer checkDigit;

  private String designation;

  private String designationLong;

  private Boolean connectionPoint;

  private String validFrom;

  private String validTo;

  private Integer servicePointNumber;

  private String parentSloidServicePoint;

  private String creationDate;

  private String editionDate;

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
