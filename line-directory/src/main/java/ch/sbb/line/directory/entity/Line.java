package ch.sbb.line.directory.entity;

import ch.sbb.atlas.api.lidi.enumaration.LidiElementType;
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
@Entity(name = "overview_line_subline")
public class Line {

  private String swissLineNumber;

  private String number;

  private String description;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private LidiElementType lidiElementType;

  private String businessOrganisation;

  @Id
  private String slnid;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

}
