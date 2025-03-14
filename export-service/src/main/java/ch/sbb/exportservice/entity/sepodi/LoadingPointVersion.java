package ch.sbb.exportservice.entity.sepodi;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.BaseEntity;
import ch.sbb.exportservice.entity.bodi.SharedBusinessOrganisation;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
public class LoadingPointVersion extends BaseEntity {

  private Long id;

  private Integer number;

  private String designation;

  private String designationLong;

  private boolean connectionPoint;

  private ServicePointNumber servicePointNumber;

  private SharedBusinessOrganisation servicePointSharedBusinessOrganisation;

  private String parentSloidServicePoint;

  private LocalDate validFrom;

  private LocalDate validTo;

}
