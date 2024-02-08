package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.mapper.ContactPointVersionMapper;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.service.ContactPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactPointImportService extends BasePrmImportService<ContactPointVersion>{

    private final ContactPointRepository contactPointRepository;
    private final ContactPointService contactPointService;
    private final VersionableService versionableService;

    @Override
    protected void save(ContactPointVersion version) {contactPointService.save(version);
    }

    @Override
    protected ItemImportResult addInfoToItemImportResult(ItemImportResult.ItemImportResultBuilder itemImportResultBuilder,
                                                         ContactPointVersion version) {
        return itemImportResultBuilder
                .validFrom(version.getValidFrom())
                .validTo(version.getValidTo())
                .itemNumber(version.getNumber().asString())
                .build();
    }

    public List<ItemImportResult> importContactPoints(List<ContactPointCsvModelContainer> csvModelContainers) {
        List<ItemImportResult> importResults = new ArrayList<>();
        for (ContactPointCsvModelContainer container : csvModelContainers) {
            List<ContactPointVersion> contactPointVersions = container.getCreateModels().stream()
                    .map(ContactPointVersionMapper::toEntity).toList();

            List<ContactPointVersion> dbVersions = contactPointService.getAllVersions(
                    contactPointVersions.iterator().next().getSloid());
            replaceCsvMergedVersions(dbVersions, contactPointVersions);

            for (ContactPointVersion  contactPointVersion : contactPointVersions) {
                boolean contactPointExists = contactPointRepository.existsBySloid(contactPointVersion.getSloid());
                ItemImportResult itemImportResult;

                if (contactPointExists) {
                    itemImportResult = updateContactPoint(contactPointVersion);
                } else {
                    itemImportResult = createVersion(contactPointVersion);
                }
                importResults.add(itemImportResult);
            }
        }
        return importResults;
    }

    private ItemImportResult updateContactPoint(ContactPointVersion contactPointVersion) {
        try {
            updateVersionForImportService(contactPointVersion);
            return buildSuccessImportResult(contactPointVersion);
        } catch (VersioningNoChangesException exception) {
            log.info("Found version {} to import without modification: {}", contactPointVersion.getSloid(), exception.getMessage());
            return buildSuccessImportResult(contactPointVersion);
        } catch (Exception exception) {
            log.error("[Contact Point Import]: Error during update", exception);
            return buildFailedImportResult(contactPointVersion, exception);
        }
    }

    private void updateVersionForImportService(ContactPointVersion edited) {
        List<ContactPointVersion> dbVersions = contactPointService.getAllVersions(edited.getSloid());
        ContactPointVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
        List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
                dbVersions);
        ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
        versionableService.applyVersioning(ContactPointVersion.class, versionedObjects, this::save,
                new ApplyVersioningDeleteByIdLongConsumer(contactPointRepository));
    }

    private ItemImportResult createVersion(ContactPointVersion contactPointVersion) {
        try {
            ContactPointVersion savedVersion = contactPointService.save(contactPointVersion);
            return buildSuccessImportResult(savedVersion);
        } catch (AtlasException exception) {
            log.error("[Contact Point Import]: Error during save", exception);
            return buildFailedImportResult(contactPointVersion, exception);
        }
    }
}
