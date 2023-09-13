package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.BaseAmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.business.organisation.directory.service.export.BusinessOrganisationExportFileName;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Service
public class BusinessOrganisationAmazonService extends BaseAmazonFileStreamingService {

  public BusinessOrganisationAmazonService(AmazonService amazonService, FileService fileService) {
    super(amazonService, fileService);
  }

  public StreamingResponseBody streamJsonFile(ExportType exportType) {
    String fileToStream = getFileToStream(exportType);
    return streamFileAndDecompress(AmazonBucket.EXPORT, fileToStream);
  }

  public StreamingResponseBody streamGzipFile(ExportType exportType) {
    String fileToStream = getFileToStream(exportType);
    return streamFile(AmazonBucket.EXPORT, fileToStream);
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
