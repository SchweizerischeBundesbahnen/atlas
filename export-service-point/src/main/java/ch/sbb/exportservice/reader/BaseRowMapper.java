package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.entity.BusinessOrganisation;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseRowMapper {

  protected BusinessOrganisation getBusinessOrganisation(ResultSet rs) throws SQLException {
    return BusinessOrganisation.builder()
        .businessOrganisation(rs.getString("business_organisation"))
        .businessOrganisationNumber(RowMapperUtil.getInteger(rs, "organisation_number"))
        .businessOrganisationAbbreviationDe(rs.getString("abbreviation_de"))
        .businessOrganisationAbbreviationFr(rs.getString("abbreviation_fr"))
        .businessOrganisationAbbreviationEn(rs.getString("abbreviation_en"))
        .businessOrganisationAbbreviationIt(rs.getString("abbreviation_it"))
        .businessOrganisationDescriptionDe(rs.getString("description_de"))
        .businessOrganisationDescriptionFr(rs.getString("description_fr"))
        .businessOrganisationDescriptionEn(rs.getString("description_en"))
        .businessOrganisationDescriptionIt(rs.getString("description_it"))
        .build();
  }

}
