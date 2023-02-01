package ch.sbb.line.directory.entity;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
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
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Immutable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
@Immutable
@FieldNameConstants
@Entity(name = "subline")
public class Subline {

  private String swissSublineNumber;

  private String number;

  private String description;

  private String swissLineNumber;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private SublineType sublineType;

  private String businessOrganisation;

  @Id
  private String slnid;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

}
