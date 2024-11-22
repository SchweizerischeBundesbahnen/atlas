package ch.sbb.workflow.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@Entity(name = "line_workflow")
@Redacted
public class LineWorkflow {

  private static final String VERSION_SEQ = "line_workflow_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  private Long businessObjectId;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_500)
  private String swissId;

  @NotNull
  @Enumerated(EnumType.STRING)
  private WorkflowType workflowType;

  @Size(max = AtlasFieldLengths.LENGTH_500)
  private String description;

  @NotNull
  @Enumerated(EnumType.STRING)
  private WorkflowStatus status;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String workflowComment;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String checkComment;

  @Redacted
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "client_id", referencedColumnName = "id")
  private Person client;

  @Redacted
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "examinant_id", referencedColumnName = "id")
  private Person examinant;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

}
