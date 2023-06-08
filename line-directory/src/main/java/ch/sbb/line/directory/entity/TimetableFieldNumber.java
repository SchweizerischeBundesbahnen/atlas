package ch.sbb.line.directory.entity;

import ch.sbb.atlas.kafka.model.Status;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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

  private String description;

  @Enumerated(EnumType.STRING)
  private Status status;

  private String businessOrganisation;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

}
