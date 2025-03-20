package ch.sbb.exportservice.job.prm.toilet.model;

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
public class ToiletVersionCsvModel extends BasePrmCsvModel {

  private String sloid;

  private String parentSloidServicePoint;

  private Integer parentNumberServicePoint;

  private String designation;

  private String additionalInformation;

  private String wheelchairToilet;

  private String validFrom;

  private String validTo;

  private String creationDate;

  private String editionDate;

}
