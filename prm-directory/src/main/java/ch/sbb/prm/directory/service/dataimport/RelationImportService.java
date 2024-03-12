package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.exception.ReducedVariantException;
import ch.sbb.prm.directory.mapper.RelationVersionMapper;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.service.ContactPointService;
import ch.sbb.prm.directory.service.ParkingLotService;
import ch.sbb.prm.directory.service.PlatformService;
import ch.sbb.prm.directory.service.ReferencePointService;
import ch.sbb.prm.directory.service.RelationService;
import ch.sbb.prm.directory.service.StopPointService;
import ch.sbb.prm.directory.service.ToiletService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelationImportService extends BasePrmImportService<RelationVersion>{

    private final RelationRepository relationRepository;
    private final RelationService relationService;
    private final VersionableService versionableService;
    private final ReferencePointService referencePointService;
    private final ToiletService toiletService;
    private final ParkingLotService parkingLotService;
    private final ContactPointService contactPointService;
    private final PlatformService platformService;
    private final StopPointService stopPointService;

    private static final String REFERENCE_POINT = "REFERENCE_POINT";

    @Override
    protected void save(RelationVersion version) {
        version.setStatus(Status.VALIDATED);
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

            List<RelationVersion> dbVersions = relationService.getAllVersionsBySloidAndReferencePoint(
                    relationVersions.iterator().next().getSloid(), relationVersions.iterator().next().getReferencePointSloid());
            replaceCsvMergedVersions(dbVersions, relationVersions);

            for (RelationVersion  relationVersion : relationVersions) {
                boolean relationExists = relationRepository.existsBySloidAndReferencePointSloid(relationVersion.getSloid(), relationVersion.getReferencePointSloid());
                ItemImportResult itemImportResult;

                if (relationExists) {
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
        List<RelationVersion> dbVersions = relationService.getAllVersionsBySloidAndReferencePoint(edited.getSloid(), edited.getReferencePointSloid());
        RelationVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
        List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
                dbVersions);
        ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
        versionableService.applyVersioning(RelationVersion.class, versionedObjects, this::save,
                new ApplyVersioningDeleteByIdLongConsumer(relationRepository));
    }

    private ItemImportResult createVersion(RelationVersion relationVersion) {
        try {
            checkReferencePointExists(relationVersion);
            checkStopPointExists(relationVersion);
            checkElementExists(relationVersion.getReferencePointElementType(), relationVersion.getSloid());
            RelationVersion savedVersion = relationService.createRelationThroughImport(relationVersion);
            return buildSuccessImportResult(savedVersion);
        } catch (AtlasException exception) {
            log.error("[Relation Import]: Error during save", exception);
            return buildFailedImportResult(relationVersion, exception);
        }
    }

    private void checkReferencePointExists(RelationVersion version){
        referencePointService.checkReferencePointExists(version.getReferencePointSloid(), REFERENCE_POINT);
    }

    private void checkStopPointExists(RelationVersion version){
        stopPointService.checkStopPointExists(version.getParentServicePointSloid());
        if(stopPointService.isReduced(version.getParentServicePointSloid())){
            throw new ReducedVariantException();
        }
    }

    public void checkElementExists(ReferencePointElementType type, String sloid) {
        if (type == ReferencePointElementType.CONTACT_POINT) {
            contactPointService.checkContactPointExists(sloid, ReferencePointElementType.CONTACT_POINT.name());
        }
        if (type == ReferencePointElementType.PLATFORM) {
            platformService.checkPlatformExists(sloid, ReferencePointElementType.PLATFORM.name());
        }
        if (type == ReferencePointElementType.PARKING_LOT) {
            parkingLotService.checkParkingLotExists(sloid, ReferencePointElementType.PARKING_LOT.name());
        }
        if (type == ReferencePointElementType.TOILET) {
            toiletService.checkToiletExists(sloid, ReferencePointElementType.TOILET.name());
        }
    }
}
