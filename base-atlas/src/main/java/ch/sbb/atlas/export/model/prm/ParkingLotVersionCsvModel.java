package ch.sbb.atlas.export.model.prm;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
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
public class ParkingLotVersionCsvModel {

  private String sloid;

  private String parentSloidServicePoint;

  private Integer parentNumberServicePoint;

  private String designation;

  private String additionalInformation;

  private BooleanOptionalAttributeType placesAvailable;

  private BooleanOptionalAttributeType prmPlacesAvailable;

  private String validFrom;

  private String validTo;

  private String creationDate;

  private String editionDate;

}
