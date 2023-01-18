package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepointdirectory.api.ServicePointImportResult;
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

  public void importServicePointCsvModels(List<ServicePointCsvModel> csvModels) {
    List<ServicePointVersion> servicePointVersions = csvModels
        .stream()
        .map(new ServicePointCsvToEntityMapper())
        .toList();
    servicePointVersionRepository.saveAll(servicePointVersions);
  }

  public List<ServicePointImportResult> importServicePoints(List<ServicePointCsvModel> servicePointCsvModels) {
    List<ServicePointVersion> servicePointVersions = servicePointCsvModels
        .stream()
        .map(new ServicePointCsvToEntityMapper())
        .toList();
    List<ServicePointImportResult> results = new ArrayList<>();
    for (ServicePointVersion servicePointVersion : servicePointVersions) {
      // check if already existing
      boolean existing = servicePointService.isServicePointNumberExisting(servicePointVersion.getNumber());
      if (existing) {
        try {
          servicePointService.updateServicePointVersion(servicePointVersion);
          results.add(buildImportSuccessResult(servicePointVersion));
        } catch (Exception e) {
          log.error("service point update error", e);
          results.add(buildImportFailedResult(servicePointVersion, e));
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
    return results;
  }

  private static ServicePointImportResult buildImportSuccessResult(ServicePointVersion servicePointVersion) {
    return ServicePointImportResult.builder()
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .status("SUCCESS")
        .message("[SUCCESS]: This version was imported successfully")
        .build();
  }

  private static ServicePointImportResult buildImportFailedResult(ServicePointVersion servicePointVersion, Exception exception) {
    return ServicePointImportResult.builder()
        .servicePointNumber(servicePointVersion.getNumber())
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

}
