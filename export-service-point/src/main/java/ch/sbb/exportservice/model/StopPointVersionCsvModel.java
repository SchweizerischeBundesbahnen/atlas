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
public class StopPointVersionCsvModel {

  private String sloid;

  private Integer number;

  private Integer checkDigit;

  private String designation;

  private String validFrom;

  private String validTo;

  private String parentSloidServicePoint;

  private String meansOfTransport;

  private String creationDate;

  private String editionDate;

//  private String servicePointBusinessOrganisation;
//
//  private Integer servicePointBusinessOrganisationNumber;
//
//  private String servicePointBusinessOrganisationAbbreviationDe;
//
//  private String servicePointBusinessOrganisationAbbreviationFr;
//
//  private String servicePointBusinessOrganisationAbbreviationIt;
//
//  private String servicePointBusinessOrganisationAbbreviationEn;
//
//  private String servicePointBusinessOrganisationDescriptionDe;
//
//  private String servicePointBusinessOrganisationDescriptionFr;
//
//  private String servicePointBusinessOrganisationDescriptionIt;
//
//  private String servicePointBusinessOrganisationDescriptionEn;

}
