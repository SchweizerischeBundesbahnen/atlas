package ch.sbb.exportservice.job.bodi.transportcompany;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import java.time.LocalDateTime;
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
public class TransportCompany {

  private Long id;

  private String number;

  private String abbreviation;

  private String description;

  private String businessRegisterName;

  private TransportCompanyStatus transportCompanyStatus;

  private String businessRegisterNumber;

  private String enterpriseId;

  private String ricsCode;

  private String businessOrganisationNumbers;

  private String comment;

  private LocalDateTime creationDate;

  private LocalDateTime editionDate;

}
