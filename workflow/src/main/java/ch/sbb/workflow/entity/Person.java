package ch.sbb.workflow.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
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
@SuperBuilder
@FieldNameConstants
@Entity(name = "person")
public class Person extends BaseWorkflowEntity {

  private static final String VERSION_SEQ = "person_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String firstName;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String lastName;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String function;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String mail;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stop_point_workflow_id")
  private StopPointWorkflow stopPointWorkflow;

}
