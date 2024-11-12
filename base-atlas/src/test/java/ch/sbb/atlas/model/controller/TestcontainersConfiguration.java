package ch.sbb.atlas.model.controller;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * <a
 * href="https://confluence.sbb.ch/display/CLEW/Advanced+Topics#AdvancedTopics-Knownissuesandtroubleshooting">Testcontainers</a>
 * on Kubedock troubleshooting
 */
public class TestcontainersConfiguration {

  private static final String KUBEDOCK_RUNAS_USER_LABEL_NAME = "com.joyrex2001.kubedock.runas-user";
  private static final String KUBEDOCK_RUNAS_USER = "999";

  @Bean(destroyMethod = "stop")
  @ServiceConnection
  public PostgreSQLContainer<?> postgreSQLContainer() {
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.4"))
        .withLabel(KUBEDOCK_RUNAS_USER_LABEL_NAME, KUBEDOCK_RUNAS_USER);
  }

}