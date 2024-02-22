package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.mapper.RelationVersionMapper;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.service.RelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelationImportService extends BasePrmImportService<RelationVersion>{

    private final RelationRepository relationRepository;
    private final RelationService relationService;
    private final VersionableService versionableService;

    @Override
    protected void save(RelationVersion version) {
        relationRepository.saveAndFlush(version);
    }

    @Override
    protected ItemImportResult addInfoToItemImportResult(ItemImportResult.ItemImportResultBuilder itemImportResultBuilder,
                                                         RelationVersion version) {
        return itemImportResultBuilder
                .validFrom(version.getValidFrom())
                .validTo(version.getValidTo())
                .itemNumber(version.getNumber().asString())
                .build();
    }

    public List<ItemImportResult> importRelations(List<RelationCsvModelContainer> csvModelContainers) {
        List<ItemImportResult> importResults = new ArrayList<>();
        for (RelationCsvModelContainer container : csvModelContainers) {
            List<RelationVersion> relationVersions = container.getCreateModels().stream()
                    .map(RelationVersionMapper::toEntity).toList();

            List<RelationVersion> dbVersions = relationService.getAllVersions(
                    relationVersions.iterator().next().getSloid());
            replaceCsvMergedVersions(dbVersions, relationVersions);

            for (RelationVersion  relationVersion : relationVersions) {
                boolean contactPointExists = relationRepository.existsBySloid(relationVersion.getSloid());
                ItemImportResult itemImportResult;

                if (contactPointExists) {
                    itemImportResult = updateRelation(relationVersion);
                } else {
                    itemImportResult = createVersion(relationVersion);
                }
                importResults.add(itemImportResult);
            }
        }
        return importResults;
    }

    private ItemImportResult updateRelation(RelationVersion relationVersion) {
        try {
            updateVersionForImportService(relationVersion);
            return buildSuccessImportResult(relationVersion);
        } catch (VersioningNoChangesException exception) {
            log.info("Found version {} to import without modification: {}", relationVersion.getSloid(), exception.getMessage());
            return buildSuccessImportResult(relationVersion);
        } catch (Exception exception) {
            log.error("[Relation Import]: Error during update", exception);
            return buildFailedImportResult(relationVersion, exception);
        }
    }

    private void updateVersionForImportService(RelationVersion edited) {
        List<RelationVersion> dbVersions = relationService.getAllVersions(edited.getSloid());
        RelationVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
        List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
                dbVersions);
        ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
        versionableService.applyVersioning(RelationVersion.class, versionedObjects, this::save,
                new ApplyVersioningDeleteByIdLongConsumer(relationRepository));
    }

    private ItemImportResult createVersion(RelationVersion relationVersion) {
        try {
            RelationVersion savedVersion = relationService.createRelationThroughImport(relationVersion);
            return buildSuccessImportResult(savedVersion);
        } catch (AtlasException exception) {
            log.error("[Relation Import]: Error during save", exception);
            return buildFailedImportResult(relationVersion, exception);
        }
    }
}
