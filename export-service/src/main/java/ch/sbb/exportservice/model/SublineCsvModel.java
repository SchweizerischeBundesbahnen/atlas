package ch.sbb.exportservice.model;

import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
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
public class SublineCsvModel {

  private String slnid;
  private String mainlineSlnid;
  private String validFrom;
  private String validTo;
  private Status status;
  private SublineType sublineType;
  private SublineConcessionType concessionType;
  private String description;
  private String longName;

  // From Line
  private String swissSublineNumber;
  private String swissLineNumber;
  private String number;
  private String shortNumber;
  private OfferCategory offerCategory;

  private String businessOrganisation;
  private String creationTime;
  private String editionTime;

}
