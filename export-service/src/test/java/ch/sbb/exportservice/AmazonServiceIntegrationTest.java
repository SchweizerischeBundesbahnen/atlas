package ch.sbb.exportservice;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.controller.IntegrationTest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;

@IntegrationTest
class AmazonServiceIntegrationTest {

  private static final String INTEGRATION_TEST_DIR = "integration-test";
  private static final String CSV_FILE = "csv-minimal-service_point-2024-07-10.csv";
  private static final String JSON_FILE = "json-minimal-service_point-2024-07-10.json";

  @Autowired
  private AmazonService amazonService;

  @Test
  void shouldUploadAndDownloadCsvCorrectly() throws IOException {
    // Upload
    File file = getMinimalServicePointCsvFile();

    URL url = amazonService.putFile(AmazonBucket.EXPORT, file, INTEGRATION_TEST_DIR);
    assertThat(url.toString()).isEqualTo(
        "https://atlas-data-export-dev-dev.s3.eu-central-1.amazonaws.com/" + INTEGRATION_TEST_DIR + "/" + CSV_FILE);

    // Download
    File downloadedFile = amazonService.pullFile(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + CSV_FILE);

    assertThat(downloadedFile.length()).isEqualTo(file.length());
    assertThat(Files.readAllLines(file.toPath())).isEqualTo(Files.readAllLines(downloadedFile.toPath()));
  }

  @Test
  void shouldUploadAndStreamCsvCorrectly() throws IOException {
    // Upload
    File file = getMinimalServicePointCsvFile();

    URL url = amazonService.putFile(AmazonBucket.EXPORT, file, INTEGRATION_TEST_DIR);
    assertThat(url.toString()).isEqualTo(
        "https://atlas-data-export-dev-dev.s3.eu-central-1.amazonaws.com/" + INTEGRATION_TEST_DIR + "/" + CSV_FILE);

    // Stream
    InputStreamResource stream = amazonService.pullFileAsStream(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + CSV_FILE);

    assertThat(stream.getInputStream().readAllBytes().length).isEqualTo(file.length());
  }

  @Test
  void shouldUploadZippedCsvCorrectly() throws IOException {
    File file = getMinimalServicePointCsvFile();

    URL url = amazonService.putZipFile(AmazonBucket.EXPORT, file, INTEGRATION_TEST_DIR);
    InputStreamResource inputStreamResource = amazonService.pullFileAsStream(AmazonBucket.EXPORT,
        INTEGRATION_TEST_DIR + "/" + CSV_FILE + ".zip");
    //check is a zip file
    assertThat(new ZipInputStream(inputStreamResource.getInputStream()).getNextEntry()).isNotNull();

    assertThat(url.toString()).isEqualTo(
        "https://atlas-data-export-dev-dev.s3.eu-central-1.amazonaws.com/" + INTEGRATION_TEST_DIR + "/" + CSV_FILE +
            ".zip");
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
    assertThat(new GZIPInputStream(inputStreamResource.getInputStream()).readAllBytes()).isNotNull();
    assertThat(url.toString()).isEqualTo(
        "https://atlas-data-export-dev-dev.s3.eu-central-1.amazonaws.com/" + INTEGRATION_TEST_DIR + "/" + JSON_FILE + ".gz");
  }

  @Test
  void shouldFindLastJsonUploadCorrectly() {
    String latestJsonKey = amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT,
        "service_point/full", "full-swiss-only-service_point-");

    String date = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now());
    assertThat(latestJsonKey).isEqualTo("service_point/full/full-swiss-only-service_point-" + date + ".json.gz");
  }

  private File getMinimalServicePointCsvFile() throws IOException {
    return getMinimalFileAsCopy(CSV_FILE);
  }

  private File getMinimalServicePointJsonFile() throws IOException {
    return getMinimalFileAsCopy(JSON_FILE);
  }

  private File getMinimalFileAsCopy(String name) throws IOException {
    try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("s3/" + name)) {
      if (!Files.exists(Paths.get(INTEGRATION_TEST_DIR))) {
        Files.createDirectory(Paths.get(INTEGRATION_TEST_DIR));
      }
      File file = new File(INTEGRATION_TEST_DIR + "/" + name);
      Files.copy(Objects.requireNonNull(inputStream), file.toPath());
      return file;
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    amazonService.deleteFile(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + CSV_FILE);
    amazonService.deleteFile(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + CSV_FILE + ".zip");
    amazonService.deleteFile(AmazonBucket.EXPORT, INTEGRATION_TEST_DIR + "/" + JSON_FILE + ".gz");

    Files.deleteIfExists(Paths.get(INTEGRATION_TEST_DIR, CSV_FILE));
    Files.deleteIfExists(Paths.get(INTEGRATION_TEST_DIR, JSON_FILE));
  }
}