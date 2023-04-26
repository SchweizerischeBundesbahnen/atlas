package ch.sbb.line.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.CantonAssociated;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity(name = "timetable_hearing_statement")
public class TimetableHearingStatement extends BaseEntity implements CantonAssociated {

  private static final String VERSION_SEQ = "timetable_hearing_statement_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  // Information regarding subject
  @NotNull
  private Long timetableYear;

  @NotNull
  @Enumerated(EnumType.STRING)
  private StatementStatus statementStatus;

  private String ttfnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SwissCanton swissCanton;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String stopPlace;

  @OneToMany(mappedBy = "statement", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ResponsibleTransportCompany> responsibleTransportCompanies;

  private String responsibleTransportCompaniesDisplay;

  @Valid
  private StatementSender statementSender;

  // Statement
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String statement;

  @ToString.Exclude
  @Size(max = TimetableHearingConstants.MAX_DOCUMENTS)
  @OneToMany(mappedBy = "statement", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<StatementDocument> documents;

  // FoT Justification field for comments
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String justification;

  @Size(max = AtlasFieldLengths.LENGTH_280)
  private String comment;

  public void removeDocument(String documentFilename) {
    Optional<StatementDocument> optionalStatementDocument = documents.stream().filter(doc -> Objects.equals(documentFilename, doc.getFileName())).findFirst();
    optionalStatementDocument.ifPresent(documents::remove);
  }

  public void addDocument(StatementDocument statementDocument) {
    statementDocument.setStatement(this);
    documents.add(statementDocument);
  }

  public boolean checkIfStatementDocumentExists(String documentFilename) {
    return documents.stream().anyMatch(document -> Objects.equals(documentFilename, document.getFileName()));
  }

}
