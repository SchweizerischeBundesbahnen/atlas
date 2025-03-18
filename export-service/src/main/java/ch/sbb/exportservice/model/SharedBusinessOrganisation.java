package ch.sbb.exportservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
public class SharedBusinessOrganisation {

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
