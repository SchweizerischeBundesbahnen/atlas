package ch.sbb.business.organisation.directory.module.company.repository;

import ch.sbb.business.organisation.directory.module.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String>,
    JpaSpecificationExecutor<Company> {

}
