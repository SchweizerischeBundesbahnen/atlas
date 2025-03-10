package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.scheduling.client.BulkImportServiceBatchClient;
import ch.sbb.scheduling.exception.SchedulingExecutionException;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import feign.Util;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class UpdateGeolocationJobSchedulerServiceTest {

  private UpdateGeolocationJobSchedulerService service;

  @Mock
  private BulkImportServiceBatchClient client;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new UpdateGeolocationJobSchedulerService(client);
  }

  @Test
  void shouldSyncSloidSuccessfully() {
    //given
    Response response = Response.builder()
        .status(204)
        .reason("No Content")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.triggerUpdateGeolocationServicePointJob()).thenReturn(response);

    //when
    Response result = service.updateGeoLocations();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(204);
  }

  @Test
  void shouldSyncSloidUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(client.triggerUpdateGeolocationServicePointJob()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> service.updateGeoLocations());
  }

}