package ch.sbb.exportservice.entity.lidi;

import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.entity.BaseEntity;
import java.time.LocalDate;
import java.util.Set;
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
public class TimetableFieldNumber extends BaseEntity {

  private String ttfnid;
  private LocalDate validFrom;
  private LocalDate validTo;
  private Status status;
  private String swissTimetableFieldNumber;
  private String number;
  private String businessOrganisation;
  private String description;
  private String comment;
  private Set<String> lineRelations;

}
