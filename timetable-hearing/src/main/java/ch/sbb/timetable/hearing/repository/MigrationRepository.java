package ch.sbb.timetable.hearing.repository;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
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

  private static final List<String> TABLES = List.of(
      "timetable_hearing_year",
      "timetable_hearing_statement",
      "timetable_hearing_statement_emails",
      "timetable_hearing_statement_responsible_transport_companies",
      "statement_document");
  private static final List<String> SEQUENCES = List.of(
      "timetable_hearing_statement_seq",
      "statement_document_seq");

  private static final int PIPE_SIZE = 64_000;
  public static final int SEQUENCE_POSTFIX_LENGTH = 4;

  private final DataSource lineDirectoryDataSource;
  private final DataSource timetableHearingDataSource;

  public MigrationRepository(
      @Qualifier("lineDirectoryDataSource") DataSource lineDirectoryDataSource,
      @Qualifier("dataSource") DataSource timetableHearingDataSource) {
    this.lineDirectoryDataSource = lineDirectoryDataSource;
    this.timetableHearingDataSource = timetableHearingDataSource;
  }

  public void migrateFromLidi() {
    log.info("Starting migration from LiDi");
    truncateTablesInTimetableHearing();
    log.info("Tables in timetable-hearing truncated");

    copyAllTables();
    log.info("Copied data from LiDi to timetable-hearing");

    setSequencesCorrectly();
    log.info("Updated sequences for timetable-hearing");

    log.info("Migration completed.");
  }

  @SneakyThrows
  private void truncateTablesInTimetableHearing() {
    try (Connection connection = timetableHearingDataSource.getConnection();
        Statement statement = connection.createStatement()) {

      TABLES.forEach(table -> {
        try {
          statement.execute("TRUNCATE TABLE " + table + " CASCADE");
        } catch (SQLException e) {
          throw new IllegalStateException(e);
        }
        log.info("Truncated table '{}'", table);
      });
    }
  }

  @SneakyThrows
  private void copyAllTables() {
    log.info("Copying {} table(s) from lidi to timetable-hearing", TABLES.size());

    try (Connection source = lineDirectoryDataSource.getConnection();
        Connection target = timetableHearingDataSource.getConnection()) {

      CopyManager sourceCopy = source.unwrap(PGConnection.class).getCopyAPI();
      CopyManager targetCopy = target.unwrap(PGConnection.class).getCopyAPI();

      TABLES.forEach(table -> copyTable(sourceCopy, targetCopy, table));
    }
  }

  @SneakyThrows
  private static void copyTable(CopyManager sourceCopy, CopyManager targetCopy, String table) {
    try (PipedInputStream in = new PipedInputStream(PIPE_SIZE);
        PipedOutputStream out = new PipedOutputStream(in)) {

      Thread exporter = new Thread(() -> export(sourceCopy, table, out), "copy-out-" + table);
      exporter.start();

      long rows = targetCopy.copyIn("COPY " + table + " FROM STDIN (FORMAT BINARY)", in);
      exporter.join();

      log.info("Copied {} row(s) of table '{}'", rows, table);
    }
  }

  @SneakyThrows
  private static void export(CopyManager sourceCopy, String table, PipedOutputStream out) {
    try (out) {
      sourceCopy.copyOut("COPY " + table + " TO STDOUT (FORMAT BINARY)", out);
    }
  }

  @SneakyThrows
  private void setSequencesCorrectly() {
    try (Connection connection = timetableHearingDataSource.getConnection();
        Statement statement = connection.createStatement()) {

      SEQUENCES.forEach(sequence -> {
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
}
