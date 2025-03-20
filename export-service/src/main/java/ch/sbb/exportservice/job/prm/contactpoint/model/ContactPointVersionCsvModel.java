package ch.sbb.exportservice.job.prm.contactpoint.model;

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
public class ContactPointVersionCsvModel extends BasePrmCsvModel {

  private String sloid;

  private String parentSloidServicePoint;

  private Integer parentNumberServicePoint;

  private String type;

  private String designation;

  private String additionalInformation;

  private String inductionLoop;

  private String openingHours;

  private String wheelchairAccess;

}
