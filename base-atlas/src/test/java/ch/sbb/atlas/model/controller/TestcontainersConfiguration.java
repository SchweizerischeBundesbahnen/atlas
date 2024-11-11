package ch.sbb.atlas.model.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * <a
 * href="https://confluence.sbb.ch/display/CLEW/Advanced+Topics#AdvancedTopics-Knownissuesandtroubleshooting">Testcontainers
 * on Kubedock troubleshooting</a>
 */
@TestConfiguration(proxyBeanMethods = false)
@Testcontainers
public class TestcontainersConfiguration {

  public static final String KUBEDOCK_RUNAS_USER_LABEL_NAME = "com.joyrex2001.kubedock.runas-user";

  /**
   * DB container with customization for Kubedock compatibility. How to find the correct user id?
   * <ol>
   *     <li>Start the application locally</li>
   *     <li>learn from <a href="https://hub.docker.com/_/postgres">Postgres Container docs</a> the the default linux username is 'postgres'.</li>
   *     <li>Shell into the Postgres Container</li>
   *     <li>Get the uid of the linux user: <code>id -u postgres</code></li>
   * </ol>
   */
  @Bean
  @ServiceConnection
  public PostgreSQLContainer<?> dbContainer() {
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.4"));
    container.withLabel(KUBEDOCK_RUNAS_USER_LABEL_NAME, "999");
    return container;
  }

}