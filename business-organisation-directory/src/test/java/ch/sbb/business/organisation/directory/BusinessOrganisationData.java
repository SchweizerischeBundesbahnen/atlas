package ch.sbb.business.organisation.directory;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionVersionModel;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionVersionModel.BusinessOrganisationVersionVersionModelBuilder;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessType;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BusinessOrganisationData {

  public static BusinessOrganisationVersion businessOrganisationVersion() {
    return businessOrganisationVersionBuilder().build();
  }

  public static BusinessOrganisationVersion.BusinessOrganisationVersionBuilder<?, ?> businessOrganisationVersionBuilder() {
    return BusinessOrganisationVersion.builder()
        .sboid("ch:1:sboid:1000000")
        .abbreviationDe("de")
        .abbreviationFr("fr")
        .abbreviationIt("it")
        .abbreviationEn("en")
        .descriptionDe("desc-de")
        .descriptionFr("desc-fr")
        .descriptionIt("desc-it")
        .descriptionEn("desc-en")
        .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
        .contactEnterpriseEmail("mail@mail.ch")
        .organisationNumber(123)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31));
  }

  public static BusinessOrganisationVersionVersionModel businessOrganisationVersionModel() {
    return businessOrganisationVersionModelBuilder().build();
  }

  public static BusinessOrganisationVersionVersionModelBuilder businessOrganisationVersionModelBuilder() {
    return BusinessOrganisationVersionVersionModel.builder()
        .abbreviationDe("de")
        .abbreviationFr("fr")
        .abbreviationIt("it")
        .abbreviationEn("en")
        .descriptionDe("desc-de")
        .descriptionFr("desc-fr")
        .descriptionIt("desc-it")
        .descriptionEn("desc-en")
        .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
        .contactEnterpriseEmail("mail@mail.ch")
        .organisationNumber(123)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31));
  }

}
