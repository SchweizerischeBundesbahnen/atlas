package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.imports.util.DidokCsvMapper;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.HeightNotCalculatableException;
import ch.sbb.atlas.servicepointdirectory.service.BaseImportServicePointDirectoryService;
import ch.sbb.atlas.servicepointdirectory.service.BasePointUtility;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointImportService extends BaseImportServicePointDirectoryService<ServicePointVersion> {

  private final ServicePointService servicePointService;
  private final VersionableService versionableService;
  private final ServicePointFotCommentService servicePointFotCommentService;
  private final ServicePointDistributor servicePointDistributor;
  private final ServicePointNumberService servicePointNumberService;
  private final GeoReferenceService geoReferenceService;

  @Override
  protected void save(ServicePointVersion servicePointVersion) {
    servicePointService.saveWithoutValidationForImportOnly(servicePointVersion);
  }

  @Override
  protected String[] getIgnoredPropertiesWithoutGeolocation() {
    return new String[]{
        ServicePointVersion.Fields.validFrom,
        ServicePointVersion.Fields.validTo,
        ServicePointVersion.Fields.id
    };
  }

  @Override
  protected String[] getIgnoredPropertiesWithGeolocation() {
    return ArrayUtils.add(getIgnoredPropertiesWithoutGeolocation(), ServicePointVersion.Fields.servicePointGeolocation);
  }

  @Override
  protected String getIgnoredReferenceFieldOnGeolocationEntity() {
    return ServicePointGeolocation.Fields.servicePointVersion;
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder,
      ServicePointVersion servicePointVersion) {
    return itemImportResultBuilder
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .itemNumber(servicePointVersion.getNumber().asString())
        .build();
  }

  public static List<ServicePointCsvModel> parseServicePoints(InputStream inputStream)
      throws IOException {
    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    List<ServicePointCsvModel> servicePoints = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      servicePoints.add(mappingIterator.next());
    }
    log.info("Parsed {} servicePoints", servicePoints.size());
    return servicePoints;
  }

  public List<ItemImportResult> importServicePoints(
      List<ServicePointCsvModelContainer> servicePointCsvModelContainers
  ) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (ServicePointCsvModelContainer container : servicePointCsvModelContainers) {
      List<ServicePointVersion> servicePointVersions = container.getServicePointCsvModelList()
          .stream()
          .map(new ServicePointCsvToEntityMapper())
          .toList();

      ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(container.getDidokCode());
      List<ServicePointVersion> dbVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
      replaceCsvMergedVersions(dbVersions, servicePointVersions);

      for (ServicePointVersion servicePointVersion : servicePointVersions) {
        boolean servicePointNumberExisting = servicePointService.isServicePointNumberExisting(servicePointVersion.getNumber());

        //TODO umbauen
        ItemImportResult heightResult = getHeightForServicePointImport(servicePointVersion);
        if(heightResult != null){
          importResults.add(heightResult);
        }

        if (servicePointNumberExisting) {
          ItemImportResult updateResult = updateServicePointVersion(servicePointVersion);
          importResults.add(updateResult);
        } else {
          ItemImportResult saveResult = saveServicePointVersion(servicePointVersion);
          importResults.add(saveResult);
        }
      }
      servicePointDistributor.publishServicePointsWithNumbers(servicePointNumber);
      saveFotComment(container);
    }
    return importResults;
  }

  public void updateServicePointVersionForImportService(ServicePointVersion edited) {
    List<ServicePointVersion> dbVersions = servicePointService.findAllByNumberOrderByValidFrom(edited.getNumber());
    ServicePointVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
    BasePointUtility.addCreateAndEditDetailsToGeolocationPropertyFromVersionedObjects(versionedObjects,
        ServicePointVersion.Fields.servicePointGeolocation);
    versionableService.applyVersioning(ServicePointVersion.class, versionedObjects,
        servicePointService::saveWithoutValidationForImportOnly,
        new ApplyVersioningDeleteByIdLongConsumer(servicePointService.getServicePointVersionRepository()));
  }

  private void saveFotComment(ServicePointCsvModelContainer container) {
    Set<String> comments = container.getServicePointCsvModelList().stream().map(ServicePointCsvModel::getComment)
        .collect(Collectors.toSet());
    if (comments.size() != 1) {
      throw new IllegalStateException("We had different comments on didok_code: " + container.getDidokCode());
    }
    servicePointFotCommentService.importFotComment(ServicePointFotComment.builder()
        .servicePointNumber(container.getDidokCode())
        .fotComment(comments.iterator().next())
        .build());
  }

  private ItemImportResult saveServicePointVersion(ServicePointVersion servicePointVersion) {
    try {
      ServicePointVersion savedServicePointVersion = servicePointService.saveWithoutValidationForImportOnly(servicePointVersion);
      servicePointNumberService.deleteAvailableNumber(savedServicePointVersion.getNumber(),
          savedServicePointVersion.getCountry());
      return buildSuccessImportResult(savedServicePointVersion);
    } catch (Exception exception) {
      log.error("[Service-Point Import]: Error during save", exception);
      return buildFailedImportResult(servicePointVersion, exception);
    }
  }

  private ItemImportResult updateServicePointVersion(ServicePointVersion servicePointVersion) {
    try {


      updateServicePointVersionForImportService(servicePointVersion);
      return buildSuccessImportResult(servicePointVersion);
    } catch (Exception exception) {
      if (exception instanceof VersioningNoChangesException) {
        log.info("Found version {} to import without modification: {}",
            servicePointVersion.getNumber().getValue(),
            exception.getMessage()
        );
        return buildSuccessImportResult(servicePointVersion);
      } else {
        log.error("[Service-Point Import]: Error during update", exception);
        return buildFailedImportResult(servicePointVersion, exception);
      }
    }
  }

  private ItemImportResult getHeightForServicePointImport(ServicePointVersion servicePointVersion){
    //TODO im update und create machen das try und catch

    try{
      geoReferenceService.getHeightForServicePoint(servicePointVersion, true);
    }catch (HeightNotCalculatableException e){
      return buildFailedImportResult(servicePointVersion, e);
    }
    return null;
  }

}
