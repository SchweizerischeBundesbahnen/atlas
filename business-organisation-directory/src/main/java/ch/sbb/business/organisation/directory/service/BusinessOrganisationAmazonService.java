package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.business.organisation.directory.service.export.BusinessOrganisationExportFileName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessOrganisationAmazonService {

  private static final BusinessOrganisationExportFileName exportFileName =
      BusinessOrganisationExportFileName.BUSINESS_ORGANISATION_VERSION;

  private final AmazonService amazonService;

  private final FileService fileService;

  public StreamingResponseBody streamJsonFile(ExportType exportType) {
    return fileService.streamingJsonFile(exportType, exportFileName, amazonService, getFileName(exportType));
  }

  public StreamingResponseBody streamGzipFile(ExportType exportType) {
    return fileService.streamingGzipFile(exportType, exportFileName, amazonService, getFileName(exportType));
  }

    public String getFileName(ExportType exportType) {
        LocalDate todayDate = LocalDate.now();
        return exportType.getFileTypePrefix() + exportFileName.getFileName() + "_" + todayDate;
    }

}
