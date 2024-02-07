package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.mapper.ContactPointVersionMapper;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import ch.sbb.prm.directory.search.ContactPointSearchRestrictions;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional
public class ContactPointService extends PrmRelatableVersionableService<ContactPointVersion> {

  private final ContactPointRepository contactPointRepository;

  public ContactPointService(ContactPointRepository contactPointRepository, StopPointService stopPointService,
      RelationService relationRepository, ReferencePointRepository referencePointRepository,
      VersionableService versionableService, PrmLocationService locationService) {
    super(versionableService, stopPointService, relationRepository, referencePointRepository, locationService);
    this.contactPointRepository = contactPointRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return ReferencePointElementType.CONTACT_POINT;
  }

  @Override
  protected SloidType getSloidType() {
    return SloidType.CONTACT_POINT;
  }

  @Override
  protected void incrementVersion(String sloid) {
    contactPointRepository.incrementVersion(sloid);
  }

  @Override
  protected ContactPointVersion save(ContactPointVersion version) {
    return contactPointRepository.saveAndFlush(version);
  }

  @Override
  public List<ContactPointVersion> getAllVersions(String sloid) {
    return contactPointRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(ContactPointVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(contactPointRepository));
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#version)")
  public ContactPointVersion createContactPoint(ContactPointVersion version) {
    createRelationWithSloidAllocation(version);
    return save(version);
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public ContactPointVersion updateContactPointVersion(ContactPointVersion currentVersion,
      ContactPointVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<ContactPointVersion> getContactPointVersionById(Long id) {
    return contactPointRepository.findById(id);
  }

  public Page<ContactPointVersion> findAll(ContactPointSearchRestrictions searchRestrictions) {
    return contactPointRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<ItemImportResult> importContactPoints(List<ContactPointCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();

    for (ContactPointCsvModelContainer container : csvModelContainers) {
      List<ContactPointVersion> csvVersions = container.getCreateModels().stream().map(ContactPointVersionMapper::toEntity).toList();

      List<ItemImportResult> replacingErrors = replaceCsvMergedVersionsInDb(csvVersions);
      if (replacingErrors.isEmpty()) {
        List<ItemImportResult> versioningResult = insertOrUpdateViaVersioning(csvVersions);
        importResults.addAll(versioningResult);
      } else {
        importResults.addAll(replacingErrors);
      }
    }
    return importResults;
  }

  private List<ItemImportResult> insertOrUpdateViaVersioning(List<ContactPointVersion> csvVersions) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (ContactPointVersion contactPointVersion : csvVersions) {
      boolean platformExists = contactPointRepository.existsBySloid(contactPointVersion.getSloid());
      ItemImportResult itemImportResult;
      if (platformExists) {
        itemImportResult = updateContactPoint(contactPointVersion);
      } else {
        //itemImportResult = createVersion(contactPointVersion);
      }
      importResults.add(itemImportResult);
    }
    return importResults;
  }

  private ItemImportResult updateContactPoint(ContactPointVersion contactPointVersion) {
    try {
      //updateVersionForImportService(contactPointVersion);
      //return buildSuccessImportResult(contactPointVersion);
    } catch (VersioningNoChangesException exception) {
      log.info("Found version {} to import without modification: {}", contactPointVersion.getSloid(), exception.getMessage());
      //return buildSuccessImportResult(contactPointVersion);
    } catch (Exception exception) {
      log.error("[Platform Import]: Error during update", exception);
      //return buildFailedImportResult(contactPointVersion, exception);
    }
  }

  private List<ItemImportResult> replaceCsvMergedVersionsInDb(List<ContactPointVersion> csvVersions) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (ContactPointVersion contactPointVersion : csvVersions) {
      try {
        //clearVariantDependentProperties(contactPointVersion);
      } catch (AtlasException exception) {
        log.error("[Platform Import]: Error during clearVariantDependentProperties", exception);
        //importResults.add(buildFailedImportResult(contactPointVersion, exception));
      }
    }
  }
}
