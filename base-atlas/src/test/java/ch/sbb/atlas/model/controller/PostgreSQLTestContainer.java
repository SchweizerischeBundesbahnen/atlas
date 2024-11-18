package ch.sbb.atlas.model.controller;

import lombok.experimental.UtilityClass;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * <a
 * href="https://confluence.sbb.ch/display/CLEW/Advanced+Topics#AdvancedTopics-Knownissuesandtroubleshooting">Testcontainers</a>
 * on Kubedock troubleshooting
 */
@UtilityClass
public class PostgreSQLTestContainer {

  private static final String KUBEDOCK_RUNAS_USER_LABEL_NAME = "com.joyrex2001.kubedock.runas-user";
  private static final String KUBEDOCK_RUNAS_USER = "999";

  /**
   * This Postgres Version should reflect the docker-compose.yml and the postgres version used on AWS
   */
  private static final String POSTGRES_DOCKER_IMAGE = "postgres:17.1";

  public static PostgreSQLContainer<?> create() {
    return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_DOCKER_IMAGE))
        .withLabel(KUBEDOCK_RUNAS_USER_LABEL_NAME, KUBEDOCK_RUNAS_USER);
  }

  public static void setSystemPropertiesForDatasource(String prefix, PostgreSQLContainer<?> container) {
    System.setProperty(prefix + ".url", container.getJdbcUrl());
    System.setProperty(prefix + ".username", container.getUsername());
    System.setProperty(prefix + ".password", container.getPassword());
    System.setProperty(prefix + ".driver-class-name", container.getDriverClassName());
  }
}