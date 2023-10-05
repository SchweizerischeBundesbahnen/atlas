package ch.sbb.business.organisation.directory.repository;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationExportVersionWithTuInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BusinessOrganisationVersionExportRepositoryImpl implements BusinessOrganisationVersionExportRepository {

  private static final String ORDER_BY = " ORDER BY bov.sboid, bov.valid_from ASC";

  private final EntityManager entityManager;

  @Override
  public List<BusinessOrganisationExportVersionWithTuInfo> findAll() {
    String sqlString = buildQuery(Optional.empty());
    Query nativeQuery = entityManager.createNativeQuery(sqlString, BusinessOrganisationExportVersionWithTuInfo.class);
    return nativeQuery.getResultList();
  }

  @Override
  public List<BusinessOrganisationExportVersionWithTuInfo> findVersionsValidOn(LocalDate validOn) {
    String sqlString = buildQuery(Optional.of(validOn));
    Query nativeQuery = entityManager.createNativeQuery(sqlString, BusinessOrganisationExportVersionWithTuInfo.class);
    return nativeQuery.getResultList();
  }

  private static String buildQuery(Optional<LocalDate> validOn) {
    StringBuilder query = new StringBuilder(String.format("""
        SELECT DISTINCT bov.*, tc.number, tc.abbreviation, tc.business_register_name, tc.id as transport_company_id
        FROM business_organisation_version as bov
            left join transport_company_relation tcr on bov.sboid = tcr.sboid and
              ('%s' between tcr.valid_from and tcr.valid_to)
            left join transport_company tc on tcr.transport_company_id = tc.id
        """, DateHelper.getDateAsSqlString(validOn.orElse(LocalDate.now()))));
    validOn.ifPresent(valid -> query.append(
        String.format("WHERE '%s' between bov.valid_from and bov.valid_to", DateHelper.getDateAsSqlString(valid))));
    query.append(ORDER_BY);
    return query.toString();
  }

}
