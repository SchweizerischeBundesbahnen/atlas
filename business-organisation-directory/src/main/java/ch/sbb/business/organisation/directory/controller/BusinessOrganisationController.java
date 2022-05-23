package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.model.Status;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationApiV1;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessType;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    Set<BusinessType> businessTypes = new HashSet<>();
    businessTypes.add(BusinessType.AIR);
    businessTypes.add(BusinessType.FAIR);
    BusinessOrganisationVersion businessOrganisationVersion =
        BusinessOrganisationVersion.builder()
                                   .organisationNumber(123)
                                   .descriptionDe("Description")
            .abbreviationDe("de")
            .abbreviationFr("fr")
            .abbreviationIt("it")
            .abbreviationEn("en")
            .descriptionDe("DescDe")
            .descriptionFr("DescFr")
            .descriptionIt("DescIt")
            .descriptionEn("DescEn")
            .status(Status.ACTIVE)
            .businessTypes(businessTypes)
            .validFrom(LocalDate.of(2000,1,1))
            .validTo(LocalDate.of(2000,12,31))
                                   .build();
    BusinessOrganisationVersion createdVersion = businessOrganisationRepository.save(
        businessOrganisationVersion);
    return createdVersion;
  }


}
