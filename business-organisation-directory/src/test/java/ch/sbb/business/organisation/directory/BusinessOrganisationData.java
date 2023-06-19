package ch.sbb.business.organisation.directory;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.BusinessOrganisationVersionModelBuilder;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
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

  public static BusinessOrganisationVersionModel businessOrganisationVersionModel() {
    return businessOrganisationVersionModelBuilder().build();
  }

  public static BusinessOrganisationVersionModelBuilder<?, ?> businessOrganisationVersionModelBuilder() {
    return BusinessOrganisationVersionModel.builder()
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
