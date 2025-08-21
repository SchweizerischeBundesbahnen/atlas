package ch.sbb.business.organisation.directory.module.company.crd;

import ch.sbb.business.organisation.directory.service.crd.Company;
import java.util.List;

public interface CrdClient {

  List<Company> getAllCompanies();
}
