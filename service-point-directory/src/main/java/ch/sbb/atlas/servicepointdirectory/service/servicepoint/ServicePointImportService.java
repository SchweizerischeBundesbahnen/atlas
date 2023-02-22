package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult.ServicePointItemImportResultBuilder;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointImportService {

  private final ServicePointService servicePointService;

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
    }
    return importResults;
  }

  private ServicePointItemImportResult saveServicePointVersion(ServicePointVersion servicePointVersion) {
    try {
      ServicePointVersion savedServicePointVersion = servicePointService.save(servicePointVersion);
      return buildSuccessImportResult(savedServicePointVersion);
    } catch (Exception exception) {
      log.error("[Service-Point Import]: Error during save", exception);
      return buildFailedImportResult(servicePointVersion, exception);
    }
  }

  private ServicePointItemImportResult updateServicePointVersion(ServicePointVersion servicePointVersion) {
    try {
      servicePointService.updateServicePointVersion(servicePointVersion);
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
