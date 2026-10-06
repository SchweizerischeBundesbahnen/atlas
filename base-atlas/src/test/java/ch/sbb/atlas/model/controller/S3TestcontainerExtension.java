package ch.sbb.atlas.model.controller;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.util.ClassUtils;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * Redirects all S3 traffic of the integration tests to an S3Mock Testcontainer. Without this, the integration-test
 * profile resolves the credentials from Vault and the tests write into the shared DEV buckets.
 * <p>
 * The extension is registered on {@link IntegrationTest}, so every integration test is protected automatically. A
 * JUnit extension is used instead of an imported {@code @TestConfiguration} because the properties have to be
 * published before the Spring context is created, and because an additional {@code @Import} on a
 * {@link BaseControllerApiTest} subclass breaks the auto-configuration lookup of that test class.
 * <p>
 * A module opts in by depending on atlas-s3 and adding
 * {@code testImplementation(libs.s3mock.testcontainers)}. Modules that do not use S3 omit them and no container is
 * started for them. The container therefore lives in the nested {@link S3MockContainerHolder}, which is only
 * initialised once both are present.
 */
public class S3TestcontainerExtension implements BeforeAllCallback {

  private static final String AMAZON_CONFIG_CLASS = "ch.sbb.atlas.s3.config.AmazonConfigProps";
  private static final String S3MOCK_CONTAINER_CLASS = "com.adobe.testing.s3mock.testcontainers.S3MockContainer";

  private static final boolean S3_TESTCONTAINER_SUPPORTED =
      isOnClasspath(AMAZON_CONFIG_CLASS) && isOnClasspath(S3MOCK_CONTAINER_CLASS);

  @Override
  public void beforeAll(ExtensionContext context) {
    if (S3_TESTCONTAINER_SUPPORTED) {
      S3MockContainerHolder.publishProperties();
    }
  }

  private static boolean isOnClasspath(String className) {
    return ClassUtils.isPresent(className, S3TestcontainerExtension.class.getClassLoader());
  }

  private static final class S3MockContainerHolder {

    /**
     * MinIO no longer allows anonymous pulls on quay.io, which made the image unavailable through the SBB registry
     * proxy. S3Mock is published on Docker Hub and is therefore pulled through the docker.io mirror. The version must
     * be kept in sync with {@code libs.s3mock.testcontainers}.
     */
    private static final String S3MOCK_DOCKER_IMAGE = "dockerio.docker.bin.sbb.ch/adobe/s3mock:4.11.0";

    /**
     * Must be kept in sync with the buckets of {@code ch.sbb.atlas.s3.config.AmazonBucket}, which cannot be referenced
     * here because atlas-s3 depends on base-atlas.
     */
    private static final List<String> BUCKETS = List.of("export-files", "hearing-documents", "bulk-import");

    private static final String CREDENTIAL = "atlas-integration-test";

    private static final int S3MOCK_HTTP_PORT = 9090;

    /**
     * The image stores its data in /s3mockroot, which cannot be created by the random non-root user that the CI
     * cluster (OpenShift) runs containers with. S3Mock would exit right after startup.
     */
    private static final String STORE_ROOT = "/tmp/s3mockroot";

    private static S3MockContainer startedContainer;

    /**
     * Starts the container on first use. The started container is only cached after a successful start, so a failure
     * propagates with its original cause instead of being swallowed into a {@link NoClassDefFoundError} on every
     * following test class.
     * <p>
     * The wait strategy of {@code S3MockContainer} uses the Testcontainers default read timeout of one second, which
     * times out on a loaded machine and then fails the whole container start. It is therefore replaced by an
     * equivalent one with a longer read timeout.
     */
    private static synchronized S3MockContainer container() {
      if (startedContainer == null) {
        S3MockContainer container = new S3MockContainer(
            DockerImageName.parse(S3MOCK_DOCKER_IMAGE).asCompatibleSubstituteFor("adobe/s3mock"))
            .withInitialBuckets(String.join(",", BUCKETS.stream().map(S3MockContainerHolder::bucketName).toList()))
            .withEnv("root", STORE_ROOT)
            .waitingFor(Wait.forHttp("/favicon.ico")
                .forPort(S3MOCK_HTTP_PORT)
                .forStatusCode(200)
                .withReadTimeout(Duration.ofSeconds(10)))
            .withStartupAttempts(3);
        container.start();
        startedContainer = container;
      }
      return startedContainer;
    }

    private static void publishProperties() {
      System.setProperty("s3-testcontainer.endpoint", container().getHttpEndpoint());
      System.setProperty("s3-testcontainer.access-key", CREDENTIAL);
      System.setProperty("s3-testcontainer.secret-key", CREDENTIAL);
      BUCKETS.forEach(bucket -> System.setProperty("s3-testcontainer." + bucket + ".bucket-name", bucketName(bucket)));
    }

    private static String bucketName(String bucket) {
      return "atlas-" + bucket + "-integration-test";
    }
  }

}
