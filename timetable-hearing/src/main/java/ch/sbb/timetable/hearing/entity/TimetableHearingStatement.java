package ch.sbb.timetable.hearing.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BoContactAssociated;
import ch.sbb.atlas.api.model.CantonAssociated;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.StatementDossierLinked;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.timetable.hearing.shared.transportcompany.entity.SharedTransportCompany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity(name = "timetable_hearing_statement")
@Redacted
public class TimetableHearingStatement extends BaseEntity implements CantonAssociated, StatementDossierLinked,
    BoContactAssociated {

  private static final String VERSION_SEQ = "timetable_hearing_statement_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  private Long timetableYear;

  @NotNull
  @Enumerated(EnumType.STRING)
  private StatementStatus statementStatus;

  private String ttfnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SwissCanton swissCanton;

  @Enumerated(EnumType.STRING)
  private SwissCanton oldSwissCanton;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String stopPlace;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "timetable_hearing_statement_responsible_transport_companies",
      joinColumns = {@JoinColumn(name = "timetable_hearing_statement_id")},
      inverseJoinColumns = {@JoinColumn(name = "transport_company_id")})
  private Set<SharedTransportCompany> responsibleTransportCompanies;

  private String responsibleTransportCompaniesDisplay;

  @Valid
  @Redacted
  private StatementSender statementSender;

  // Statement made by citizen
  @Redacted
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String statement;

  private Boolean statementAnonymous;

  // Statement anonymized by canton
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String anonymousStatement;

  @ToString.Exclude
  @Size(max = TimetableHearingConstants.MAX_DOCUMENTS)
  @OneToMany(mappedBy = "statement", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<StatementDocument> documents;

  // FoT Justification field for comments
  @Redacted
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String publicComment;

  // Canton internal comment
  @Redacted
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String internalComment;

  @Redacted
  @Size(max = AtlasFieldLengths.LENGTH_280)
  private String cantonTransferComment;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String topic;

  private Long dossierId;

  private String dossierContactMail;

  private String dossierContactSbbuid;

  private boolean dataProtectionChecked;

  public void removeDocument(String documentFilename) {
    Optional<StatementDocument> optionalStatementDocument = documents.stream()
        .filter(doc -> Objects.equals(documentFilename, doc.getFileName())).findFirst();
    optionalStatementDocument.ifPresent(documents::remove);
  }

  public void addDocument(StatementDocument statementDocument) {
    statementDocument.setStatement(this);
    documents.add(statementDocument);
  }

  public boolean checkIfStatementDocumentExists(String documentFilename) {
    return documents.stream().anyMatch(document -> Objects.equals(documentFilename, document.getFileName()));
  }

  public String getTransportCompaniesCommaSeparated() {
    List<String> sorted = getResponsibleTransportCompanies()
        .stream()
        .map(SharedTransportCompany::getAbbreviation)
        .filter(Objects::nonNull)
        .sorted()
        .toList();
    return String.join(", ", sorted);
  }

  public boolean isPartOfDossier() {
    return dossierId != null;
  }

  @Override
  public String getBoContactMail() {
    return dossierContactMail;
  }

  @Override
  public String getBoContactSbbuid() {
    return dossierContactSbbuid;
  }
}
