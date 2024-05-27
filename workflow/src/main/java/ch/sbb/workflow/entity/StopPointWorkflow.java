package ch.sbb.workflow.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.service.UserService;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@SuperBuilder
@FieldNameConstants
@Entity(name = "stop_point_workflow")
public class StopPointWorkflow {

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
  private String swissMunicipalityName;

  @NotNull
  @Enumerated(EnumType.STRING)
  private WorkflowStatus status;

  //From predefined list
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "examinant_bav_id", referencedColumnName = "id")
  private Person examinantBav;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "follow_up_workflow_id", referencedColumnName = "id")
  private StopPointWorkflow followUpWorkflow;

  @Builder.Default
  @OneToMany(mappedBy = "stopPointWorkflow", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Person> examinants = new HashSet<>();

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> ccEmails;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String closingComment;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String cancelComment;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String workflowComment;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String fotComment;

  @Size(max = AtlasFieldLengths.LENGTH_30)
  private String designationOfficial;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate startDate;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate endDate;

  @Column(updatable = false)
  private String creator;

  private String editor;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

  @PrePersist
  public void onPrePersist() {
    String sbbUid = UserService.getUserIdentifier();
    setCreator(sbbUid);
    setEditor(sbbUid);
  }

  @PreUpdate
  public void onPreUpdate() {
    setEditor(UserService.getUserIdentifier());
  }

}
