package ch.sbb.exportservice.job.prm.parkinglot.model;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
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
@EqualsAndHashCode(callSuper = true)
public class ParkingLotVersionCsvModel extends BasePrmCsvModel {

  private String sloid;

  private String parentSloidServicePoint;

  private Integer parentNumberServicePoint;

  private String designation;

  private String additionalInformation;

  private BooleanOptionalAttributeType placesAvailable;

  private BooleanOptionalAttributeType prmPlacesAvailable;

}
