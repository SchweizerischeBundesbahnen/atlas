package ch.sbb.timetable.field.number.entity;

import ch.sbb.timetable.field.number.enumaration.Status;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
@Immutable
@Entity(name = "timetable_field_number")
public class TimetableFieldNumber {

  private String swissTimetableFieldNumber;

  @Id
  private String ttfnid;

  private String name;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

}
