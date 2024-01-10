package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.business.organisation.directory.service.export.BusinessOrganisationExportFileName;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessOrganisationAmazonService {

  private final AmazonFileStreamingService amazonFileStreamingService;

  public InputStreamResource streamJsonFile(ExportType exportType) {
    String fileToStream = getFileToStream(exportType);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, fileToStream);
  }

  public InputStreamResource streamGzipFile(ExportType exportType) {
    String fileToStream = getFileToStream(exportType);
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileToStream);
  }

  private String getFileToStream(ExportType exportType) {
    return BusinessOrganisationExportFileName.BUSINESS_ORGANISATION_VERSION.getBaseDir() + "/" +
        getFileName(exportType) + ".json.gz";
  }

  public String getFileName(ExportType exportType) {
    return exportType.getFileTypePrefix() + BusinessOrganisationExportFileName.BUSINESS_ORGANISATION_VERSION.getFileName() + "_" +
        DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now());
  }

}
