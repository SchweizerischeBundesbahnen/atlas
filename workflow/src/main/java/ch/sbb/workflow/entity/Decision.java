package ch.sbb.workflow.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.user.administration.security.redact.Redacted;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@Entity(name = "decision")
@Redacted
public class Decision extends BaseWorkflowEntity {

  private static final String VERSION_SEQ = "decision_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 130000)
  private Long id;

  @Enumerated(EnumType.STRING)
  private DecisionType decisionType;

  @Enumerated(EnumType.STRING)
  private JudgementType judgement;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String motivation;

  @Redacted
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "examinant_id", referencedColumnName = "id")
  private Person examinant;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime motivationDate;

  @Enumerated(EnumType.STRING)
  private JudgementType fotJudgement;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String fotMotivation;

  @Redacted
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "fot_overrider_id", referencedColumnName = "id")
  private Person fotOverrider;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime fotMotivationDate;

  public JudgementType getWeightedJudgement() {
    if (fotJudgement == null) {
      return judgement;
    } else {
      return fotJudgement;
    }
  }

  public boolean hasWeightedJudgementTypeNo() {
    return JudgementType.NO.equals(getWeightedJudgement());
  }

}
