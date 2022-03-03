package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Entity(name = "subline")
public class Subline {

  @NotBlank
  @Size(max = 50)
  private String swissSublineNumber;

  @Size(max = 255)
  private String description;

  @NotBlank
  @Size(max = 50)
  private String swissLineNumber;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SublineType type;

  @NotBlank
  @Size(max = 50)
  private String businessOrganisation;

  @Id
  @NotBlank
  private String slnid;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

}
