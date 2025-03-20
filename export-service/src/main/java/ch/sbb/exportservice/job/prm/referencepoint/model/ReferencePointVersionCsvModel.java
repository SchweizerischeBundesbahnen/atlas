package ch.sbb.exportservice.job.prm.referencepoint.model;

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
public class ReferencePointVersionCsvModel extends BasePrmCsvModel {

  private String sloid;

  private String parentSloidServicePoint;

  private Integer parentNumberServicePoint;

  private String designation;

  private boolean mainReferencePoint;

  private String additionalInformation;

  private String referencePointType;

}
