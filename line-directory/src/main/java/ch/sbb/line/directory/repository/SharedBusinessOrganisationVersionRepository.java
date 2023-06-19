package ch.sbb.line.directory.repository;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.SharedBusinessOrganisationVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedBusinessOrganisationVersionRepository extends JpaRepository<SharedBusinessOrganisationVersion, Long>,
    BusinessOrganisationVersionSharingDataAccessor {

  boolean existsBySboid(String sboid);

  default void save(SharedBusinessOrganisationVersionModel model) {
    save(SharedBusinessOrganisationVersion.builder()
        .id(model.getId())
        .sboid(model.getSboid())
        .abbreviationDe(model.getAbbreviationDe())
        .abbreviationFr(model.getAbbreviationFr())
        .abbreviationIt(model.getAbbreviationIt())
        .abbreviationEn(model.getAbbreviationEn())
        .descriptionDe(model.getDescriptionDe())
        .descriptionFr(model.getDescriptionFr())
        .descriptionIt(model.getDescriptionIt())
        .descriptionEn(model.getDescriptionEn())
        .organisationNumber(model.getOrganisationNumber())
        .status(Status.valueOf(model.getStatus()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .build());
  }
}
