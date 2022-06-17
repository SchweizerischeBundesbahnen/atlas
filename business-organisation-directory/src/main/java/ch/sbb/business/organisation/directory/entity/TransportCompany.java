package ch.sbb.business.organisation.directory.entity;

import ch.sbb.business.organisation.directory.service.TransportCompanyStatus;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Entity(name = "transport_company")
public class TransportCompany {

  @Id
  private Long id;

  private String number;

  private String abbreviation;

  private String description;

  private String businessRegisterName;

  @Enumerated(EnumType.STRING)
  private TransportCompanyStatus transportCompanyStatus;

  private String businessRegisterNumber;

  private String enterpriseId;

  private String ricsCode;

  private String businessOrganisationNumbers;

  private String comment;

}
