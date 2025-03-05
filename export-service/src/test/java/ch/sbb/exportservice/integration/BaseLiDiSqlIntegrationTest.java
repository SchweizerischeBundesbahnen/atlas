package ch.sbb.exportservice.integration;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.LiDiDbSchemaCreation;
import ch.sbb.exportservice.PrmDbSchemaCreation;
import ch.sbb.exportservice.entity.lidi.Line;
import ch.sbb.exportservice.entity.lidi.Subline;
import ch.sbb.exportservice.entity.lidi.TimetableFieldNumber;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@LiDiDbSchemaCreation
@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
abstract class BaseLiDiSqlIntegrationTest {

  @Autowired
  @Qualifier("lineDirectoryDataSource")
  protected DataSource lineDirectoryDataSource;

  protected void insertLineVersion(Line line) throws SQLException {
    String insertSql = """
        INSERT INTO line_version (id, slnid, valid_from, valid_to, status, line_type, concession_type,
        swiss_line_number, description, long_name, number, short_number, offer_category, business_organisation, comment,
        creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', '%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',
        '2022-02-19 09:54:38.000000', 'u123456', '2022-02-19 09:54:38.000000', 'u123456', 0);
        """
        .formatted(line.getId(), line.getSlnid(), formatDate(line.getValidFrom()), formatDate(line.getValidTo()),
            line.getStatus(), line.getLineType(), line.getConcessionType(),
            line.getSwissLineNumber(), line.getDescription(), line.getLongName(), line.getNumber(), line.getShortNumber(),
            line.getOfferCategory(), line.getBusinessOrganisation(), line.getComment());
    execute(insertSql);
  }

  protected void cleanupLines() throws SQLException {
    execute("delete from line_version");
  }

  protected void insertSublineVersion(Subline subline) throws SQLException {
    String insertSql = """
        INSERT INTO subline_version (id, slnid, mainline_slnid, valid_from, valid_to, status, subline_type, concession_type,
        swiss_subline_number, description, long_name, business_organisation,
        creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', '%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',
        '2022-02-19 09:54:38.000000', 'u123456', '2022-02-19 09:54:38.000000', 'u123456', 0);
        """
        .formatted(subline.getId(), subline.getSlnid(), subline.getMainlineSlnid(),
            formatDate(subline.getValidFrom()), formatDate(subline.getValidTo()),
            subline.getStatus(), subline.getSublineType(), subline.getConcessionType(),
            subline.getSwissSublineNumber(), subline.getDescription(), subline.getLongName(), subline.getBusinessOrganisation());
    execute(insertSql);
  }

  protected void insertTtfnVersion(TimetableFieldNumber timetableFieldNumber) throws SQLException {
    String insertSql = """
        INSERT INTO timetable_field_number_version (id, ttfnid, valid_from, valid_to, status, swiss_timetable_field_number,
        number, business_organisation, description, comment,
        creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', '%s','%s','%s','%s','%s','%s','%s','%s',
        '2022-02-19 09:54:38.000000', 'u123456', '2022-02-19 09:54:38.000000', 'u123456', 0);
        """
        .formatted(timetableFieldNumber.getId(), timetableFieldNumber.getTtfnid(),
            formatDate(timetableFieldNumber.getValidFrom()), formatDate(timetableFieldNumber.getValidTo()),
            timetableFieldNumber.getStatus(), timetableFieldNumber.getSwissTimetableFieldNumber(),
            timetableFieldNumber.getNumber(),
            timetableFieldNumber.getBusinessOrganisation(), timetableFieldNumber.getDescription(), timetableFieldNumber.getComment());
    execute(insertSql);
  }

  private void execute(String insertSql) throws SQLException {
    Connection connection = lineDirectoryDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

  protected String formatDate(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }
}
