package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.base.service.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
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

  private final ServicePointVersionRepository servicePointVersionRepository;
  private final ServicePointService servicePointService;

  private static ServicePointItemImportResult buildImportSuccessResult(ServicePointVersion servicePointVersion) {
    return ServicePointItemImportResult.builder()
        .itemNumber(servicePointVersion.getNumber().getValue())
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .status("SUCCESS")
        .message("[SUCCESS]: This version was imported successfully")
        .build();
  }

  private static ServicePointItemImportResult buildImportFailedResult(ServicePointVersion servicePointVersion,
      Exception exception) {
    return ServicePointItemImportResult.builder()
        .itemNumber(servicePointVersion.getNumber().getNumber())
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .status("FAILED")
        .message(
            "[FAILED]: This version could not be imported due to: " + exception.getMessage())
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

  public void importServicePointCsvModels(List<ServicePointCsvModel> csvModels) {
    List<ServicePointVersion> servicePointVersions = csvModels
        .stream()
        .map(new ServicePointCsvToEntityMapper())
        .toList();
    servicePointVersionRepository.saveAll(servicePointVersions);
  }

  public List<ServicePointItemImportResult> importServicePoints(
      List<ServicePointCsvModelContainer> servicePointCsvModelContainers) {

    List<ServicePointItemImportResult> results = new ArrayList<>();
    servicePointCsvModelContainers.forEach(servicePointCsvModelContainer -> {
      log.info("Import Service Point with didokCode {} and {} versions", servicePointCsvModelContainer.getDidokCode(),
          servicePointCsvModelContainer.getServicePointCsvModelList().size());
      List<ServicePointVersion> servicePointVersions = servicePointCsvModelContainer.getServicePointCsvModelList()
          .stream()
          .map(new ServicePointCsvToEntityMapper())
          .toList();

      for (ServicePointVersion servicePointVersion : servicePointVersions) {
        // check if already existing
        boolean existing = servicePointService.isServicePointNumberExisting(servicePointVersion.getNumber());
        if (existing) {
          try {
            servicePointService.updateServicePointVersion(servicePointVersion);
            results.add(buildImportSuccessResult(servicePointVersion));
          } catch (Exception e) {
            if (e instanceof VersioningNoChangesException) {
              log.info("Found version {} to import without modification: {}", servicePointVersion.getNumber(), e.getMessage());
            } else {
              log.error("Error {}", e);
              results.add(buildImportFailedResult(servicePointVersion, e));
            }
          }
        } else {
          try {
            ServicePointVersion saved = servicePointService.save(servicePointVersion);
            results.add(buildImportSuccessResult(saved));
          } catch (Exception e) {
            log.error("service point save error", e);
            results.add(buildImportFailedResult(servicePointVersion, e));
          }
        }
      }
    });

    return results;
  }

}
