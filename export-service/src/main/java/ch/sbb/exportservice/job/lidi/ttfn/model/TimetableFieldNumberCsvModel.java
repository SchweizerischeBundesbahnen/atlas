package ch.sbb.exportservice.job.lidi.ttfn.model;

import ch.sbb.atlas.model.Status;
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
public class TimetableFieldNumberCsvModel {

  private String ttfnid;
  private String validFrom;
  private String validTo;
  private Status status;
  private String swissTimetableFieldNumber;
  private String number;
  private String businessOrganisation;
  private String description;
  private String comment;
  private String lineRelations;
  private String creationTime;
  private String editionTime;

}
