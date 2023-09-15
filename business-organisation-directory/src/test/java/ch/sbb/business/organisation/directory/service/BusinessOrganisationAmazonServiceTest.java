package ch.sbb.business.organisation.directory.service;

import static org.mockito.Mockito.verify;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.export.enumeration.ExportType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BusinessOrganisationAmazonServiceTest {

  @Mock
  private AmazonFileStreamingService amazonFileStreamingService;

  private BusinessOrganisationAmazonService businessOrganisationAmazonService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    businessOrganisationAmazonService = new BusinessOrganisationAmazonService(amazonFileStreamingService);
  }

  @Test
  void shouldStreamJsonFileWhileDecompressing() {
    businessOrganisationAmazonService.streamJsonFile(ExportType.FULL);
    verify(amazonFileStreamingService).streamFileAndDecompress(AmazonBucket.EXPORT,
        "business_organisation/full_business_organisation_versions_" + LocalDate.now() + ".json.gz");
  }

  @Test
  void shouldStreamGzipFile() {
    businessOrganisationAmazonService.streamGzipFile(ExportType.FULL);
    verify(amazonFileStreamingService).streamFile(AmazonBucket.EXPORT,
        "business_organisation/full_business_organisation_versions_" + LocalDate.now() + ".json.gz");
  }
}