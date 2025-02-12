package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.entity.bodi.BusinessOrganisation;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BusinessOrganisationRowMapper implements RowMapper<BusinessOrganisation> {

  // todo
  @Override
  public BusinessOrganisation mapRow(ResultSet rs, int rowNum) throws SQLException {
    return null;
  }

}
