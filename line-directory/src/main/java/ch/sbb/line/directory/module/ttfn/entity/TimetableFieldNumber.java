package ch.sbb.line.directory.module.ttfn.entity;

import ch.sbb.atlas.model.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Immutable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
@Immutable
@FieldNameConstants
@Entity(name = "timetable_field_number")
public class TimetableFieldNumber {

  @Id
  private String ttfnid;

  private String swissTimetableFieldNumber;

  private String number;

  @Column(name = "description_outward_line_1")
  private String descriptionOutwardLine1;

  @Enumerated(EnumType.STRING)
  private Status status;

  private String businessOrganisation;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

}
