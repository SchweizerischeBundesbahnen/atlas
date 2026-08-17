package ch.sbb.timetable.hearing.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.DossierQuestion;
import ch.sbb.timetable.hearing.entity.StatementDocument;
import ch.sbb.timetable.hearing.entity.StatementSender;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;

@IntegrationTest
@Sql(scripts = "/lidi-hearing-schema.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(dataSource = "lineDirectoryDataSource", transactionManager = "lineDirectoryTransactionManager",
        transactionMode = TransactionMode.ISOLATED))
@Sql(scripts = "/lidi-hearing-drop.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(dataSource = "lineDirectoryDataSource", transactionManager = "lineDirectoryTransactionManager",
        transactionMode = TransactionMode.ISOLATED))
@Sql(scripts = "/workflow-dossier-schema.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(dataSource = "workflowDataSource", transactionManager = "workflowTransactionManager",
        transactionMode = TransactionMode.ISOLATED))
@Sql(scripts = "/workflow-dossier-drop.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(dataSource = "workflowDataSource", transactionManager = "workflowTransactionManager",
        transactionMode = TransactionMode.ISOLATED))
class MigrationRepositoryTest {

  private final MigrationRepository migrationRepository;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final DossierRepository dossierRepository;

  @Autowired
  MigrationRepositoryTest(MigrationRepository migrationRepository,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      TimetableHearingYearRepository timetableHearingYearRepository,
      DossierRepository dossierRepository) {
    this.migrationRepository = migrationRepository;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.dossierRepository = dossierRepository;
  }

  @AfterEach
  void tearDown() {
    dossierRepository.deleteAll();
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldMigrateAllTablesFromLidi() {
    migrationRepository.migrateFromLidi();

    assertThat(timetableHearingYearRepository.findAll())
        .extracting(TimetableHearingYear::getTimetableYear)
        .containsExactly(2024L);

    List<TimetableHearingStatement> statements = timetableHearingStatementRepository.findAll();
    assertThat(statements)
        .extracting(TimetableHearingStatement::getId)
        .containsExactlyInAnyOrder(100L, 101L);
  }

  @Test
  void shouldMigrateNestedDataOfStatements() {
    migrationRepository.migrateFromLidi();

    TimetableHearingStatement statement = timetableHearingStatementRepository.findById(100L).orElseThrow();

    assertThat(statement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
    assertThat(statement.getSwissCanton()).isEqualTo(SwissCanton.BERN);
    assertThat(statement.getStatementSender().getEmails()).containsExactly("mike@thebike.com");
    assertThat(statement.getDocuments())
        .extracting(StatementDocument::getFileName)
        .containsExactlyInAnyOrder("document-1.pdf", "document-2.pdf");
  }

  @Test
  void shouldMigrateDossiersFromWorkflowAfterLidiStatements() {
    migrationRepository.migrateFromLidi();

    Dossier dossier = dossierRepository.findById(300L).orElseThrow();
    assertThat(dossier.getTopic()).isEqualTo("Bern, Salem - Takt");
    assertThat(dossier.getDossierStatus()).isEqualTo(DossierStatus.ADDED);
    assertThat(dossier.getSwissCanton()).isEqualTo(SwissCanton.BERN);
    assertThat(dossier.getStatementIds()).containsExactlyInAnyOrder(100L, 101L);
    assertThat(dossier.getTthDossierYear().getTimetableYear()).isEqualTo(2024L);
    assertThat(dossier.getDossierQuestions())
        .extracting(DossierQuestion::getQuestion)
        .containsExactly("Kann der Takt erhoeht werden?");
  }

  @Test
  void shouldReplaceExistingDataInTimetableHearing() {
    timetableHearingStatementRepository.save(TimetableHearingStatement.builder()
        .timetableYear(1999L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder().emails(List.of("old@data.com")).build())
        .statement("Alter Datensatz der ueberschrieben werden soll")
        .build());

    migrationRepository.migrateFromLidi();

    List<TimetableHearingStatement> statements = timetableHearingStatementRepository.findAll();
    assertThat(statements)
        .extracting(TimetableHearingStatement::getId)
        .containsExactlyInAnyOrder(100L, 101L);
  }

  @Test
  void shouldAdjustSequencesAfterMigrationSoNewIdsDoNotClash() {
    migrationRepository.migrateFromLidi();

    TimetableHearingStatement newStatement = timetableHearingStatementRepository.save(
        TimetableHearingStatement.builder()
            .timetableYear(2024L)
            .swissCanton(SwissCanton.BERN)
            .statementStatus(StatementStatus.RECEIVED)
            .statementSender(StatementSender.builder().emails(List.of("new@data.com")).build())
            .statement("Neuer Datensatz nach der Migration")
            .build());

    // Highest migrated id is 101, so the sequence must continue above it.
    assertThat(newStatement.getId()).isGreaterThan(101L);
  }
}






