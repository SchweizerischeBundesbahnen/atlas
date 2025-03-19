package ch.sbb.exportservice.job.lidi.subline;

import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
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
public class Subline extends BaseEntity {

  private Long id;
  private String slnid;
  private String mainlineSlnid;

  private LocalDate validFrom;
  private LocalDate validTo;

  private Status status;
  private SublineType sublineType;
  private SublineConcessionType concessionType;
  private String swissSublineNumber;

  // derived from parent
  private String number;
  private String swissLineNumber;
  private String shortNumber;
  private OfferCategory offerCategory;

  private String description;
  private String longName;
  private String businessOrganisation;

}
