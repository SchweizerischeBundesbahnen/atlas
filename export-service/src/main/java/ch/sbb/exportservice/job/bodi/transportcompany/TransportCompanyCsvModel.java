package ch.sbb.exportservice.job.bodi.transportcompany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@FieldNameConstants
@EqualsAndHashCode
public class TransportCompanyCsvModel {

  private Long id;

  private String number;

  private String abbreviation;

  private String description;

  private String businessRegisterName;

  private String transportCompanyStatus;

  private String businessRegisterNumber;

  private String enterpriseId;

  private String ricsCode;

  private String businessOrganisationNumbers;

  private String comment;

  private String creationDate;

  private String editionDate;

}
