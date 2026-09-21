package ch.sbb.atlas.model.controller;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.util.ClassUtils;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Redirects all S3 traffic of the integration tests to a MinIO Testcontainer. Without this, the integration-test
 * profile resolves the credentials from Vault and the tests write into the shared DEV buckets.
 * <p>
 * The extension is registered on {@link IntegrationTest}, so every integration test is protected automatically. A
 * JUnit extension is used instead of an imported {@code @TestConfiguration} because the properties have to be
 * published before the Spring context is created, and because an additional {@code @Import} on a
 * {@link BaseControllerApiTest} subclass breaks the auto-configuration lookup of that test class.
 * <p>
 * A module opts in by depending on atlas-s3 and adding
 * {@code testImplementation("org.testcontainers:testcontainers-minio")}. Modules that do not use S3 omit them and no
 * container is started for them. The container therefore lives in the nested {@link MinioContainerHolder}, which is
 * only initialised once both are present.
 */
public class S3TestcontainerExtension implements BeforeAllCallback {

  private static final String AMAZON_CONFIG_CLASS = "ch.sbb.atlas.s3.config.AmazonConfigProps";
  private static final String MINIO_CONTAINER_CLASS = "org.testcontainers.containers.MinIOContainer";

  private static final boolean S3_TESTCONTAINER_SUPPORTED =
      isOnClasspath(AMAZON_CONFIG_CLASS) && isOnClasspath(MINIO_CONTAINER_CLASS);

  @Override
  public void beforeAll(ExtensionContext context) {
    if (S3_TESTCONTAINER_SUPPORTED) {
      MinioContainerHolder.publishProperties();
    }
  }

  private static boolean isOnClasspath(String className) {
    return ClassUtils.isPresent(className, S3TestcontainerExtension.class.getClassLoader());
  }

  private static final class MinioContainerHolder {

    /**
     * MinIO publishes its images on quay.io, so the quay.io mirror is used instead of the docker.io one. The version
     * reflects the minio image used in docker-compose.yml.
     */
    private static final String MINIO_DOCKER_IMAGE = "quayio.docker.bin.sbb.ch/minio/minio:RELEASE.2025-04-08T15-41-24Z";

    /**
     * Must be kept in sync with the buckets of {@code ch.sbb.atlas.s3.config.AmazonBucket}, which cannot be referenced
     * here because atlas-s3 depends on base-atlas.
     */
    private static final List<String> BUCKETS = List.of("export-files", "hearing-documents", "bulk-import");

    private static final String CREDENTIAL = "atlas-integration-test";

    private static final MinIOContainer MINIO_CONTAINER = new MinIOContainer(
        DockerImageName.parse(MINIO_DOCKER_IMAGE).asCompatibleSubstituteFor("minio/minio"))
        .withUserName(CREDENTIAL)
        .withPassword(CREDENTIAL);

    static {
      Startables.deepStart(MINIO_CONTAINER).join();
      createBuckets();
    }

    private static void publishProperties() {
      System.setProperty("s3-testcontainer.endpoint", MINIO_CONTAINER.getS3URL());
      System.setProperty("s3-testcontainer.access-key", MINIO_CONTAINER.getUserName());
      System.setProperty("s3-testcontainer.secret-key", MINIO_CONTAINER.getPassword());
      BUCKETS.forEach(bucket -> System.setProperty("s3-testcontainer." + bucket + ".bucket-name", bucketName(bucket)));
    }

    private static String bucketName(String bucket) {
      return "atlas-" + bucket + "-integration-test";
    }

    private static void createBuckets() {
      try (S3Client s3Client = S3Client.builder()
          .region(Region.of("local"))
          .forcePathStyle(true)
          .endpointOverride(URI.create(MINIO_CONTAINER.getS3URL()))
          .credentialsProvider(StaticCredentialsProvider.create(
              AwsBasicCredentials.create(MINIO_CONTAINER.getUserName(), MINIO_CONTAINER.getPassword())))
          .build()) {
        BUCKETS.forEach(bucket -> s3Client.createBucket(request -> request.bucket(bucketName(bucket))));
      }
    }
  }

}
