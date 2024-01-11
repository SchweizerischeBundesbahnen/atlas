package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.imports.util.DidokCsvMapper;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.HeightNotCalculatableException;
import ch.sbb.atlas.servicepointdirectory.service.BaseImportServicePointDirectoryService;
import ch.sbb.atlas.servicepointdirectory.service.BasePointUtility;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoAdminHeightResponse;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import com.fasterxml.jackson.databind.MappingIterator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ServicePointImportService extends BaseImportServicePointDirectoryService<ServicePointVersion> {

  private final ServicePointService servicePointService;
  private final VersionableService versionableService;
  private final ServicePointFotCommentService servicePointFotCommentService;
  private final ServicePointDistributor servicePointDistributor;
  private final ServicePointNumberService servicePointNumberService;
  private final GeoReferenceService geoReferenceService;

  @Override
  protected void save(ServicePointVersion servicePointVersion) {
    servicePointService.saveWithoutValidationForImportOnly(servicePointVersion, servicePointVersion.getStatus());
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
      final String sloid = container.getServicePointCsvModelList().get(0).getSloid();
      try {
        servicePointService.claimSloid(sloid);
      } catch (FeignException e) {
        final ItemImportResult itemImportResult = new ItemImportResult();
        itemImportResult.setItemNumber(sloid);
        itemImportResult.setStatus(ItemImportResponseStatus.FAILED);
        itemImportResult.setMessage("[FAILED]: The following sloid could not be claimed: " + sloid);
        importResults.add(itemImportResult);
        continue;
      }

      List<ServicePointVersion> servicePointVersions = container.getServicePointCsvModelList()
          .stream()
          .map(new ServicePointCsvToEntityMapper())
          .toList();

      ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(container.getDidokCode());
      List<ServicePointVersion> dbVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
      replaceCsvMergedVersions(dbVersions, servicePointVersions);

      for (ServicePointVersion servicePointVersion : servicePointVersions) {
        boolean servicePointNumberExisting = servicePointService.isServicePointNumberExisting(servicePointVersion.getNumber());

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

  public void updateServicePointVersionForImportService(ServicePointVersion edited, List<ServicePointVersion> dbVersions, ServicePointVersion current) {
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
    BasePointUtility.addCreateAndEditDetailsToGeolocationPropertyFromVersionedObjects(versionedObjects,
        ServicePointVersion.Fields.servicePointGeolocation);
    versionableService.applyVersioning(ServicePointVersion.class, versionedObjects,
        version -> servicePointService.saveWithoutValidationForImportOnly(version, edited.getStatus()),
        new ApplyVersioningDeleteByIdLongConsumer(servicePointService.getServicePointVersionRepository()));
  }

  private ServicePointVersion saveOnlyStatusIfOnlyStatusIsUpdated(Long id, Status status) {
    ServicePointVersion existingServicePointVersion = servicePointService.findById(id)
            .orElseThrow(() -> new NotFoundException.IdNotFoundException(id));
    existingServicePointVersion.setStatus(status);
    existingServicePointVersion.setEditionDate(LocalDateTime.now());
    return existingServicePointVersion;
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
    List<Exception> warnings = new ArrayList<>();

    getHeightForServicePointImport(servicePointVersion, warnings);

    try {
      ServicePointVersion savedServicePointVersion = servicePointService.saveWithoutValidationForImportOnly(servicePointVersion,
          servicePointVersion.getStatus());
      servicePointNumberService.deleteAvailableNumber(savedServicePointVersion.getNumber(),
          savedServicePointVersion.getCountry());
    } catch (Exception exception) {
      log.error("[Service-Point Import]: Error during save", exception);
      return buildFailedImportResult(servicePointVersion, exception);
    }

    return buildSuccessMessageBasedOnWarnings(servicePointVersion, warnings);
  }

  private ItemImportResult updateServicePointVersion(ServicePointVersion servicePointVersion) {
    List<Exception> warnings = new ArrayList<>();
    getHeightForServicePointImport(servicePointVersion, warnings);
    List<ServicePointVersion> dbVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointVersion.getNumber());
    ServicePointVersion current = ImportUtils.getCurrentPointVersion(dbVersions, servicePointVersion);
    try {
      updateServicePointVersionForImportService(servicePointVersion, dbVersions, current);
    } catch (VersioningNoChangesException exception) {
      if (!servicePointVersion.getStatus().equals(current.getStatus())) {
        log.info("During the service point import, a service point with the number {} was identified, where only the status changed from {} to {}",
                servicePointVersion.getNumber().getValue(),
                current.getStatus(),
                servicePointVersion.getStatus());
        saveOnlyStatusIfOnlyStatusIsUpdated(current.getId(), servicePointVersion.getStatus());
      } else {
        log.info("Found version {} to import without modification: {}",
                servicePointVersion.getNumber().getValue(),
                exception.getMessage()
        );
        return buildSuccessImportResult(servicePointVersion);
      }
    } catch (Exception exception) {
      log.error("[Service-Point Import]: Error during update", exception);
      return buildFailedImportResult(servicePointVersion, exception);
    }

    return buildSuccessMessageBasedOnWarnings(servicePointVersion, warnings);
  }

  private void getHeightForServicePointImport(ServicePointVersion servicePointVersion, List<Exception> warnings) {
    ServicePointGeolocation servicePointGeolocation = servicePointVersion.getServicePointGeolocation();
    try {
      if (servicePointGeolocation != null && servicePointGeolocation.getHeight() == null) {
        GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(servicePointGeolocation.asCoordinatePair());
        servicePointGeolocation.setHeight(geoAdminHeightResponse.getHeight());
      }
    } catch (HeightNotCalculatableException exception) {
      log.warn("[Service-Point Import]: Warning during height calculation ", exception);
      warnings.add(exception);
    }
  }

  private ItemImportResult buildSuccessMessageBasedOnWarnings(ServicePointVersion servicePointVersion, List<Exception> warnings) {
    if (!warnings.isEmpty()) {
      return buildWarningImportResult(servicePointVersion, warnings);
    } else {
      return buildSuccessImportResult(servicePointVersion);
    }
  }
}
