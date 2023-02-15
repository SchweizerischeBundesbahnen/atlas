package ch.sbb.line.directory.entity;

import ch.sbb.atlas.api.lidi.enumaration.CoverageType;
import ch.sbb.atlas.api.lidi.enumaration.ModelType;
import ch.sbb.atlas.api.lidi.enumaration.ValidationErrorType;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "coverage")
public class Coverage {

  private static final String COVERAGE_SEQ = "coverage_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = COVERAGE_SEQ)
  @SequenceGenerator(name = COVERAGE_SEQ, sequenceName = COVERAGE_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  private String slnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private ModelType modelType;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @NotNull
  @Enumerated(EnumType.STRING)
  private CoverageType coverageType;

  @Enumerated(EnumType.STRING)
  private ValidationErrorType validationErrorType;

}
