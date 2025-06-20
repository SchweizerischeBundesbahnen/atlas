package ch.sbb.exportservice.job.lidi.line.entity;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.exportservice.job.BaseEntity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Immutable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Immutable
public class Line extends BaseEntity implements Versionable {

  private Long id;
  private String slnid;

  private LocalDate validFrom;
  private LocalDate validTo;

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

}
