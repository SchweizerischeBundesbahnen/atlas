package ch.sbb.timetable.hearing.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BoContactAssociated;
import ch.sbb.atlas.api.model.CantonAssociated;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.StatementDossierLinked;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.redact.Redacted;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@Entity(name = "tth_dossier")
@Redacted
public class TthDossier extends BaseWorkflowEntity implements StatementDossierLinked, CantonAssociated, BoContactAssociated {

  private static final String VERSION_SEQ = "tth_dossier_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SwissCanton swissCanton;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String topic;

  @NotNull
  @Enumerated(EnumType.STRING)
  private DossierStatus dossierStatus;

  @Size(max = AtlasFieldLengths.LENGTH_5000)
  @Redacted
  private String internalComment;

  @Size(max = AtlasFieldLengths.LENGTH_5000)
  @Redacted
  private String publicComment;

  @NotEmpty
  @ElementCollection(targetClass = Long.class, fetch = FetchType.EAGER)
  private List<Long> statementIds;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String boContactMail;

  private String boContactSbbuid;

  private LocalDate boDeadlineToAnswer;

  @ManyToOne
  @NotNull
  @JoinColumn(name = "timetable_year")
  private TthDossierYear tthDossierYear;

  @Builder.Default
  @OneToMany(mappedBy = "tthDossier", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<TthDossierQuestion> dossierQuestions = new ArrayList<>();

}
