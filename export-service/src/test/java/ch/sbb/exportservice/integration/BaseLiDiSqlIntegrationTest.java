package ch.sbb.exportservice.integration;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.LiDiDbSchemaCreation;
import ch.sbb.exportservice.PrmDbSchemaCreation;
import ch.sbb.exportservice.entity.lidi.Line;
import ch.sbb.exportservice.entity.lidi.Subline;
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
    final String insertSql = """
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

  protected void insertSublineVersion(Subline subline) throws SQLException {
    final String insertSql = """
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

  protected void cleanupLines() throws SQLException {
    execute("delete from line_version");
  }

  private void execute(String insertSql) throws SQLException {
    final Connection connection = lineDirectoryDataSource.getConnection();
    try (final PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

  protected String formatDate(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }
}
