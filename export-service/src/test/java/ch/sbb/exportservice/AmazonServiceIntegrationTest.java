package ch.sbb.exportservice;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.s3.config.AmazonBucket;
import ch.sbb.atlas.s3.service.AmazonService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;

@IntegrationTest
class AmazonServiceIntegrationTest {

  private static final String INTEGRATION_TEST_DIR = "integration-test";
  private static final String CSV_FILE = "csv-minimal-service_point-2024-07-10.csv";
  private static final String JSON_FILE = "json-minimal-service_point-2024-07-10.json";
  private static final String LATEST_JSON_DIR = "v2/service-point/full";
  private static final String LATEST_JSON_FILE_PREFIX = "full-swiss-service-point-";

  @Autowired
  private AmazonService amazonService;

  @Value("${amazon.bucketConfigs.export-files.endpoint}")
  private String s3Endpoint;

  @Value("${amazon.bucketConfigs.export-files.bucketName}")
  private String s3BucketName;

  @Test
  void shouldUploadAndDownloadCsvCorrectly() throws IOException {
    // Upload
    File file = getMinimalServicePointCsvFile();

    URL url = amazonService.putFile(AmazonBucket.EXPORT, file, INTEGRATION_TEST_DIR);
    assertThat(url).hasToString(expectedUrl(INTEGRATION_TEST_DIR + "/" + CSV_FILE));

    // Download
    File downloadedFile = amazonService.pullFile(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + CSV_FILE);

    assertThat(downloadedFile).hasSize(file.length());
    assertThat(Files.readAllLines(file.toPath())).isEqualTo(Files.readAllLines(downloadedFile.toPath()));
  }

  @Test
  void shouldUploadAndStreamCsvCorrectly() throws IOException {
    // Upload
    File file = getMinimalServicePointCsvFile();

    URL url = amazonService.putFile(AmazonBucket.EXPORT, file, INTEGRATION_TEST_DIR);
    assertThat(url).hasToString(expectedUrl(INTEGRATION_TEST_DIR + "/" + CSV_FILE));

    // Stream
    InputStreamResource stream = amazonService.pullFileAsStream(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + CSV_FILE);

    assertThat(stream.getInputStream().readAllBytes().length).isEqualTo(file.length());
  }

  @Test
  void shouldUploadZippedCsvCorrectly() throws IOException {
    File file = getMinimalServicePointCsvFile();

    URL url = amazonService.putZipFileCleanupBoth(AmazonBucket.EXPORT, file, INTEGRATION_TEST_DIR);
    InputStreamResource inputStreamResource = amazonService.pullFileAsStream(AmazonBucket.EXPORT,
        INTEGRATION_TEST_DIR + "/" + CSV_FILE + ".zip");
    //check is a zip file
    try (ZipInputStream zipInputStream = new ZipInputStream(inputStreamResource.getInputStream())) {
      assertThat(zipInputStream.getNextEntry()).isNotNull();
    }

    assertThat(url).hasToString(expectedUrl(INTEGRATION_TEST_DIR + "/" + CSV_FILE + ".zip"));
  }

  @Test
  void shouldUploadGzipJsonCorrectly() throws IOException {
    //given
    File file = getMinimalServicePointJsonFile();

    //when
    URL url = amazonService.putGzipFile(AmazonBucket.EXPORT, file, INTEGRATION_TEST_DIR);

    //then
    //check is a gz file
    InputStreamResource inputStreamResource = amazonService.pullFileAsStream(AmazonBucket.EXPORT,
        INTEGRATION_TEST_DIR + "/" + JSON_FILE + ".gz");
    try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStreamResource.getInputStream())) {
      assertThat(gzipInputStream.readAllBytes()).isNotNull();
    }
    assertThat(url).hasToString(expectedUrl(INTEGRATION_TEST_DIR + "/" + JSON_FILE + ".gz"));
  }

  @Test
  void shouldFindLastJsonUploadCorrectly() throws IOException {
    //given
    String fileName = LATEST_JSON_FILE_PREFIX + DATE_FORMATTER_BASE.format(LocalDate.now()) + ".json";
    File file = getMinimalFileAsCopy(JSON_FILE, fileName);
    amazonService.putGzipFile(AmazonBucket.EXPORT, file, LATEST_JSON_DIR);

    //when
    String latestJsonKey = amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT, LATEST_JSON_DIR,
        LATEST_JSON_FILE_PREFIX);

    //then
    assertThat(latestJsonKey).isEqualTo(LATEST_JSON_DIR + "/" + fileName + ".gz");
  }

  private String expectedUrl(String key) {
    return s3Endpoint + "/" + s3BucketName + "/" + key;
  }

  private File getMinimalServicePointCsvFile() throws IOException {
    return getMinimalFileAsCopy(CSV_FILE);
  }

  private File getMinimalServicePointJsonFile() throws IOException {
    return getMinimalFileAsCopy(JSON_FILE);
  }

  private File getMinimalFileAsCopy(String name) throws IOException {
    return getMinimalFileAsCopy(name, name);
  }

  private File getMinimalFileAsCopy(String resourceName, String targetName) throws IOException {
    try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("s3/" + resourceName)) {
      if (!Files.exists(Paths.get(INTEGRATION_TEST_DIR))) {
        Files.createDirectory(Paths.get(INTEGRATION_TEST_DIR));
      }
      File file = new File(INTEGRATION_TEST_DIR + "/" + targetName);
      Files.copy(Objects.requireNonNull(inputStream), file.toPath());
      return file;
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    String latestJsonFileName = LATEST_JSON_FILE_PREFIX + DATE_FORMATTER_BASE.format(LocalDate.now()) + ".json";

    amazonService.deleteFile(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + CSV_FILE);
    amazonService.deleteFile(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + CSV_FILE + ".zip");
    amazonService.deleteFile(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + JSON_FILE + ".gz");
    amazonService.deleteFile(AmazonBucket.EXPORT, LATEST_JSON_DIR + "/" + latestJsonFileName + ".gz");

    Files.deleteIfExists(Paths.get(INTEGRATION_TEST_DIR, CSV_FILE));
    Files.deleteIfExists(Paths.get(INTEGRATION_TEST_DIR, JSON_FILE));
    Files.deleteIfExists(Paths.get(INTEGRATION_TEST_DIR, latestJsonFileName));
  }
}
