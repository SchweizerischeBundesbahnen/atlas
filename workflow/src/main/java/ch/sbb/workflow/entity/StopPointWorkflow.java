package ch.sbb.workflow.entity;

import static java.util.stream.Collectors.toSet;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "stop_point_workflow")
public class StopPointWorkflow extends BaseWorkflowEntity {

  private static final String VERSION_SEQ = "stop_point_workflow_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  private Long versionId;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_500)
  private String sloid;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_32)
  private String sboid;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String localityName;

  @NotNull
  @Enumerated(EnumType.STRING)
  private WorkflowStatus status;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "follow_up_workflow_id", referencedColumnName = "id")
  private StopPointWorkflow followUpWorkflow;

  @OneToMany(mappedBy = "stopPointWorkflow", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Person> examinants = new HashSet<>();

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> ccEmails;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String workflowComment;

  @Size(max = AtlasFieldLengths.LENGTH_30)
  private String designationOfficial;

  @CreationTimestamp
  @Column(columnDefinition = "DATE")
  private LocalDate startDate;

  @Column(columnDefinition = "DATE")
  private LocalDate endDate;

   public void setExaminants(Set<Person> examinants) {
    this.examinants =
        examinants.stream().peek(examinant -> examinant.setStopPointWorkflow(this)).collect(toSet());
  }

}
