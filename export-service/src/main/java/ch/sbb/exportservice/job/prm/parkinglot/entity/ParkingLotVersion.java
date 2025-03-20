package ch.sbb.exportservice.job.prm.parkinglot.entity;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.BasePrmEntity;
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
public class ParkingLotVersion extends BasePrmEntity {

  private Long id;

  private String sloid;

  private String parentServicePointSloid;

  private ServicePointNumber parentServicePointNumber;

  private String designation;

  private String additionalInformation;

  private BooleanOptionalAttributeType placesAvailable;

  private BooleanOptionalAttributeType prmPlacesAvailable;

  private LocalDate validFrom;

  private LocalDate validTo;

}
