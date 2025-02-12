package ch.sbb.exportservice.entity.bodi;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.entity.BaseEntity;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Immutable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Immutable
public class BusinessOrganisation extends BaseEntity {

  private Long id;
  private String sboid;

  private Status status;

  private String abbreviationDe;
  private String abbreviationFr;
  private String abbreviationIt;
  private String abbreviationEn;

  private String descriptionDe;
  private String descriptionFr;
  private String descriptionIt;
  private String descriptionEn;

  private Integer organisationNumber;
  private String contactEnterpriseEmail;

  // todo: check: @Convert(converter = BusinessTypeConverter.class)
  private Set<BusinessType> businessTypes;

  private LocalDate validFrom;
  private LocalDate validTo;

  private String number;
  private String abbreviation;
  private String businessRegisterName;

}
