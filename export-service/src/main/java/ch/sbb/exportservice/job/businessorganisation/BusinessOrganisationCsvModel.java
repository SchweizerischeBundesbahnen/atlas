package ch.sbb.exportservice.job.businessorganisation;

import ch.sbb.atlas.model.Status;
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
public class BusinessOrganisationCsvModel {

  private String sboid;
  private String said;
  private String validFrom;
  private String validTo;
  private Integer organisationNumber;
  private Status status;
  private String descriptionDe;
  private String descriptionFr;
  private String descriptionIt;
  private String descriptionEn;
  private String abbreviationDe;
  private String abbreviationFr;
  private String abbreviationIt;
  private String abbreviationEn;
  private String businessTypesId;
  private String businessTypesDe;
  private String businessTypesIt;
  private String businessTypesFr;
  private String transportCompanyNumber;
  private String transportCompanyAbbreviation;
  private String transportCompanyBusinessRegisterName;
  private String creationTime;
  private String editionTime;

}
