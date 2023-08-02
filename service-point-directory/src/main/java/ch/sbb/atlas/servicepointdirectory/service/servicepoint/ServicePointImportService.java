package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointItemImportResult.ServicePointItemImportResultBuilder;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.service.BasePointUtility;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.util.BeanCopyUtil;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import com.fasterxml.jackson.databind.MappingIterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointImportService {

  private final ServicePointService servicePointService;
  private final VersionableService versionableService;
  private final ServicePointFotCommentService servicePointFotCommentService;

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

  public List<ServicePointItemImportResult> importServicePoints(
      List<ServicePointCsvModelContainer> servicePointCsvModelContainers
  ) {
    List<ServicePointItemImportResult> importResults = new ArrayList<>();
    for (ServicePointCsvModelContainer container : servicePointCsvModelContainers) {
      List<ServicePointVersion> servicePointVersions = container.getServicePointCsvModelList()
          .stream()
          .map(new ServicePointCsvToEntityMapper())
          .toList();
      replaceCsvMergedVersions(container, servicePointVersions);
      for (ServicePointVersion servicePointVersion : servicePointVersions) {
        boolean servicePointNumberExisting = servicePointService.isServicePointNumberExisting(servicePointVersion.getNumber());
        if (servicePointNumberExisting) {
          ServicePointItemImportResult updateResult = updateServicePointVersion(servicePointVersion);
          importResults.add(updateResult);
        } else {
          ServicePointItemImportResult saveResult = saveServicePointVersion(servicePointVersion);
          importResults.add(saveResult);
        }
      }
      saveFotComment(container);
    }
    return importResults;
  }

  /**
   * In case we want to merge 2 or more versions from a CSV File (Import or "Massen Import") first we need to compare the
   * number of the found DB versions with the number of the versions present in the CSV File.
   * If the number of the CSV File versions are less than the DB Versions, and we found more than one version
   * exactly included between CSV Version validFrom and CSV Version validTo, than we replace the versions properties,
   * (expect validFrom, validTo and id) and save the versions that comes from the DB.
   */
  private void replaceCsvMergedVersions(ServicePointCsvModelContainer container, List<ServicePointVersion> csvServicePointVersions) {
    List<ServicePointVersion> dbVersions = servicePointService.findAllByNumberOrderByValidFrom(ServicePointNumber.of(container.getDidokCode()));
    if(dbVersions.size() > csvServicePointVersions.size()) {
      log.info("The ServicePoint CSV versions are less than the ServicePoint versions stored in the DB. A merge may have taken place...");
      for(ServicePointVersion csvVersion : csvServicePointVersions) {
        List<ServicePointVersion> dbVersionsFoundToBeReplaced =
                findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(csvVersion.getValidFrom(), csvVersion.getValidTo(), dbVersions);
        if(dbVersionsFoundToBeReplaced.size() > 1) {
          updateMergedVersions(csvVersion, dbVersionsFoundToBeReplaced);
        }
      }
    }
  }

  private void updateMergedVersions(ServicePointVersion csvVersion, List<ServicePointVersion> dbVersionsFoundToBeReplaced) {
    log.info("The properties of the following versions: {}", dbVersionsFoundToBeReplaced);
    for(ServicePointVersion dbVersion : dbVersionsFoundToBeReplaced) {
      log.info("will be overridden with (expect [validFrom, validTo, id]): {}", dbVersion);
      BeanCopyUtil.copyNonNullProperties(csvVersion,dbVersion, Fields.validFrom,Fields.validTo,Fields.id);
      if(dbVersion.getServicePointGeolocation() != null) {
        dbVersion.getServicePointGeolocation().setServicePointVersion(dbVersion);
      }
      servicePointService.save(dbVersion);
    }
  }

  List<ServicePointVersion> findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
          LocalDate editedValidFrom, LocalDate editedValidTo, List<ServicePointVersion> versions) {
    List<ServicePointVersion> collected = versions.stream()
            .filter(toVersioning -> !toVersioning.getValidFrom().isAfter(editedValidTo))
            .filter(toVersioning -> !toVersioning.getValidTo().isBefore(editedValidFrom))
            .collect(Collectors.toList());
    if(!collected.isEmpty() &&
            (collected.get(0).getValidFrom().equals(editedValidFrom) && collected.get(collected.size()-1).getValidTo().equals(editedValidTo))){
      return collected;
    }
    return List.of();
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


  private ServicePointItemImportResult saveServicePointVersion(ServicePointVersion servicePointVersion) {
    try {
      ServicePointVersion savedServicePointVersion = servicePointService.saveWithoutValidationForImportOnly(servicePointVersion);
      return buildSuccessImportResult(savedServicePointVersion);
    } catch (Exception exception) {
      log.error("[Service-Point Import]: Error during save", exception);
      return buildFailedImportResult(servicePointVersion, exception);
    }
  }

  private ServicePointItemImportResult updateServicePointVersion(ServicePointVersion servicePointVersion) {
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

  public void updateServicePointVersionForImportService(ServicePointVersion edited) {
    List<ServicePointVersion> dbVersions = servicePointService.findAllByNumberOrderByValidFrom(edited.getNumber());
    ServicePointVersion current = BasePointUtility.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsForImportFromCsv(current, edited,
        dbVersions);
    BasePointUtility.addCreateAndEditDetailsToGeolocationPropertyFromVersionedObjects(versionedObjects,
        Fields.servicePointGeolocation);
    versionableService.applyVersioning(ServicePointVersion.class, versionedObjects,
        servicePointService::saveWithoutValidationForImportOnly,
        servicePointService::deleteById);
  }

  private ServicePointItemImportResult buildSuccessImportResult(ServicePointVersion servicePointVersion) {
    ServicePointItemImportResultBuilder successResultBuilder = ServicePointItemImportResult.successResultBuilder();
    return addServicePointInfoTo(successResultBuilder, servicePointVersion).build();
  }

  private ServicePointItemImportResult buildFailedImportResult(ServicePointVersion servicePointVersion, Exception exception) {
    ServicePointItemImportResultBuilder failedResultBuilder = ServicePointItemImportResult.failedResultBuilder(exception);
    return addServicePointInfoTo(failedResultBuilder, servicePointVersion).build();
  }

  private ServicePointItemImportResultBuilder addServicePointInfoTo(
      ServicePointItemImportResult.ServicePointItemImportResultBuilder servicePointItemImportResultBuilder,
      ServicePointVersion servicePointVersion
  ) {
    return servicePointItemImportResultBuilder
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .itemNumber(servicePointVersion.getNumber().getValue());
  }

}
