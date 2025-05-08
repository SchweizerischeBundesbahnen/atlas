package ch.sbb.workflow.sepodi.termination.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.workflow.entity.BaseWorkflowEntity;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Size;
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
@Entity(name = "termination_decision")
public class TerminationDecision extends BaseWorkflowEntity {

  private static final String VERSION_SEQ = "termination_decision_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 130000)
  private Long id;

  @Enumerated(EnumType.STRING)
  private JudgementType judgement;

  @Enumerated(EnumType.STRING)
  private TerminationDecisionPerson terminationDecisionPerson;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String motivation;

}
