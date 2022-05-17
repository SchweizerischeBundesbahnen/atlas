package ch.sbb.business.organisation.directory.controller;

import ch.sbb.business.organisation.directory.api.BusinessOrganisationApiV1;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BusinessOrganisationController implements BusinessOrganisationApiV1 {

  private final BusinessOrganisationRepository businessOrganisationRepository;

  @Override
  public List<BusinessOrganisationVersion> getBusinessOrganisations() {
    return businessOrganisationRepository.findAll();
  }

  @Override
  public BusinessOrganisationVersion createBusinessOrganisations() {
    BusinessOrganisationVersion businessOrganisationVersion =
        BusinessOrganisationVersion.builder()
                                   .organisationNumber(123)
                                   .descriptionDe("Description")
            .validFrom(LocalDate.of(2000,1,1))
            .validTo(LocalDate.of(2000,12,31))
                                   .build();
    BusinessOrganisationVersion createdVersion = businessOrganisationRepository.save(
        businessOrganisationVersion);
    return createdVersion;
  }


}
