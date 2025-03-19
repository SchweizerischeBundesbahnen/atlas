package ch.sbb.exportservice.job.lidi.line.model;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
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
public class LineCsvModel {

  private String slnid;
  private String validFrom;
  private String validTo;
  private Status status;
  private LineType lineType;
  private LineConcessionType concessionType;
  private String swissLineNumber;
  private String description;
  private String longName;
  private String number;
  private String shortNumber;
  private OfferCategory offerCategory;
  private String businessOrganisation;
  private String comment;
  private String creationTime;
  private String editionTime;

}
