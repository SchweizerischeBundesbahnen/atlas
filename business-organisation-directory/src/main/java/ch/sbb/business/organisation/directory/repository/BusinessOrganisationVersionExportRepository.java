package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationExportVersionWithTuInfo;
import java.time.LocalDate;
import java.util.List;

@Deprecated(forRemoval = true)
public interface BusinessOrganisationVersionExportRepository {

  List<BusinessOrganisationExportVersionWithTuInfo> findAll();

  List<BusinessOrganisationExportVersionWithTuInfo> findVersionsValidOn(LocalDate validOn);

}
