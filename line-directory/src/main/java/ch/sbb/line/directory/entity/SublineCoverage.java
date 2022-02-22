package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.enumaration.ModelType;
import ch.sbb.line.directory.enumaration.SublineCoverageType;
import ch.sbb.line.directory.enumaration.ValidationErrorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
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
@Entity(name = "subline_coverage")
public class SublineCoverage {

  private static final String SUBLINE_COVERAGE_SEQ = "subline_coverage_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SUBLINE_COVERAGE_SEQ)
  @SequenceGenerator(name = SUBLINE_COVERAGE_SEQ, sequenceName = SUBLINE_COVERAGE_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  private String slnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private ModelType modelType;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SublineCoverageType sublineCoverageType;

  @Enumerated(EnumType.STRING)
  private ValidationErrorType validationErrorType;

}
