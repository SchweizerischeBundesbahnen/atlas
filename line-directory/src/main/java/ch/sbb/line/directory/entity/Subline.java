package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

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

  @Size(max = 50)
  private String number;

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
