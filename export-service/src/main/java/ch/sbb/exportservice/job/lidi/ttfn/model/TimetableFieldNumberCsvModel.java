package ch.sbb.exportservice.job.lidi.ttfn.model;

import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
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
  private String descriptionOutwardLine1;
  private String descriptionOutwardLine2;
  private String descriptionOutwardLine3;
  private String descriptionReturnLine1;
  private String descriptionReturnLine2;
  private String descriptionReturnLine3;
  private TtfnMeanOfTransport meanOfTransport;
  private String lineRelations;
  private String creationTime;
  private String editionTime;

}
