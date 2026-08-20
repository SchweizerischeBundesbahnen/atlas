package ch.sbb.timetable.hearing.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.BatchUpdateTimetableHearingStatementsModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierQuestionModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.exception.SimpleAtlasException;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.DossierQuestion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DossierMapperTest {

  private Dossier dossier;

  @BeforeEach
  void setUp() {
    dossier = Dossier.builder()
        .id(1L)
        .swissCanton(SwissCanton.BERN)
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .boContactMail("bern@mobil.be")
        .statementIds(List.of(132L, 145L))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .dossierStatus(DossierStatus.REJECTED)
        .build();
  }

  @Test
  void shouldUpdateStatementForCancelCorrectly() {
    BatchUpdateTimetableHearingStatementsModel batchUpdateModel = DossierMapper.toBatchUpdateModel(dossier,
        DossierStatus.CANCELED);

    BatchUpdateTimetableHearingStatementsModel expected = BatchUpdateTimetableHearingStatementsModel.builder()
        .ids(dossier.getStatementIds())
        .dossierCanton(dossier.getSwissCanton())
        .topic(dossier.getTopic())
        .internalComment(dossier.getInternalComment())
        .publicComment(dossier.getPublicComment())
        .statementStatus(StatementStatus.RECEIVED)
        .dossierId(null)
        .dossierContactMail(null)
        .dossierContactSbbuid(null)
        .build();
    assertThat(batchUpdateModel).isEqualTo(expected);
  }

  @Test
  void shouldUpdateStatementForAcceptedCorrectly() {
    BatchUpdateTimetableHearingStatementsModel batchUpdateModel = DossierMapper.toBatchUpdateModel(dossier,
        DossierStatus.ACCEPTED);

    BatchUpdateTimetableHearingStatementsModel expected = BatchUpdateTimetableHearingStatementsModel.builder()
        .ids(dossier.getStatementIds())
        .dossierCanton(dossier.getSwissCanton())
        .topic(dossier.getTopic())
        .internalComment(dossier.getInternalComment())
        .publicComment(dossier.getPublicComment())
        .statementStatus(StatementStatus.ACCEPTED)
        .dossierId(dossier.getId())
        .dossierContactMail(dossier.getBoContactMail())
        .dossierContactSbbuid(dossier.getBoContactSbbuid())
        .build();
    assertThat(batchUpdateModel).isEqualTo(expected);
  }

  @Test
  void shouldUpdateStatementForRejectedCorrectly() {
    BatchUpdateTimetableHearingStatementsModel batchUpdateModel = DossierMapper.toBatchUpdateModel(dossier,
        DossierStatus.REJECTED);

    BatchUpdateTimetableHearingStatementsModel expected = BatchUpdateTimetableHearingStatementsModel.builder()
        .ids(dossier.getStatementIds())
        .dossierCanton(dossier.getSwissCanton())
        .topic(dossier.getTopic())
        .internalComment(dossier.getInternalComment())
        .publicComment(dossier.getPublicComment())
        .statementStatus(StatementStatus.REJECTED)
        .dossierId(dossier.getId())
        .dossierContactMail(dossier.getBoContactMail())
        .dossierContactSbbuid(dossier.getBoContactSbbuid())
        .build();
    assertThat(batchUpdateModel).isEqualTo(expected);
  }

  @Test
  void shouldUpdateStatementForMovedCorrectly() {
    BatchUpdateTimetableHearingStatementsModel batchUpdateModel = DossierMapper.toBatchUpdateModel(dossier,
        DossierStatus.MOVED);

    BatchUpdateTimetableHearingStatementsModel expected = BatchUpdateTimetableHearingStatementsModel.builder()
        .ids(dossier.getStatementIds())
        .dossierCanton(dossier.getSwissCanton())
        .topic(dossier.getTopic())
        .internalComment(dossier.getInternalComment())
        .publicComment(dossier.getPublicComment())
        .statementStatus(StatementStatus.MOVED)
        .dossierId(dossier.getId())
        .dossierContactMail(dossier.getBoContactMail())
        .dossierContactSbbuid(dossier.getBoContactSbbuid())
        .build();
    assertThat(batchUpdateModel).isEqualTo(expected);
  }

  @Test
  void shouldUpdateStatementForDissolvedCorrectly() {
    BatchUpdateTimetableHearingStatementsModel batchUpdateModel = DossierMapper.toBatchUpdateModel(dossier,
        DossierStatus.DISSOLVED);

    BatchUpdateTimetableHearingStatementsModel expected = BatchUpdateTimetableHearingStatementsModel.builder()
        .ids(dossier.getStatementIds())
        .dossierCanton(dossier.getSwissCanton())
        .topic(dossier.getTopic())
        .internalComment(dossier.getInternalComment())
        .publicComment(dossier.getPublicComment())
        .statementStatus(StatementStatus.REJECTED)
        .dossierId(null)
        .dossierContactMail(null)
        .dossierContactSbbuid(null)
        .build();
    assertThat(batchUpdateModel).isEqualTo(expected);
  }

  @Test
  void shouldThrowExceptionIfCompleteToDissolvedFromAdded() {
    dossier.setDossierStatus(DossierStatus.ADDED);

    assertThatExceptionOfType(SimpleAtlasException.class).isThrownBy(
        () -> DossierMapper.toBatchUpdateModel(dossier, DossierStatus.DISSOLVED));
  }

  @Test
  void shouldMapVersionToEtagVersion() {
    dossier.setVersion(3);
    dossier.setDossierQuestions(List.of(DossierQuestion.builder().id(2L).question("Warum?").version(4).build()));

    TthDossierModel model = DossierMapper.toModel(dossier);

    assertThat(model.getEtagVersion()).isEqualTo(3);
    assertThat(model.getQuestions().getFirst().getEtagVersion()).isEqualTo(4);
  }

  @Test
  void shouldMapEtagVersionToVersion() {
    TthDossierModel model = TthDossierModel.builder()
        .id(1L)
        .swissCanton(SwissCanton.BERN)
        .topic("Bern, Salem - Takt")
        .statementIds(List.of(132L))
        .etagVersion(3)
        .questions(List.of(TthDossierQuestionModel.builder().id(2L).question("Warum?").etagVersion(4).build()))
        .build();

    Dossier entity = DossierMapper.toEntity(model);

    assertThat(entity.getVersion()).isEqualTo(3);
    assertThat(entity.getDossierQuestions().getFirst().getVersion()).isEqualTo(4);
  }
}