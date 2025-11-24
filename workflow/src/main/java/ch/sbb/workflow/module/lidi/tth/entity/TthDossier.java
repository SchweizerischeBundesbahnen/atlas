package ch.sbb.workflow.module.lidi.tth.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.workflow.entity.BaseWorkflowEntity;
import ch.sbb.workflow.module.lidi.tth.model.DossierStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Entity(name = "tth_dossier")
public class TthDossier extends BaseWorkflowEntity {

  private static final String VERSION_SEQ = "tth_dossier_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_500)
  private String topic;

  @NotNull
  @Enumerated(EnumType.STRING)
  private DossierStatus dossierStatus;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_5000)
  private String internalComment;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_5000)
  private String publicComment;

  @NotEmpty
  @ElementCollection(targetClass = Long.class, fetch = FetchType.EAGER)
  private List<Long> statementIds;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String boContactMail;

  @NotNull
  private LocalDate boDeadlineToAnswer;

  @Builder.Default
  @OneToMany(mappedBy = "tthDossier", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<TthDossierQuestion> dossierQuestions = new ArrayList<>();
}
