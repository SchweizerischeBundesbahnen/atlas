package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BoDiDbSchemaCreation;
import ch.sbb.exportservice.job.bodi.businessorganisation.model.BusinessOrganisation;
import ch.sbb.exportservice.job.bodi.businessorganisation.sql.BusinessOrganisationRowMapper;
import ch.sbb.exportservice.job.bodi.businessorganisation.sql.BusinessOrganisationSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@BoDiDbSchemaCreation
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class BusinessOrganisationSqlntegrationTest {

  @Autowired
  @Qualifier("businessOrganisationDirectoryDataSource")
  protected DataSource businessOrganisationDirectoryDataSource;

  @Test
  void shouldReturnFullBusinessOrganisations() throws Exception {
    // given
    String sqlQuery = BusinessOrganisationSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    // when
    List<BusinessOrganisation> businessOrganisations = executeQuery(sqlQuery);

    // then
    assertThat(businessOrganisations.size()).isEqualTo(1);
    assertThat(businessOrganisations.getFirst().getBusinessTypes()).containsExactly(BusinessType.STREET);
    assertThat(businessOrganisations.getFirst().getBusinessRegisterName()).isEqualTo("Alcosuisse");
  }

  @Test
  void shouldReturnActualBusinessOrganisations() throws SQLException {
    //given
    String sqlQuery = BusinessOrganisationSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<BusinessOrganisation> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableFutureBusinessOrganisations() throws SQLException {
    //given
    String sqlQuery = BusinessOrganisationSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<BusinessOrganisation> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<BusinessOrganisation> executeQuery(String sqlQuery) throws SQLException {
    List<BusinessOrganisation> result = new ArrayList<>();
    Connection connection = businessOrganisationDirectoryDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      BusinessOrganisationRowMapper businessOrganisationRowMapper = new BusinessOrganisationRowMapper();
      while (resultSet.next()) {
        BusinessOrganisation businessOrganisation = businessOrganisationRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(businessOrganisation);
      }
    }
    connection.close();
    return result;
  }

}
