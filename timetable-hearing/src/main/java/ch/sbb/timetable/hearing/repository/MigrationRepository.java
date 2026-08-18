package ch.sbb.timetable.hearing.repository;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MigrationRepository {

  private static final List<TableMigration> LIDI_TABLES = Stream.of(
          "timetable_hearing_year",
          "timetable_hearing_statement",
          "timetable_hearing_statement_emails",
          "timetable_hearing_statement_responsible_transport_companies",
          "statement_document")
      .map(table -> new TableMigration(table, table))
      .toList();
  private static final List<String> LIDI_SEQUENCES = List.of(
      "timetable_hearing_statement_seq",
      "statement_document_seq");

  private static final List<TableMigration> WORKFLOW_TABLES = List.of(
      new TableMigration("tth_dossier", "dossier"),
      new TableMigration("tth_dossier_statement_ids", "dossier_statement_ids"),
      new TableMigration("tth_dossier_question", "dossier_question"));
  private static final List<String> WORKFLOW_SEQUENCES = List.of(
      "dossier_seq",
      "dossier_question_seq");

  private static final int PIPE_SIZE = 64_000;
  public static final int SEQUENCE_POSTFIX_LENGTH = 4;

  private final DataSource lineDirectoryDataSource;
  private final DataSource workflowDataSource;
  private final DataSource timetableHearingDataSource;

  public MigrationRepository(
      @Qualifier("lineDirectoryDataSource") DataSource lineDirectoryDataSource,
      @Qualifier("workflowDataSource") DataSource workflowDataSource,
      @Qualifier("dataSource") DataSource timetableHearingDataSource) {
    this.lineDirectoryDataSource = lineDirectoryDataSource;
    this.workflowDataSource = workflowDataSource;
    this.timetableHearingDataSource = timetableHearingDataSource;
  }

  public void migrateFromLidi() {
    log.info("Starting migration from LiDi and Workflow");
    truncateTablesInTimetableHearing();
    log.info("Tables in timetable-hearing truncated");

    copyAllTables(lineDirectoryDataSource, LIDI_TABLES, "LiDi");
    copyAllTables(workflowDataSource, WORKFLOW_TABLES, "Workflow");
    log.info("Copied data from LiDi and Workflow to timetable-hearing");

    setSequencesCorrectly(LIDI_SEQUENCES);
    setSequencesCorrectly(WORKFLOW_SEQUENCES);
    log.info("Updated sequences for timetable-hearing");

    log.info("Migration completed.");
  }

  @SneakyThrows
  private void truncateTablesInTimetableHearing() {
    try (Connection connection = timetableHearingDataSource.getConnection();
        Statement statement = connection.createStatement()) {

      Stream.concat(WORKFLOW_TABLES.stream(), LIDI_TABLES.stream()).forEach(table -> {
        try {
          statement.execute("TRUNCATE TABLE " + table.target() + " CASCADE");
        } catch (SQLException e) {
          throw new IllegalStateException(e);
        }
        log.info("Truncated table '{}'", table.target());
      });
    }
  }

  @SneakyThrows
  private void copyAllTables(DataSource sourceDataSource, List<TableMigration> tables, String sourceName) {
    log.info("Copying {} table(s) from {} to timetable-hearing", tables.size(), sourceName);

    try (Connection source = sourceDataSource.getConnection();
        Connection target = timetableHearingDataSource.getConnection()) {

      CopyManager sourceCopy = source.unwrap(PGConnection.class).getCopyAPI();
      CopyManager targetCopy = target.unwrap(PGConnection.class).getCopyAPI();

      tables.forEach(table -> copyTable(sourceCopy, targetCopy, table));
    }
  }

  @SneakyThrows
  private static void copyTable(CopyManager sourceCopy, CopyManager targetCopy, TableMigration table) {
    try (PipedInputStream in = new PipedInputStream(PIPE_SIZE);
        PipedOutputStream out = new PipedOutputStream(in)) {

      Thread exporter = new Thread(() -> export(sourceCopy, table.source(), out), "copy-out-" + table.source());
      exporter.start();

      long rows = targetCopy.copyIn("COPY " + table.target() + " FROM STDIN (FORMAT BINARY)", in);
      exporter.join();

      log.info("Copied {} row(s) of table '{}' to '{}'", rows, table.source(), table.target());
    }
  }

  @SneakyThrows
  private static void export(CopyManager sourceCopy, String table, PipedOutputStream out) {
    try (out) {
      sourceCopy.copyOut("COPY " + table + " TO STDOUT (FORMAT BINARY)", out);
    }
  }

  @SneakyThrows
  private void setSequencesCorrectly(List<String> sequences) {
    try (Connection connection = timetableHearingDataSource.getConnection();
        Statement statement = connection.createStatement()) {

      sequences.forEach(sequence -> {
        try {
          String tableOfSequence = getTableOfSequence(sequence);
          statement.execute("select setval('" + sequence + "', (select max(id) from " + tableOfSequence + "), true)");
        } catch (SQLException e) {
          throw new IllegalStateException(e);
        }
      });
    }

  }

  private String getTableOfSequence(String sequence) {
    if (sequence.endsWith("_seq")) {
      return sequence.substring(0, sequence.length() - SEQUENCE_POSTFIX_LENGTH);
    }
    throw new IllegalArgumentException(sequence);
  }

  private record TableMigration(String source, String target) {

  }
}
