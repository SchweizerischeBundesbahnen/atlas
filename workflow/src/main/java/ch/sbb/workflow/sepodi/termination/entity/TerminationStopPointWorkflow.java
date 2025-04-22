package ch.sbb.workflow.sepodi.termination.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.redact.RedactBySboid;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.workflow.entity.BaseWorkflowEntity;
import ch.sbb.workflow.entity.Person;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Entity(name = "termination_stop_point_workflow")
@Redacted
public class TerminationStopPointWorkflow extends BaseWorkflowEntity {

  private static final String VERSION_SEQ = "termination_stop_point_workflow_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  private Long versionId;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_500)
  private String sloid;

  @RedactBySboid(application = ApplicationType.SEPODI)
  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_32)
  private String sboid;

  @Redacted(showFirstChar = true)
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String applicantMail;

  @NotNull
  @Enumerated(EnumType.STRING)
  private TerminationWorkflowStatus status;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime boTerminationDate;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime infoPlusTerminationDate;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime novaTerminationDate;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "info_plus_person_id", referencedColumnName = "id")
  private Person infoPlusExaminant;

  @JoinColumn(name = "nova_person_id", referencedColumnName = "id")
  @OneToOne(cascade = CascadeType.ALL)
  private Person novaExaminant;

}
