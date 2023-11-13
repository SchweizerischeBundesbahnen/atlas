package ch.sbb.prm.directory.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.prm.directory.exception.ServicePointDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@IntegrationTest
class SharedServicePointServiceTest {

  private static final String SERVICE_POINT_SLOID = "ch:1:sloid:70000";

  @Autowired
  private SharedServicePointConsumer sharedServicePointConsumer;

  @Autowired
  private SharedServicePointService sharedServicePointService;

  @BeforeEach
  void setUp() {
    sharedServicePointConsumer.readServicePointFromKafka(SharedServicePointVersionModel.builder()
        .servicePointSloid(SERVICE_POINT_SLOID)
        .sboids(Set.of("ch:1:sboid:100001"))
        .trafficPointSloids(Set.of("ch:1:sloid:12345:1"))
        .build());
  }

  @Test
  void shouldFindServicePoint() {
    Optional<SharedServicePointVersionModel> servicePoint = sharedServicePointService.findServicePoint(SERVICE_POINT_SLOID);
    assertThat(servicePoint).isPresent();
  }

  @Test
  void shouldValidateServicePointExists() {
    assertThatNoException().isThrownBy(() -> sharedServicePointService.validateServicePointExists(SERVICE_POINT_SLOID));
  }

  @Test
  void shouldValidateServicePointExistsAndThrowException() {
    assertThatExceptionOfType(ServicePointDoesNotExistException.class).isThrownBy(
        () -> sharedServicePointService.validateServicePointExists("spinatknödel"));
  }
}