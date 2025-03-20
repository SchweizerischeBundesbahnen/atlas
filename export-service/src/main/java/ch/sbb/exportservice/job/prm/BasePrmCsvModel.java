package ch.sbb.exportservice.job.prm;

import ch.sbb.atlas.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldNameConstants
@SuperBuilder
public abstract class BasePrmCsvModel {

  private String validFrom;

  private String validTo;

  private String creationDate;

  private String editionDate;

  private Status status;

}
