package ch.sbb.exportservice.job.prm.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
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
public class WheelchairAccessibilityCsvModel {

  private String number;

  private String sloid;

  private String type;

  private WheelchairAccessibilityState accessibility;

  private String validFrom;

  private String validTo;

}
