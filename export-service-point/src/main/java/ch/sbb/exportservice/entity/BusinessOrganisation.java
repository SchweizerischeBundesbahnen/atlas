package ch.sbb.exportservice.entity;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
public class BusinessOrganisation {

  private String businessOrganisation;
  private Integer businessOrganisationNumber;
  private String businessOrganisationAbbreviationDe;
  private String businessOrganisationAbbreviationFr;
  private String businessOrganisationAbbreviationIt;
  private String businessOrganisationAbbreviationEn;
  private String businessOrganisationDescriptionDe;
  private String businessOrganisationDescriptionFr;
  private String businessOrganisationDescriptionIt;
  private String businessOrganisationDescriptionEn;

}
